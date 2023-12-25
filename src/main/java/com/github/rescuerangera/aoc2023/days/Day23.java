package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

public class Day23 extends Day {

    private enum Direction {
        EAST, SOUTH, NORTH, WEST;

        public static Direction getOpposite(Direction Direction) {
            return switch (Direction) {
                case EAST -> WEST;
                case WEST -> EAST;
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
            };
        }
    }


    private static char[][] buildGrid(BufferedReader reader) {
        return reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    @Override
    protected String solveA(BufferedReader reader) {
        return solveTask(reader, this::possiblePathsA);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return solveTask(reader, this::possiblePathsB);
    }

    private List<Position> possiblePathsB(char[][] grid, Position from) {
        EnumSet<Direction> allDirections = switch (grid[from.i()][from.j()]) {
            case '>', '^', 'v', '<', '.' -> EnumSet.allOf(Direction.class);
            default -> EnumSet.noneOf(Direction.class);
        };

        return allDirections
                .stream()
                .map(direction -> applyDirection(from, direction))
                .filter(pos -> pos.i() >= 0 && pos.i() < grid.length && pos.j() >= 0 && pos.j() < grid[0].length)
                .filter(pos -> grid[pos.i()][pos.j()] != '#')
                .toList();
    }

    private List<Position> possiblePathsA(char[][] grid, Position from) {
        EnumSet<Direction> allDirections = switch (grid[from.i()][from.j()]) {
            case '>' -> EnumSet.of(Direction.EAST);
            case '^' -> EnumSet.of(Direction.NORTH);
            case 'v' -> EnumSet.of(Direction.SOUTH);
            case '<' -> EnumSet.of(Direction.WEST);
            case '.' -> EnumSet.allOf(Direction.class);
            default -> EnumSet.noneOf(Direction.class);
        };

        return allDirections
                .stream()
                .map(direction -> applyDirection(from, direction))
                .filter(pos -> pos.i() >= 0 && pos.i() < grid.length && pos.j() >= 0 && pos.j() < grid[0].length)
                .filter(pos -> grid[pos.i()][pos.j()] != '#')
                .toList();
    }

    private record Position(int i, int j) {

    }

    private Position applyDirection(Position position, Direction direction) {
        return switch (direction) {
            case EAST -> new Position(position.i(), position.j() + 1);
            case SOUTH -> new Position(position.i() + 1, position.j());
            case WEST -> new Position(position.i(), position.j() - 1);
            case NORTH -> new Position(position.i() - 1, position.j());
            default -> throw new IllegalArgumentException(String.valueOf(direction));
        };
    }

    private record Vertex(Position nextPosition, int length) {

    }

    private LinkedHashMap<Position, List<Vertex>> buildGraph(
            BufferedReader reader,
            BiFunction<char[][], Position, List<Position>> possiblePathsFunction
    ) {
        char[][] grid = buildGrid(reader);

        Position start = null;
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[0][j] == '.') {
                start = new Position(0, j);
            }
        }
        Objects.requireNonNull(start);

        Position finish = null;
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[grid.length - 1][j] == '.') {
                finish = new Position(grid.length - 1, j);
            }
        }
        Objects.requireNonNull(finish);

        LinkedHashMap<Position, List<Vertex>> graph = new LinkedHashMap<>();

        Queue<Position> queue = new LinkedList<>();
        queue.add(start);

        Set<Position> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Position position = queue.poll();

            if (visited.contains(position)) {
                continue;
            }

            graph.put(position, new ArrayList<>());

            for (Position newPosition : possiblePathsFunction.apply(grid, position)) {
                Position prev = position;
                Position lastPos = newPosition;
                int length = 1;

                boolean deadEnd = false;

                while (true) {
                    List<Position> neighbours = possiblePathsFunction.apply(grid, lastPos);

                    if (neighbours.equals(List.of(prev))
                            && List.of('>', '^', 'v', '<').contains(grid[lastPos.i()][lastPos.j()])
                    ) {
                        deadEnd = true;
                        break;
                    }

                    if (neighbours.size() != 2) {
                        break;
                    }

                    for (Position neighbour : neighbours) {
                        if (neighbour.equals(prev)) {
                            continue;
                        }

                        length++;
                        prev = lastPos;
                        lastPos = neighbour;
                        break;
                    }
                }

                if (deadEnd) {
                    continue;
                }

                graph.get(position).add(new Vertex(lastPos, length));
                queue.add(lastPos);
            }

            visited.add(position);
        }

        return graph;
    }

    private record StackHikeItem(Position last, int length, Set<Position> visited) {

    }

    private String solveTask(
            BufferedReader reader,
            BiFunction<char[][], Position, List<Position>> possiblePathsFunction
    ) {
        LinkedHashMap<Position, List<Vertex>> graph = buildGraph(reader, possiblePathsFunction);
        Position start = graph.keySet().stream().findFirst().orElseThrow();
        Position finish = graph.keySet().stream().reduce((a, b) -> b).orElseThrow();

        Stack<StackHikeItem> stack = new Stack<>();
        stack.add(new StackHikeItem(start, 0, new HashSet<>(Set.of(start))));


        int max = -1;
        while (!stack.isEmpty()) {
            StackHikeItem stackHikeItem = stack.pop();
            Position last = stackHikeItem.last();
            Set<Position> visited = stackHikeItem.visited();

            if (last.equals(finish)) {
                max = Math.max(max, stackHikeItem.length());
            }

            graph
                    .get(last)
                    .stream()
                    .filter(v -> !visited.contains(v.nextPosition()))
                    .forEach(v -> {
                        HashSet<Position> newVisited = new HashSet<>(visited);
                        newVisited.add(v.nextPosition());

                        stack.add(
                                new StackHikeItem(
                                        v.nextPosition(),
                                        stackHikeItem.length() + v.length(),
                                        newVisited
                                )
                        );
                    });
        }

        return String.valueOf(max);
    }

}
