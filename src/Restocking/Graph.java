package Restocking;

import java.util.*;

public class Graph {
    private final int cities;
    private final int time;
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
        if (highwaysMap.containsKey(key)) {
            //highwaysMap.put(key, highwaysMap.get(key) + cap);
            this.highways.get(vars[0]).forEach(h -> { // TODO improve using i.e. binary-search
                if (h.to() == vars[1] && h.length() == vars[2]) {
                    h.add_capacity(cap);
                }
            });
        } else {
            highwaysMap.put(key, cap);

            // Binary-insert
            this.highways.get(vars[0]).add(new Highway(vars[0], vars[1], cap, vars[2], Integer.MAX_VALUE));
        }
    }

    public void preprocess() {
        this.find_paths(0, 0, null);
        for (List<Highway> city : this.highways) {
            city.removeIf(h -> h.min_dist_to_dest() < 0);
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
            pred.set_min_dist_to_dest(-1);
            return -1;
        }

        if (source == cities - 1 && pred != null) {
            pred.set_min_dist_to_dest(pred.length());
            return pred.length();
        }

        int shortest_child_path = Integer.MAX_VALUE;
        for (Highway highway : this.highways.get(source)) {
            if (highway.min_dist_to_dest() < 0) {
                // no solution
                continue;
            }

            if (highway.min_dist_to_dest() <= this.time) {
                // already discovered
                shortest_child_path = Math.min(shortest_child_path, highway.min_dist_to_dest());
                continue;
            }

            // discover
            int result = find_paths(highway.to(), length + highway.length(), highway);
            if (result < 0) continue; // ignore path with no solution

            shortest_child_path = Math.min(shortest_child_path, result);
        }

        if (shortest_child_path > this.time) {
            // no path was found
            if (pred != null)
                pred.set_min_dist_to_dest(-1);
            return -1;
        }

        if (pred != null) {
            pred.set_min_dist_to_dest(shortest_child_path + pred.length());
            return shortest_child_path + pred.length();
        }
        return shortest_child_path;
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
