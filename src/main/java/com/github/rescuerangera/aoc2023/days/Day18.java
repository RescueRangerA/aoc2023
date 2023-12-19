package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

public class Day18 extends Day {

    private record Dot(long i, long j, String rgb) {
    }

    private long calcGauss(List<Dot> dots) {
        long gauss = 0L;
        for (int k = 0; k < dots.size() - 1; k++) {
            gauss += dots.get(k).i() * dots.get(k + 1).j() - dots.get(k + 1).i() * dots.get(k).j();
        }
        gauss += dots.get(dots.size() - 1).i() * dots.get(0).j() - dots.get(0).i() * dots.get(dots.size() - 1).j();
        gauss = Math.abs(gauss / 2);

        return gauss;
    }

    @Override
    protected String solveA(BufferedReader reader) {
        LinkedList<Dot> dots = new LinkedList<>();

        AtomicLong per = new AtomicLong();

        reader
                .lines()
                .forEach(line -> {
                    String[] split = line.split(" ");
                    String dir = split[0];
                    int count = Integer.parseInt(split[1]);
                    String rgb = split[2].substring(1, split[2].length() - 1);

                    long i;
                    long j;

                    try {
                        i = dots.getLast().i();
                        j = dots.getLast().j();
                    } catch (NoSuchElementException e) {
                        i = 0;
                        j = 0;
                    }

                    if (dir.equals("R")) {
                        j += count;
                    } else if (dir.equals("D")) {
                        i += count;
                    } else if (dir.equals("L")) {
                        j -= count;
                    } else if (dir.equals("U")) {
                        i -= count;
                    }

                    dots.add(new Dot(i, j, rgb));
                    per.set(per.get() + count);
                });

        return String.valueOf(calcGauss(dots) + per.get() / 2 + 1);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        LinkedList<Dot> dots = new LinkedList<>();

        AtomicLong per = new AtomicLong();

        reader
                .lines()
                .forEach(line -> {
                    String[] split = line.split(" ");
                    String rgb = split[2].substring(2, split[2].length() - 1);

                    String dir;
                    switch (rgb.charAt(5)) {
                        case '0': {
                            dir = "R";
                            break;
                        }
                        case '1': {
                            dir = "D";
                            break;
                        }
                        case '2': {
                            dir = "L";
                            break;
                        }
                        case '3': {
                            dir = "U";
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException();
                        }
                    }

                    long count = Long.valueOf(rgb.substring(0, 5), 16);

                    long i;
                    long j;

                    try {
                        i = dots.getLast().i();
                        j = dots.getLast().j();
                    } catch (NoSuchElementException e) {
                        i = 0;
                        j = 0;
                    }

                    if (dir.equals("R")) {
                        j += count;
                    } else if (dir.equals("D")) {
                        i += count;
                    } else if (dir.equals("L")) {
                        j -= count;
                    } else if (dir.equals("U")) {
                        i -= count;
                    }

                    dots.add(new Dot(i, j, rgb));
                    per.set(per.get() + count);
                });

        return String.valueOf(calcGauss(dots) + per.get() / 2 + 1);
    }
}
