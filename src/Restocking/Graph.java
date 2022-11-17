package Restocking;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private int cities;
    private final int time;
    private final Map<String, Integer> highwaysMap;
    private List<Highway> highways;
    private final LinkedList[] adjacency;

    public Graph(int cities, int time) {
        this.cities = cities;

        this.highwaysMap = new HashMap<>();
        this.highways = new ArrayList<>();

        this.adjacency = new LinkedList[cities];
        for (int i = 0; i < cities; i++) {
            adjacency[i] = new LinkedList();
        }

        this.time = time;
    }

    public void makeHighway(String input) {
        String[] variables = input.split(" ");
        String key =  variables[0] + ';' + variables[1] + ';' + variables[2];
        int cap = Integer.parseInt(variables[3]);
        if (highwaysMap.containsKey(key)) {
            highwaysMap.put(key, highwaysMap.get(key) + cap);
        } else {
            highwaysMap.put(key, cap);
            adjacency[Integer.parseInt(variables[0])].add(variables[1] + ';' + variables[2]);
        }
    }

    public void prune() {

        highwaysMap.forEach((key1, value) -> {
            Object[] key = Arrays.stream(key1.split(";")).map(Integer::parseInt).toArray();
            highways.add(new Highway((int) key[0], (int) key[1], value, (int) key[2], -1, -1));
        });

        this.BFS(0, 0);

        System.out.println(this);
        System.out.println("Prune result:");
        this.highways.removeIf(h -> h.fastest_path() < 0);
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
        String adj;
        String[] adj1;
        int edge_length, neighbour;
//        System.out.println(adjacency[source]);
        for (Object o : adjacency[source]) {
            adj = (String) o;
            adj1 = adj.split(";");
            neighbour = Integer.parseInt(adj1[0]);
            edge_length = Integer.parseInt(adj1[1]);

//            System.out.println("BFS: " + source + " to " + neighbour + ";" + (length + edge_length));
            int result = BFS(neighbour, length + edge_length);
//            System.out.println("BFS: " + source + " to " + neighbour + ";" + (length + edge_length) + "->" + result);

//            System.out.println(min_result + "|" + result);
            if (min_result < 0 || (result < min_result && result > 0)) {
                min_result = result;
            }
//            System.out.println(min_result);

            Highway highway = null;
//            System.out.println("search " + source + "->" + neighbour);
            for (Highway h : highways) {
                if (h.from() == source && h.to() == neighbour) {
                    highway = h;
                }
            }
            assert highway != null;
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
