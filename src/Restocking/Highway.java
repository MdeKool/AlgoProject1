package Restocking;

import java.util.LinkedList;
import java.util.List;

public class Highway {
    private City origin;
    private City target;
    private int length;
    private int capacity;
    private List<Truck> fill;

    public Highway(City org, City tgt, int len, int cap) {
        origin = org;
        target = tgt;
        length = len;
        capacity = cap;
        fill = new LinkedList<>();
    }

    public void addTruck(Truck t) {
        fill.add(t);
    }

    public Truck removeTruck() {
        return fill.remove(0);
    }

    public String toString() {
        return "Highway from " +
                origin.getID() +
                " to " +
                target.getID() +
                " of length " +
                length +
                " and capacity " +
                capacity;
    }
}
