package Restocking;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private List<City> cities;
    private int length;
    private int capacity;

    public Path() {
        this.cities = new LinkedList<>();
        this.length = 0;
        this.capacity = 1000000;
    }

    public Path(Path p) {
        this.cities = p.getCities();
        this.length = p.getLength();
        this.capacity = p.getCapacity();
    }

    public void add(City c) {
        this.cities.add(c);
    }

    public void addLength(int len) {
        this.length = this.length + len;
    }

    //
    // Getters/Setters
    //

    // Cities
    public List<City> getCities() {
        return this.cities;
    }

    public void setCities(List<City> cs) {
        this.cities = cs;
    }

    // Source
    public City getSource() {
        return this.cities.get(0);
    }

    public void setSource(City source) {
        this.cities.set(0, source);
    }

    // Target
    public City getTarget() {
        return this.cities.get(cities.size()-1);
    }

    public void setTarget(City target) {
        this.cities.set(cities.size()-1, target);
    }

    // Length
    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    // Capacity
    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < this.capacity) {
            this.capacity = capacity;
        }
    }

    //
    // String representation
    //

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSource().getID());
        for (int i = 1; i < cities.size(); i++) {
            sb.append(" -> ");
            sb.append(cities.get(i).getID());
        }
        sb.append(" | Length: ");
        sb.append(length);
        sb.append(" | Capacity: ");
        sb.append(capacity);

        return sb.toString();
    }
}
