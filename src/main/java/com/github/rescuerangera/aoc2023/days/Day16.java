package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Day16 extends Day {

    private enum Direction {
        NORTH, WEST, SOUTH, EAST
    }

    private static char[][] buildGrid(BufferedReader reader) {
        return reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    private static Long calculateCountOfEnergizedTiles(char[][] grid, Pair<Direction, Pair<Integer, Integer>> start) {
        Queue<Pair<Direction, Pair<Integer, Integer>>> queue = new LinkedList<>();
        queue.add(start);

        int[][] visited = new int[grid.length][grid[0].length];
        Set<Pair<Integer, Integer>> visitedSplitters = new HashSet<>();

        while (!queue.isEmpty()) {
            Pair<Direction, Pair<Integer, Integer>> currentPair = queue.poll();

            Direction direction = currentPair.getFirst();
            Pair<Integer, Integer> current = currentPair.getSecond();
            int i = current.getFirst();
            int j = current.getSecond();

            if (i < 0 || grid.length <= i) {
                continue;
            }

            if (j < 0 || grid[0].length <= j) {
                continue;
            }

            if (visitedSplitters.contains(current)) {
                continue;
            }

            char symbol = grid[i][j];
            visited[i][j] = 1;

            if (symbol == '|') {
                if (direction.equals(Direction.NORTH)) {
                    queue.add(new Pair<>(direction, new Pair<>(i - 1, j)));
                } else if (direction.equals(Direction.SOUTH)) {
                    queue.add(new Pair<>(direction, new Pair<>(i + 1, j)));
                } else if (direction.equals(Direction.WEST) || direction.equals(Direction.EAST)) {
                    queue.add(new Pair<>(Direction.NORTH, new Pair<>(i - 1, j)));
                    queue.add(new Pair<>(Direction.SOUTH, new Pair<>(i + 1, j)));
                    visitedSplitters.add(current);
                }
            } else if (symbol == '-') {
                if (direction.equals(Direction.WEST)) {
                    queue.add(new Pair<>(direction, new Pair<>(i, j - 1)));
                } else if (direction.equals(Direction.EAST)) {
                    queue.add(new Pair<>(direction, new Pair<>(i, j + 1)));
                } else if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
                    queue.add(new Pair<>(Direction.EAST, new Pair<>(i, j + 1)));
                    queue.add(new Pair<>(Direction.WEST, new Pair<>(i, j - 1)));
                    visitedSplitters.add(current);
                }
            } else if (symbol == '/') {
                if (direction.equals(Direction.WEST)) {
                    queue.add(new Pair<>(Direction.SOUTH, new Pair<>(i + 1, j)));
                } else if (direction.equals(Direction.EAST)) {
                    queue.add(new Pair<>(Direction.NORTH, new Pair<>(i - 1, j)));
                } else if (direction.equals(Direction.NORTH)) {
                    queue.add(new Pair<>(Direction.EAST, new Pair<>(i, j + 1)));
                } else if (direction.equals(Direction.SOUTH)) {
                    queue.add(new Pair<>(Direction.WEST, new Pair<>(i, j - 1)));
                }
            } else if (symbol == '\\') {
                if (direction.equals(Direction.WEST)) {
                    queue.add(new Pair<>(Direction.NORTH, new Pair<>(i - 1, j)));
                } else if (direction.equals(Direction.EAST)) {
                    queue.add(new Pair<>(Direction.SOUTH, new Pair<>(i + 1, j)));
                } else if (direction.equals(Direction.NORTH)) {
                    queue.add(new Pair<>(Direction.WEST, new Pair<>(i, j - 1)));
                } else if (direction.equals(Direction.SOUTH)) {
                    queue.add(new Pair<>(Direction.EAST, new Pair<>(i, j + 1)));
                }
            } else if (symbol == '.') {
                if (direction.equals(Direction.WEST)) {
                    queue.add(new Pair<>(direction, new Pair<>(i, j - 1)));
                } else if (direction.equals(Direction.EAST)) {
                    queue.add(new Pair<>(direction, new Pair<>(i, j + 1)));
                } else if (direction.equals(Direction.NORTH)) {
                    queue.add(new Pair<>(direction, new Pair<>(i - 1, j)));
                } else if (direction.equals(Direction.SOUTH)) {
                    queue.add(new Pair<>(direction, new Pair<>(i + 1, j)));
                }
            }
        }

        return Arrays
                .stream(visited)
                .reduce(
                        0L,
                        (sum, booleans) -> sum + Arrays.stream(booleans).reduce(Integer::sum).getAsInt(),
                        Long::sum
                );
    }

    protected String solveA(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        return calculateCountOfEnergizedTiles(grid, new Pair<>(Direction.EAST, new Pair<>(0, 0))).toString();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        Set<Pair<Direction, Pair<Integer, Integer>>> variants = new HashSet<>();
        for (int i = 0; i < grid.length; i++) {
            variants.add(new Pair<>(Direction.EAST, new Pair<>(i, 0)));
            variants.add(new Pair<>(Direction.WEST, new Pair<>(i, grid.length - 1)));
        }
        for (int i = 0; i < grid[0].length; i++) {
            variants.add(new Pair<>(Direction.SOUTH, new Pair<>(0, i)));
            variants.add(new Pair<>(Direction.NORTH, new Pair<>(grid[0].length - 1, i)));
        }

        return variants
                .stream()
                .map(v -> calculateCountOfEnergizedTiles(grid, v))
                .max(Long::compare)
                .get()
                .toString();
    }
}
