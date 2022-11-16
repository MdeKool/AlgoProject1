package Restocking;

import java.util.LinkedList;
import java.util.List;

public class Highway {
    private City origin;
    private City target;
    private final int length;
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

    public void addCapacity(Integer cap) {
        capacity += cap;
    }

    public City getOrigin() {
        return origin;
    }

    public void setOrigin(City origin) {
        this.origin = origin;
    }

    public City getTarget() {
        return target;
    }

    public void setTarget(City target) {
        this.target = target;
    }

    public int getLength() {
        return length;
    }

    public int getCapacity() {
        return capacity;
    }

    public String toString() {
        return "Highway from " +
                origin.getID() +
                " to " +
                target.getID() +
                " of length " +
                length +
                " and capacity " +
                capacity + "\n";
    }
}
