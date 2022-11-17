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


            this.highways.get(vars[0]).add(new Highway(vars[0], vars[1], cap, vars[2], -1, -1));
        }
    }

    public void preprocess() {
        this.find_fastest_path(0, 0, null);
        System.out.println(this);
        System.out.println("Prune result:");
        for (List<Highway> city : this.highways) {
            city.removeIf(h -> h.fastest_path() < 0);
        }
//        System.out.println(this);
    }

    public void process() {
        BitSet visited = new BitSet(this.cities*this.time);
        for (int t = 0; t < this.time; t++) {
            visited.set(t*this.time);
        }

        int added = 0;
        for (int c = 0; c < this.cities; c++) {
            for (int t = 0; t < this.time; t++) {
                if (visited.get(c+t*this.time)) {
                    for (Highway h : this.highways.get(c)) {
                        if (this.time >= t + h.min_dist_to_dest()) {
                            this.flow.get(c+t*this.time).add(h);
                            visited.set(h.to()+(t+h.length())*this.time);
                            added++;
                        }
                    }
                }
            }
        }

        System.out.println(visited);
        System.out.println("added: " + added);
        for (int c = 0; c < this.cities; c++) {
            for (int t = 0; t < this.time; t++) {
                if (visited.get(c+t*this.time)) {
                    System.out.println(c + "_" + t + "\t" + this.flow.get(c+t*this.time));
                }
            }
        }
    }

    private int find_fastest_path(int source, int length, Highway pred) {
        if (length > this.time) {
            return -1;
        }
        if (source == cities - 1) {
            if (pred != null) {
                if (pred.min_dist_to_dest() == -1) {
                    pred.set_min_dist_to_dest(pred.length());
                } else {
                    pred.set_min_dist_to_dest(Math.min(pred.min_dist_to_dest(), pred.length()));
                }
            }
            return length;
        }
        int min_result = -1;
        for (Highway highway : this.highways.get(source)) {
            int result = find_fastest_path(highway.to(), length + highway.length(), highway);

            if (min_result < 0 || (result < min_result && result > 0)) {
                min_result = result;
            }

            if (result > 0 && (highway.fastest_path() < 0 || highway.fastest_path() > result)) {
                highway.set_fastest_path(result);
            }
        }

        int min_dest_to_source_branch = this.highways.get(source).stream().
                mapToInt(Highway::min_dist_to_dest)
                .min()
                .orElse(-1);

        if (min_dest_to_source_branch <= 0) {
            return min_result;
        }

        if (pred != null) {
            if (pred.min_dist_to_dest() == -1) {
                pred.set_min_dist_to_dest(pred.length() + min_dest_to_source_branch);
            } else {
                pred.set_min_dist_to_dest(Math.min(pred.min_dist_to_dest(), pred.length()) + min_dest_to_source_branch );
            }
        }
        return min_result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cities: ");
        sb.append(cities);
        sb.append("\nHighways: ");
        sb.append(highways.size());
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
