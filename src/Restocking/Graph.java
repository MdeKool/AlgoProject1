package Restocking;

import java.util.*;

public class Graph {
    private final int cities;
    private final int time;
    private final int MAX_VALUE = 50*1000;
    private final Map<String, Integer> highwaysMap;
    private final List<List<Highway>> highways;
    private final List<List<Highway>> flow;

    public Graph(int cities, int time) {
        this.cities = cities;
        this.highwaysMap = new HashMap<>();
        this.highways = new ArrayList<>(cities);
        this.flow = new ArrayList<>(cities*time);
        for (int c = 0; c < cities; c++) {
            LinkedList<Highway> outgoing = new LinkedList<>();
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

    public void preprocess() {
        this.find_paths(0, 0, new Highway(0, 0, 0, 0, MAX_VALUE, MAX_VALUE));
        for (List<Highway> city : this.highways) {
            city.removeIf(h -> h.min_dist_to_dest() < 0 || h.fastest_path() > this.time);
        }
        System.out.println("Prune result:");
        System.out.println(this);
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
                            this.flow.get(c+t*this.cities).add(h);
                            visited.set(h.to()+(t+h.length())*this.cities);
                            added++;
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

    private int find_paths(int source, int length, Highway pred) {
        if (length > this.time) { // no path
            return -1;
        }

        if (source == cities - 1) {
            pred.set_min_dist_to_dest(pred.length());
            pred.set_fastest_path(Math.min(pred.fastest_path() ,length));
            return pred.length();
        }

        int shortest_child_path = MAX_VALUE;
        int fastest_path = MAX_VALUE;
        for (Highway highway : this.highways.get(source)) {

            // discover
            int result;
            if ( highway.min_dist_to_dest() >= MAX_VALUE
                    || highway.fastest_path() - highway.min_dist_to_dest() > length) {
                result = find_paths(highway.to(), length + highway.length(), highway);
            } else {
                result = highway.min_dist_to_dest();
            }

            if (result < 0) continue; // ignore path with no solution

            shortest_child_path = Math.min(shortest_child_path, result);
            fastest_path = (Math.min(fastest_path, highway.fastest_path()));
        }

        if (shortest_child_path > 0 ) {
            pred.set_min_dist_to_dest(shortest_child_path + pred.length());
        }
        pred.set_fastest_path(fastest_path);
        return shortest_child_path + pred.length();
    }

    public int edmondsKarp() {
        int s = 0;
        int t = cities - 1;
        int flow = 0;

        Queue<Integer> q = new LinkedList<>();
        q.add(s);

        Highway[] pred = new Highway[this.flow.size()];

        while(!q.isEmpty()) {
            Integer cur = q.poll();
            for (Highway h : this.flow.get(cur));
        }

        return -1;
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
