package Restocking;

import java.util.LinkedList;
import java.util.List;

public class City {
    private int ID;
    private List<Highway> incoming;
    private List<Highway> outgoing;
    private List<Truck> in_city;

    public City(int id) {
        ID = id;
        incoming = new LinkedList<>();
        outgoing = new LinkedList<>();
        in_city = new LinkedList<>();
    }
    public void addIncoming(Highway hwy) {
        incoming.add(hwy);
    }

    public void addOutgoing(Highway hwy) {
        outgoing.add(hwy);
    }

    public void enter(Truck t) {
        in_city.add(t);
    }

    public Truck leave(Truck t) {
        return in_city.remove(0);
    }

    public List<Highway> getIncoming() {
        return incoming;
    }

    public List<Highway> getOutgoing() {
        return outgoing;
    }

    public int getID() {
        return ID;
    }
}
