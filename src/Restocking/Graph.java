package Restocking;

import java.util.LinkedList;
import java.util.List;

public class Graph {

    private List<City> cities;
    private List<Highway> highways;
    private int time;

    public Graph(int cit, int tim) {
        cities = new LinkedList<>();
        for (int i = 0; i < cit; i++) {
            cities.add(new City());
        }
        highways = new LinkedList<>();
        time = tim;
    }

    public void makeHighway(int cit1, int cit2, int len, int cap) {
        Highway hwy = new Highway(cities.get(cit1), cities.get(cit2), len, cap);
        highways.add(hwy);
        cities.get(cit1).addOutgoing(hwy);
        cities.get(cit2).addIncoming(hwy);
    }
}
