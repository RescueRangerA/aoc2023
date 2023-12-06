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

    @Override
    public int compareTo(Interval other) {
        return Comparator
                .comparingLong(Interval::getStart)
                .thenComparingLong(Interval::getEnd)
                .compare(this, other);
    }
}
