package Restocking;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private int cities;
    private final int time;
    private final Map<String, Integer> highwaysMap;
    private final ArrayList<LinkedList<Highway>> highways;

    public Graph(int cities, int time) {
        this.cities = cities;
        this.highwaysMap = new HashMap<>();

        this.highways = new ArrayList<>(this.cities);
        for (int i = 0; i < cities; i++) {
            highways.add(new LinkedList<Highway>());
        }

        this.time = time;
    }

    public void makeHighway(String input) {
        String[] variables = input.split(" ");
        String key =  variables[0] + ';' + variables[1] + ';' + variables[2];
        int[] vars = Arrays.stream(variables).mapToInt(Integer::parseInt).toArray();
        int cap = vars[3];
        if (highwaysMap.containsKey(key)) {
            highwaysMap.put(key, highwaysMap.get(key) + cap);
            this.highways.get(vars[0]).forEach(h -> {
                if (h.to() == vars[1] && h.length() == vars[2]) {
                    h.add_capacity(cap);
                }
            });
        } else {
            highwaysMap.put(key, cap);
            this.highways.get(vars[0]).add(new Highway(vars[0], vars[1], cap, vars[2], -1, -1));
        }
    }

    public void prune() {
        this.BFS(0, 0);

        System.out.println(this);
        System.out.println("Prune result:");
        for (LinkedList<Highway> city : this.highways) {
            city.removeIf(h -> h.fastest_path() < 0);
        }
        System.out.println(this);
    }
    private int BFS(int source, int length) {
        if (length > this.time) {
            return -1;
        }
//        System.out.println(source + " == " + (cities -1));
        if (source == cities - 1) {
            return length;
        }
        int min_result = -1;

        for (Highway highway : this.highways.get(source)) {

            int result = BFS(highway.to(), length + highway.length());

            if (min_result < 0 || (result < min_result && result > 0)) {
                min_result = result;
            }

            if (result > 0 && (highway.fastest_path() < 0 || highway.fastest_path() > result)) {
                highway.set_fastest_path(result);
            }
//            System.out.println(highway);

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
        highways.forEach(h -> sb.append("\n").append(h.toString()));

        return sb.toString();
    }
}
