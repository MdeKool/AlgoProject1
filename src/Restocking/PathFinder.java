package Restocking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PathFinder {
    private final List<City> cities;
    private final int time;
    private List<Path> paths;
    private List<Path> path_parts;


    public PathFinder(Graph g) {
        cities = g.getCities();
        time = g.getTime();

        paths = new ArrayList<>();
        path_parts = new ArrayList<>();
    }

    public void run() {
        // Initialize paths
        Path p0 = new Path();
        p0.add(cities.get(0));
        path_parts.add(p0);

        for (int i = 0; i < path_parts.size(); i++) {
            Path p = path_parts.get(i);
            City tc = p.getTarget();

            // If target city of a path is not the final destination, then make paths for all outgoing highways
            if (!tc.equals(cities.get(cities.size()-1))) {
                for (Highway h : tc.getOutgoing()) {
                    Path np = new Path();
                    // Java copies Lists by reference, which is space-efficient, but not efficient for my brain.
                    for (City c : p.getCities()) {
                        np.add(c);
                    }
                    np.setLength(p.getLength());
                    np.setCapacity(p.getCapacity());


                    // Add information of the highways
                    np.add(h.getTarget());
                    np.addLength(h.getLength());
                    np.setCapacity(h.getCapacity());
                    if (np.getLength() <= time) {
                        path_parts.add(np);
                    }
                }
            } else {
                paths.add(p);
            }
        }

        System.out.println("Paths:");
        for (Path p : paths) {
            System.out.println(p.toString());
        }
    }
}
