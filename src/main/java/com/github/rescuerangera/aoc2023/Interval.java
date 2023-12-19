package com.github.rescuerangera.aoc2023;

import java.util.Comparator;

public class Interval implements Comparable<Interval> {
    long start, end;

    public Interval(long start, long end) {
        if ( start > end ) {
            throw new IllegalArgumentException();
        }

        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getLength() {
        return end - start;
    }

    @Override
    public int compareTo(Interval other) {
        return Comparator
                .comparingLong(Interval::getStart)
                .thenComparingLong(Interval::getEnd)
                .compare(this, other);
    }

    public static Interval intersection(Interval a, Interval b) {
        Interval left;
        Interval right;

        int compare = a.compareTo(b);

        if ( compare > 0 ) {
            left = b;
            right = a;
        } else if (compare < 0) {
            left = a;
            right = b;
        } else {
            return a;
        }

        if ( left.getEnd() < right.getStart() ) {
            return null;
        }

        if ( right.getEnd() <= left.getEnd() ) {
            return right;
        } else {
            return new Interval(right.getStart(), left.getEnd());
        }
    }
}
