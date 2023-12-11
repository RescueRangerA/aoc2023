package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

public class Day10 extends Day {

    private static char[][] buildGrid(BufferedReader reader) {
        return reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    private static Pair<Integer, Integer> pickDirection(char[][] grid, Pair<Integer, Integer> coord, Pair<Integer, Integer> from) {
        int i = coord.getFirst();
        int j = coord.getSecond();

        char pipe;
        try {
            pipe = grid[i][j];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        LinkedList<Pair<Integer, Integer>> path;

        if (pipe == '|') {
            path = new LinkedList<>(List.of(new Pair<>(i - 1, j), new Pair<>(i + 1, j)));
        } else if (pipe == '-') {
            path = new LinkedList<>(List.of(new Pair<>(i, j - 1), new Pair<>(i, j + 1)));
        } else if (pipe == 'L') {
            path = new LinkedList<>(List.of(new Pair<>(i - 1, j), new Pair<>(i, j + 1)));
        } else if (pipe == 'J') {
            path = new LinkedList<>(List.of(new Pair<>(i - 1, j), new Pair<>(i, j - 1)));
        } else if (pipe == '7') {
            path = new LinkedList<>(List.of(new Pair<>(i + 1, j), new Pair<>(i, j - 1)));
        } else if (pipe == 'F') {
            path = new LinkedList<>(List.of(new Pair<>(i + 1, j), new Pair<>(i, j + 1)));
        } else if (pipe == '.') {
            return null;
        } else if (pipe == 'S') {
            List<Pair<Integer, Integer>> list = Stream.of(
                            new Pair<>(coord.getFirst(), coord.getSecond() - 1),
                            new Pair<>(coord.getFirst() - 1, coord.getSecond()),
                            new Pair<>(coord.getFirst(), coord.getSecond() + 1),
                            new Pair<>(coord.getFirst() + 1, coord.getSecond())
                    )
                    .map(p -> new Pair<>(p, pickDirection(grid, p, coord)))
                    .filter(p -> Objects.nonNull(p.getSecond()))
                    .map(Pair::getFirst)
                    .toList();

            return list.get(0);
        } else {
            return null;
        }

        if (path.removeIf(from::equals)) {
            return path.getFirst();
        } else {
            return null;
        }
    }

    @Override
    protected String solveA(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        Queue<Pair<Integer, Integer>> operationalQ = new LinkedList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'S') {
                    operationalQ.add(new Pair<>(i, j));
                    break;
                }
            }
        }

        long counter = 0L;
        Pair<Integer, Integer> prev = null;
        while (!operationalQ.isEmpty()) {
            Pair<Integer, Integer> coord = operationalQ.poll();

            Pair<Integer, Integer> nextCoord = pickDirection(grid, coord, prev);

            if (grid[nextCoord.getFirst()][nextCoord.getSecond()] == 'S') {
                break;
            }

            prev = coord;
            operationalQ.add(nextCoord);
            counter++;
        }

        return String.valueOf((counter + 1) / 2);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[][] grid = buildGrid(reader);

        Queue<Pair<Integer, Integer>> operationalQ = new LinkedList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'S') {
                    operationalQ.add(new Pair<>(i, j));
                    break;
                }
            }
        }


        Set<Pair<Integer, Integer>> border = new HashSet<>();
        Pair<Integer, Integer> prev = null;
        while (!operationalQ.isEmpty()) {
            Pair<Integer, Integer> coord = operationalQ.poll();

            Pair<Integer, Integer> nextCoord = pickDirection(grid, coord, prev);

            border.add(coord);
            if (grid[nextCoord.getFirst()][nextCoord.getSecond()] == 'S') {
                break;
            }

            prev = coord;
            operationalQ.add(nextCoord);
        }


        int minI = border.stream().mapToInt(Pair::getFirst).min().getAsInt();
        int maxI = border.stream().mapToInt(Pair::getFirst).max().getAsInt();

        int minJ = border.stream().mapToInt(Pair::getSecond).min().getAsInt();
        int maxJ = border.stream().mapToInt(Pair::getSecond).max().getAsInt();

        long counter = 0;
        for (int i = minI; i <= maxI; i++) {

            Stack<Character> stack = new Stack<>();

            for (int j = minJ; j <= maxJ; j++) {
                Pair<Integer, Integer> coord = new Pair<>(i, j);
                char symbol = grid[i][j];

                if (border.contains(coord)) {
                    if (symbol == '-') {
                        continue;
                    }

                    if (symbol == 'F' || symbol == 'L') {
                        stack.add(symbol);
                    } else if (symbol == '|') {
                        if (stack.isEmpty()) {
                            stack.add(symbol);
                        } else {
                            stack.pop();
                        }
                    } else if (symbol == 'J' || symbol == '7') {
                        char stackSymbol = stack.peek();

                        if (stackSymbol == 'F' && symbol == '7' || stackSymbol == 'L' && symbol == 'J') {
                            stack.pop();
                        } else if (stackSymbol == 'F' && symbol == 'J' || stackSymbol == 'L' && symbol == '7') {
                            stack.pop();

                            if (stack.isEmpty()) {
                                stack.add(stackSymbol);
                            } else {
                                stack.pop();
                            }
                        }
                    }
                } else if (!stack.isEmpty()) {
                    counter++;
                }
            }
        }

        return String.valueOf(counter);
    }

}
