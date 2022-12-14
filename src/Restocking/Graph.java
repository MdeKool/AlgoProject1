package Restocking;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final int cities;
    private final int time;
    private final int MAX_VALUE = 50*1000;
    private final Map<String, Integer> highwaysMap;
    private final List<List<Highway>> highways;
    private final List<List<Highway>> flow;
    private int shortest_path = MAX_VALUE;

    public Graph(int cities, int time) {
        this.cities = cities;
        this.highwaysMap = new HashMap<>();
        this.highways = new ArrayList<>(cities);
        this.flow = new ArrayList<>(cities*time);
        for (int c = 0; c < cities; c++) {
            List<Highway> outgoing = new LinkedList<>();
            highways.add(outgoing);
            for (int t = 0; t <= time; t++) {
                flow.add(new LinkedList<>());
            }
        }
        this.time = time;
    }

    public void makeHighway(String input) {
        String[] variables = input.split(" ");
        String key =  variables[0] + ';' + variables[1] + ';' + variables[2];
        int[] vars = Arrays.stream(variables).mapToInt(Integer::parseInt).toArray();
        int cap = vars[3];
        if (cap < 1) {
            return; // do not add useless highway.
        }
        if (highwaysMap.containsKey(key)) {
            //highwaysMap.put(key, highwaysMap.get(key) + cap);
            this.highways.get(vars[0]).forEach(h -> { // TODO improve using i.e. binary-search
                if (h.to() == vars[1] && h.length() == vars[2]) {
                    h.add_capacity(cap);
                }
            });
        } else {
            highwaysMap.put(key, cap);
            this.highways.get(vars[0]).add(new Highway(vars[0], vars[1], cap, vars[2], MAX_VALUE, MAX_VALUE));
        }
    }

    public int run() {
        this.preprocess();
        return this.dinic();
    }

    public void preprocess() {
        long st = System.nanoTime();
        this.find_paths(0, 0, new Highway(0, 0, 0, 0, MAX_VALUE, MAX_VALUE));
        for (List<Highway> city : this.highways) {
            city.removeIf(h -> h.min_dist_to_dest() < 0 || h.fastest_path() > this.time);
        }
        long et = System.nanoTime();

        System.out.println("Preprocess(): " + (et - st)/1000000 + "ms");
//        System.out.println("Prune result:");
//        System.out.println(this);

//        long st = System.nanoTime();
//
//        System.out.println("Prune result:");
//        System.out.println(this);
//
//        long et = System.nanoTime();
//        System.out.println("preprocess(): " + (et - st)/1000000 + " ms");
//
//        create_time_expanded_graph();
    }

    public void process() {
        BitSet visited = new BitSet(this.cities*this.time);
        for (int t = 0; t < this.time; t++) {
            visited.set(t*this.cities);
        }
        int added = 0;
        for (int c = 0; c < this.cities; c++) {
            for (int t = 0; t < this.time; t++) {
                if (visited.get(c+t*this.cities)) {
                    for (Highway h : this.highways.get(c)) {
                        if (this.time >= t + h.min_dist_to_dest()) {
                            List<Highway> lh = this.flow.get(c+t*this.cities);
                            Highway nh = new Highway(c+t*this.cities,
                                    h.to() + (t + h.length()) * this.cities,
                                    h.capacity(),
                                    h.length(),
                                    h.min_dist_to_dest(),
                                    h.fastest_path());
                            lh.add(nh);
                            visited.set(h.to()+(t+h.length())*this.cities);
                            added++;
                        }
                    }
                }
            }
        }

//        System.out.println("added: " + added);
//        for (int c = 0; c < this.cities; c++) {
//            for (int t = 0; t < this.time; t++) {
//                if (visited.get(c+t*this.cities) && this.flow.get(c+t*this.cities).size() > 0) {
//                    System.out.println(c + "_" + t + "\t" + this.flow.get(c+t*this.cities));
//                }
//            }
//        }
    }

    private int find_paths(int source, int length, Highway pred) {
        if (length > this.time) // no path with length < time
            return -1;

        if (source == cities - 1) { // destination reached, update accordingly
            pred.set_min_dist_to_dest(pred.length());
            pred.set_fastest_path(Math.min(pred.fastest_path(), length));
            this.shortest_path = Math.min(this.shortest_path, pred.fastest_path());
            return pred.length();
        }

        int shortest_child_path = MAX_VALUE;
        int fastest_path = MAX_VALUE;
        for (Highway highway : this.highways.get(source))
        { // for each highway leaving city[source]
            int result;
            // if a search that is minimally as deep as we can search has already been done, re-use that result
            if ( highway.min_dist_to_dest() >= MAX_VALUE
                    || highway.fastest_path() - highway.min_dist_to_dest() > length) {
                result = find_paths(highway.to(), length + highway.length(), highway);
            } else { // otherwise: search
                result = highway.min_dist_to_dest();
            }
            if (result < 0) continue; // ignore path with no solution

            // minimize on result
            shortest_child_path = Math.min(shortest_child_path, result);
            fastest_path = (Math.min(fastest_path, highway.fastest_path()));
        }

        if (shortest_child_path > 0 ) {
            pred.set_min_dist_to_dest(shortest_child_path + pred.length());
        } else { // negative weight cycle detected
            System.out.println(source + " has a negative shortest path, negative weight cycle?");
        }
        pred.set_fastest_path(fastest_path);
        return shortest_child_path + pred.length();
    }

    public int dinic() {
        List<List<Integer>> level_graph = new LinkedList<>();
        List<List<Integer>> paths = new LinkedList<>();
        List<Integer> level_0 = new LinkedList<>();
        for ( int t = 0; t <= this.time; t++ ) {
            level_0.add(t*this.cities);
        }
        level_graph.add(level_0.stream()
                .filter(x -> this.flow.get(x).size() > 0)
                .toList());

        for ( Integer i : level_graph.get(0) ) {
            for ( Integer j : this.flow.get(i).stream().map(Highway::from).toList() ) {
                List<Integer> p = new LinkedList<>();
                p.add(j);
                paths.add(p);
            }
        }

        // Make level graph (i.e, graph of all shortest paths)
        for ( int i = 0; level_graph.get(i).size() != 0; ++i ) {
            List<Integer> cur_level = level_graph.get(i);
            List<Integer> next_level = new LinkedList<>();
            for ( Integer node : cur_level ) {  // Add dist + 1 nodes
                next_level.addAll(this.flow.get(node).stream()
                        .map(Highway::to)
                        .toList());
            }
            if (next_level.stream()
                    .noneMatch(x -> x % this.cities == this.cities - 1)) {
                level_graph.add(next_level);
            } else {
                level_graph.add(next_level.stream()
                        .filter(x -> x % this.cities == this.cities - 1)
                        .toList());
                break;
            }
        }

        // Fill each path
        int depth = level_graph.size();
        for ( List<Integer> path : paths ){
            recursive_dinic(level_graph, path, depth);
        }

        return 0 + dinic();
    }

    private void recursive_dinic(List<List<Integer>> level_graph, List<Integer> path, int depth) {
        if (path.size() != depth) {
            for ( Highway h : this.flow.get(path.get(path.size() - 1 ))) {
                if ( h.flow() < h.capacity() ) {
                    path.add(h.to());
                    break;
                }
            }
            recursive_dinic(level_graph, path, depth);
        }

        //return 0;
    }

    private void create_time_expanded_graph() {
        BitSet visited = new BitSet(this.cities*this.time);
        for (int t = 0; t < this.time; t++) {
            visited.set(t*this.cities);
        }

        int added = 0;
        for (int c = 0; c < this.cities; c++)
        { // for each city
            for (int t = 0; t < this.time - this.shortest_path; t++)
            { // for each possible time-frame (do not run time for which no paths exist.
                if (visited.get(c+t*this.cities))
                { // if city_time 'c_t' has been visited
                    for (Highway h : this.highways.get(c))
                    { // for each outgoing highway
                        if (this.time >= t + h.min_dist_to_dest())
                        { // if destination can still be reached using this highway
                            // add highway from city_time to dest_time+length
                            this.flow.get(c+t*this.cities).add(
                                    new Highway(c+t*this.cities,
                                        h.to() + (t+h.length())*this.time,
                                        h.capacity(), h.length(),
                                        h.min_dist_to_dest(), h.fastest_path()
                                    )
                            ); added++;
                            // mark dest_time+length as visited
                            visited.set(h.to()+(t+h.length())*this.cities);
                        }
                    }
                }
            }
        }

        System.out.println("added: " + added);
        for (int c = 0; c < this.cities; c++) {
            for (int t = 0; t < this.time; t++) {
                if (visited.get(c+t*this.cities) && this.flow.get(c+t*this.cities).size() > 0) {
                    System.out.println(c + "_" + t + "\t" + this.flow.get(c+t*this.cities));
                }
            }
        }
    }

    public int edmondsKarp() {
        long st = System.nanoTime();

        int[] sources = new int[this.time+1];
        Arrays.setAll(sources, p -> p * this.cities);
        int[] sinks = new int[this.time+1];
        Arrays.setAll(sinks, p -> (p+1) * this.cities - 1);

        ArrayList<Integer>[] pred = new ArrayList[this.flow.size()];

        Stack<Integer> q = new Stack<>();

        for (int i = 0; this.flow.get(sources[i]).size() != 0; i++) {
            q.push(sources[i]);

            while(!q.isEmpty()) {
                Integer cur = q.pop();

                for (Highway h : this.flow.get(cur)) {
                    if (h.to() < this.flow.size() && h.to() % this.cities != 0 && h.capacity() > h.flow()) {
                        if (pred[h.to()] == null) {
                            pred[h.to()] = new ArrayList<>();
                        }
                        pred[h.to()].add(h.from());
                        q.push(h.to());
                    }
                }
            }
        }

        for (int sink : sinks) {
            int t_sink = sink;
            if (pred[sink] != null) {
                List<Integer> path = new LinkedList<>();
                this.ek(path, sink, pred);
            }
        }

        long et = System.nanoTime();

        System.out.println("edmondsKarp(): " + (et - st)/1000000 + "ms");

        return this.flow.stream()
                .mapToInt(lh -> lh.stream()
                        .filter(x -> x.to() % this.cities == this.cities-1)
                        .mapToInt(Highway::flow)
                        .sum())
                .sum();
    }

    private void ek(List<Integer> path, int p, List<Integer>[] pred) {
        path.add(0, p);
        if ( pred[p] != null ) {
            for (Integer x : pred[p]) {
                this.ek(path, x, pred);
            }
        } else {
            int df = Integer.MAX_VALUE;
            for ( int i = 0; i < path.size() - 1; i++) {
                for ( Highway h : this.flow.get(path.get(i)) ) {
                    if ( h.to() == path.get(i+1) ) {
                        df = Math.min(df, h.capacity() - h.flow());
                    }
                }
            }
            for ( int i = 0; i < path.size() - 1; i++) {
                for ( Highway h : this.flow.get(path.get(i)) ) {
                    if ( h.to() == path.get(i+1) ) {
                        h.addFlow(df);
                    }
                }
            }

        }
        path.remove(0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cities: ");
        sb.append(cities);
        sb.append("\nHighways: ");
        sb.append(highways.stream().map(List::size).reduce(0, Integer::sum));
        sb.append("\nTime: ");
        sb.append(time);
        sb.append("\n");
        for (int i = 0; i < this.cities; i++) {
            List<Highway> c = highways.get(i);
            sb.append("\nCity ").append(i).append(" edges=").append(c.size()).append(": ").append(c);
        }

        return sb.toString();
    }
}
