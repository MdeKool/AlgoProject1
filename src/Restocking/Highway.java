package Restocking;

import java.util.Objects;

public final class Highway {
    private final int from;
    private final int to;
    private int capacity;
    private final int length;
    private int fastest_path;
    private int min_dist_to_dest;

    public Highway(int from, int to, int capacity, int length, int fastest_path, int min_dist_to_dest) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.length = length;
        this.fastest_path = fastest_path;
        this.min_dist_to_dest = min_dist_to_dest;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public int capacity() {
        return capacity;
    }

    public void add_capacity(int capacity) { this.capacity += capacity; }

    public int length() {
        return length;
    }

    public int fastest_path() {
        return fastest_path;
    }

    public void set_fastest_path(int t) {
        this.fastest_path = t;
    }

    public int min_dist_to_dest() { return min_dist_to_dest; }

    public void set_min_dist_to_dest(int min) { this.min_dist_to_dest = min; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Highway) obj;
        return this.from == that.from &&
                this.to == that.to &&
                this.capacity == that.capacity &&
                this.length == that.length &&
                this.fastest_path == that.fastest_path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, capacity, length, fastest_path);
    }

    @Override
    public String toString() {
        return "Highway[" +
                "from=" + from + ", " +
                "to=" + to + ", " +
                "capacity=" + capacity + ", " +
                "length=" + length + ", " +
                "min_dist=" + min_dist_to_dest + ", " +
                "fastest_path=" + fastest_path + ']';
    }
}
