package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day24 extends Day {

    private static class Line {
        private final double a;

        private final double b;

        private final double c;

        private final boolean xMovesForward;

        private final double px;

        private final double py;

        private final double pz;


        public Line(long px, long py, long pz, long vx, long vy, long vz) {
            a = vy;
            b = -vx;
            c = -(py - ((double) px / vx * vy)) * b;

            xMovesForward = vx > 0;

            this.px = px;
            this.py = py;
            this.pz = pz;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }

        public double getC() {
            return c;
        }

        public boolean isxMovesForward() {
            return xMovesForward;
        }

        public double getPx() {
            return px;
        }

        public double getPy() {
            return py;
        }

        public double getPz() {
            return pz;
        }
    }

    private Set<Line> parseLines(BufferedReader reader) {
        return reader
                .lines()
                .map(line ->
                        Arrays.stream(line.split("@"))
                                .map(s ->
                                        Arrays.stream(s.split(", "))
                                                .map(String::trim)
                                                .map(Long::valueOf)
                                                .toList()
                                ).toList()
                )
                .map(split -> new Line(
                        split.get(0).get(0),
                        split.get(0).get(1),
                        split.get(0).get(2),
                        split.get(1).get(0),
                        split.get(1).get(1),
                        split.get(1).get(2)
                ))
                .collect(Collectors.toSet());
    }

    private Pair<Double, Double> calculateIntersection(Line a, Line b) {
        return new Pair<>(
                (a.getB() * b.getC() - b.getB() * a.getC()) / (a.getA() * b.getB() - b.getA() * a.getB()),
                (a.getC() * b.getA() - b.getC() * a.getA()) / (a.getA() * b.getB() - b.getA() * a.getB())
        );
    }

    private boolean isInterceptionInBound(Pair<Double, Double> intersection) {
        final double min = 200000000000000L;
        final double max = 400000000000000L;

        return min < intersection.getFirst() && min < intersection.getSecond() && intersection.getFirst() < max && intersection.getSecond() < max;
    }

    private boolean isInterceptionPossible(Line a, Line b) {
        Pair<Double, Double> intersection = calculateIntersection(a, b);

        if (!isInterceptionInBound(intersection)) {
            return false;
        }

        return isInterceptionPossible(a, intersection) && isInterceptionPossible(b, intersection);
    }

    private boolean isInterceptionPossible(Line a, Pair<Double, Double> intersection) {
        if (a.isxMovesForward()) {
            return a.getPx() < intersection.getFirst();
        } else {
            return intersection.getFirst() < a.getPx();
        }
    }

    @Override
    protected String solveA(BufferedReader reader) {
        Set<Line> lines = parseLines(reader);

        long result = lines
                .stream()
                .flatMap(line -> lines.stream().filter(v -> !v.equals(line)).map(v -> Set.of(v, line)))
                .collect(Collectors.toSet())
                .stream()
                .map(pairSet -> {
                    List<Line> list = new ArrayList<>(pairSet);

                    return new Pair<>(list.get(0), list.get(1));
                })
                .filter(pair -> isInterceptionPossible(pair.getFirst(), pair.getSecond()))
                .count();

        return String.valueOf(result);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        List<Hailstone> hailstones = parseHailstones(reader);

        return String.valueOf(part2(hailstones.toArray(Hailstone[]::new)));
    }

    private List<Hailstone> parseHailstones(BufferedReader reader) {
        return reader
                .lines()
                .map(line ->
                        Arrays.stream(line.split("@"))
                                .map(s ->
                                        Arrays.stream(s.split(", "))
                                                .map(String::trim)
                                                .map(Long::valueOf)
                                                .toList()
                                ).toList()
                )
                .map(split -> new Hailstone(
                        split.get(0).get(0),
                        split.get(0).get(1),
                        split.get(0).get(2),
                        split.get(1).get(0),
                        split.get(1).get(1),
                        split.get(1).get(2)
                ))
                .toList();
    }

    private record Hailstone(long x, long y, long z, long vx, long vy, long vz) {

    }

    /**
     * Stolen from <a href="https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/ker8l05">here</a>
     */
    private static long part2(Hailstone[] hailstones) {

        Hailstone h1 = hailstones[0];
        Hailstone h2 = hailstones[1];

        int range = 500;
        for (int vx = -range; vx <= range; vx++) {
            for (int vy = -range; vy <= range; vy++) {
                for (int vz = -range; vz <= range; vz++) {

                    if (vx == 0 || vy == 0 || vz == 0) {
                        continue;
                    }

                    // Find starting point for rock that will intercept first two hailstones (x,y) on this trajectory

                    // simultaneous linear equation (from part 1):
                    // H1:  x = A + a*t   y = B + b*t
                    // H2:  x = C + c*u   y = D + d*u
                    //
                    //  t = [ d ( C - A ) - c ( D - B ) ] / ( a * d - b * c )
                    //
                    // Solve for origin of rock intercepting both hailstones in x,y:
                    //     x = A + a*t - vx*t   y = B + b*t - vy*t
                    //     x = C + c*u - vx*u   y = D + d*u - vy*u

                    long A = h1.x, a = h1.vx - vx;
                    long B = h1.y, b = h1.vy - vy;
                    long C = h2.x, c = h2.vx - vx;
                    long D = h2.y, d = h2.vy - vy;

                    // skip if division by 0
                    if (c == 0 || (a * d) - (b * c) == 0) {
                        continue;
                    }

                    // Rock intercepts H1 at time t
                    long t = (d * (C - A) - c * (D - B)) / ((a * d) - (b * c));

                    // Calculate starting position of rock from intercept point
                    long x = h1.x + h1.vx * t - vx * t;
                    long y = h1.y + h1.vy * t - vy * t;
                    long z = h1.z + h1.vz * t - vz * t;


                    // check if this rock throw will hit all hailstones

                    boolean hitall = true;
                    for (int i = 0; i < hailstones.length; i++) {

                        Hailstone h = hailstones[i];
                        long u;
                        if (h.vx != vx) {
                            u = (x - h.x) / (h.vx - vx);
                        } else if (h.vy != vy) {
                            u = (y - h.y) / (h.vy - vy);
                        } else if (h.vz != vz) {
                            u = (z - h.z) / (h.vz - vz);
                        } else {
                            throw new RuntimeException();
                        }

                        if ((x + u * vx != h.x + u * h.vx) || (y + u * vy != h.y + u * h.vy) || ( z + u * vz != h.z + u * h.vz)) {
                            hitall = false;
                            break;
                        }
                    }

                    if (hitall) {
                        return x + y + z;
                    }
                }
            }
        }

        return -1;
    }
}
