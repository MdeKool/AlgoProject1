package Restocking;

import java.util.Objects;

public final class Highway {
    private final int from;
    private final int to;
    private int capacity;
    private final int length;
    private int min_dist_to_dest;
    private int fastest_path;
    private int flow;

    public Highway(int from, int to, int capacity, int length, int min_dist_to_dest, int fastest_path) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.length = length;
        this.min_dist_to_dest = min_dist_to_dest;
        this.fastest_path = fastest_path;
        this.flow = 0;
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

    public int min_dist_to_dest() { return min_dist_to_dest; }

    public void set_min_dist_to_dest(int min) { this.min_dist_to_dest = min; }

    public int fastest_path() {
        return this.fastest_path;
    }

    public void set_fastest_path(int length) {
        this.fastest_path = length;
    }

    public int flow() {
        return this.flow;
    }

    public int addFlow(int f) {
        this.flow += Math.min(this.capacity, f);
        return Math.max(0, f - this.flow);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Highway) obj;
        return this.from == that.from &&
                this.to == that.to &&
                this.capacity == that.capacity &&
                this.length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, capacity, length);
    }

    @Override
    public String toString() {
        return "\n\tHighway[" +
                "from=" + from + ", " +
                "to=" + to + ", " +
                "capacity=" + capacity + ", " +
                "length=" + length + ", " +
                "min_dist=" + min_dist_to_dest + ']';
    }
}
