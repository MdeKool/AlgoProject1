package Restocking;

import java.util.*;

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
            for (int t = 0; t < time; t++) {
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
        return this.edmondsKarp();
    }

    private void preprocess() {
        this.find_paths(0, 0, new Highway(0, 0, 0, 0, MAX_VALUE, MAX_VALUE));
        for (List<Highway> city : this.highways) { // removed legacy check:  h.min_dist_to_dest() < 0 ||
            city.removeIf(h -> h.min_dist_to_dest() > this.time || h.fastest_path() > this.time);
        }
        System.out.println("Prune result:");
        System.out.println(this);
        create_time_expanded_graph();
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

    private int edmondsKarp() {
        int max_flow = 0;

        int[] sources = new int[this.time];
        Arrays.setAll(sources, p -> p * this.time);
        int[] sinks = new int[this.time];
        Arrays.setAll(sinks, p -> (p+1) * this.time - 1);

        LinkedList<Integer>[] pred = new LinkedList[this.flow.size()];

        Stack<Integer> q = new Stack<>();

        for (int source : sources) {
            q.push(source);

            while(!q.isEmpty()) {
                Integer cur = q.pop();

                for (Highway h : this.flow.get(cur)) {
                    if (h.to() < this.time*this.cities && h.to() % this.cities != 0 && h.capacity() > h.flow()) {
                        if (pred[h.to()] == null) {
                            pred[h.to()] = new LinkedList<>();
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
                int df = Integer.MAX_VALUE;

                q.addAll(pred[sink]);

                while(!q.isEmpty()) {
                    Integer cur = q.pop();

                    List<Highway> lh = this.flow.get(cur);
                    for ( int i = 0; i < lh.size(); i++ ) {
                        if ( lh.get(i).to() != t_sink ) {
                            lh.remove(lh.get(i));
                            i--;
                        }
                    }

                    df = Math.min(df, lh.stream().mapToInt(Highway::capacity).sum());

                    if (pred[cur] != null) {
                        q.addAll(pred[cur]);
                        t_sink = cur;
                    } else {

                        t_sink = sink;
                    }
                }



            }
        }

        return max_flow;
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
