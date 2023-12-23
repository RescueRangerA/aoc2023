package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 extends Day {

    private static char[][] buildGrid(BufferedReader reader) {
        return reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    @Override
    protected String solveA(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        int startI = 0, startJ = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'S') {
                    startI = i;
                    startJ = j;
                    grid[i][j] = '.';
                    break;
                }
            }
        }

        Predicate<Pair<Integer, Integer>> isNotRock = (pair) -> grid[pair.getFirst()][pair.getSecond()] != '#';
        Predicate<Pair<Integer, Integer>> isInRange = (pair) -> pair.getFirst() >= 0 && pair.getFirst() < grid.length && pair.getSecond() >= 0 && pair.getSecond() < grid[0].length;

        Set<Pair<Integer, Integer>> coords = new HashSet<>();
        coords.add(new Pair<>(startI, startJ));

        for (int i = 0; i < 64; i++) {
            Set<Pair<Integer, Integer>> newCoords = new HashSet<>();
            for (Pair<Integer, Integer> coord : coords) {
                Stream.of(
                                new Pair<>(coord.getFirst() - 1, coord.getSecond()),
                                new Pair<>(coord.getFirst() + 1, coord.getSecond()),
                                new Pair<>(coord.getFirst(), coord.getSecond() - 1),
                                new Pair<>(coord.getFirst(), coord.getSecond() + 1)
                        )
                        .filter(isInRange.and(isNotRock))
                        .forEach(newCoords::add);
            }

            coords = newCoords;
        }

        return String.valueOf(coords.size());
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        int width = grid[0].length;

        int startI = 0, startJ = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'S') {
                    startI = i;
                    startJ = j;
                    grid[i][j] = '.';
                    break;
                }
            }
        }

        long count = 26501365;
        long cycles = count / width;
        long reminder = count % width;

        Predicate<Pair<Integer, Integer>> isNotRock = (pair) -> {
            int correctI = pair.getFirst() >= 0 ? pair.getFirst() % grid.length : (grid.length + (pair.getFirst() + 1) % grid.length - 1);
            int correctJ = pair.getSecond() >= 0 ? pair.getSecond() % grid[0].length : (grid[0].length + (pair.getSecond() + 1) % grid[0].length - 1);

            return grid[correctI][correctJ] != '#';
        };

        Set<Pair<Integer, Integer>> coords = new HashSet<>();
        coords.add(new Pair<>(startI, startJ));


        final List<Pair<Integer, Integer>> regressionPoints = new ArrayList<>();

        int steps = 0;
        for (int i = 0; i < 3; i++) {
            while (steps < i * width + reminder) {
                coords = coords
                        .stream()
                        .flatMap(coord -> Stream.of(
                                new Pair<>(coord.getFirst() - 1, coord.getSecond()),
                                new Pair<>(coord.getFirst() + 1, coord.getSecond()),
                                new Pair<>(coord.getFirst(), coord.getSecond() - 1),
                                new Pair<>(coord.getFirst(), coord.getSecond() + 1)
                        ))
                        .filter(isNotRock)
                        .collect(Collectors.toSet());

                steps++;
            }

            regressionPoints.add(new Pair<>((i), coords.size()));
        }

        // cool idea took from https://github.com/SPixs/AOC2023/blob/master/src/Day21.java
        Function<Long, Long> g = x -> {
            double x1 = regressionPoints.get(0).getFirst();
            double y1 = regressionPoints.get(0).getSecond();
            double x2 = regressionPoints.get(1).getFirst();
            double y2 = regressionPoints.get(1).getSecond();
            double x3 = regressionPoints.get(2).getFirst();
            double y3 = regressionPoints.get(2).getSecond();

            return (long) (((x-x2) * (x-x3)) / ((x1-x2) * (x1-x3)) * y1 +
                    ((x-x1) * (x-x3)) / ((x2-x1) * (x2-x3)) * y2 +
                    ((x-x1) * (x-x2)) / ((x3-x1) * (x3-x2)) * y3);
        };

        return String.valueOf(g.apply(cycles));
    }
}
