package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;

public class Day17 extends Day {

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

    private record Path(Operation operation, long score) {
    }

    private record Operation(int i, int j, int straitStreak, Direction direction) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Operation operation)) return false;
            return i == operation.i && j == operation.j && straitStreak == operation.straitStreak && direction == operation.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j, straitStreak, direction);
        }
    }

    private static int[][] buildGrid(BufferedReader reader) {
        return reader
                .lines()
                .map(line -> line.chars().mapToObj(Character::getNumericValue).mapToInt(v -> v).toArray())
                .toArray(int[][]::new);
    }

    @Override
    protected String solveA(BufferedReader reader) {
        int[][] grid = buildGrid(reader);

        PriorityQueue<Path> op = new PriorityQueue<>(Comparator.comparingLong(Path::score));
        op.add(new Path(new Operation(0, 1, 1, Direction.EAST), grid[0][1]));
        op.add(new Path(new Operation(1, 0, 1, Direction.SOUTH), grid[1][0]));

        HashSet<Operation> visited = new HashSet<>();

        while (!op.isEmpty()) {
            Path path = op.poll();
            Operation currentOperation = path.operation();

            if (visited.contains(currentOperation)) {
                continue;
            }

            visited.add(currentOperation);

            int i = currentOperation.i();
            int j = currentOperation.j();

            if ((i == grid.length - 1) && (j == grid[0].length - 1) && (currentOperation.straitStreak() <= 3)) {
                return String.valueOf(path.score());
            }

            Direction direction = currentOperation.direction();
            int streak = currentOperation.straitStreak();
            long score = path.score();

            EnumSet<Direction> allDirections = EnumSet.allOf(Direction.class);
            allDirections.remove(Direction.getOpposite(direction));

            for (Direction d : allDirections) {
                final int straitStreak = d.equals(direction) ? streak + 1 : 1;

                if (straitStreak > 3) {
                    continue;
                }

                int nextI;
                int nextJ;

                switch (d) {
                    case EAST -> {
                        nextI = i;
                        nextJ = j + 1;
                    }
                    case SOUTH -> {
                        nextI = i + 1;
                        nextJ = j;
                    }
                    case WEST -> {
                        nextI = i;
                        nextJ = j - 1;
                    }
                    case NORTH -> {
                        nextI = i - 1;
                        nextJ = j;
                    }
                    default -> throw new IllegalArgumentException(String.valueOf(d));
                }

                if (nextI < 0 || nextI >= grid.length || nextJ < 0 || nextJ >= grid[0].length) {
                    continue;
                }

                op.add(
                        new Path(
                                new Operation(nextI, nextJ, straitStreak, d),
                                score + grid[nextI][nextJ]
                        )
                );
            }
        }

        return "";
    }

    @Override
    protected String solveB(BufferedReader reader) {
        int[][] grid = buildGrid(reader);

        PriorityQueue<Path> op = new PriorityQueue<>(Comparator.comparingLong(Path::score));
        op.add(new Path(new Operation(0, 1, 1, Direction.EAST), grid[0][1]));
        op.add(new Path(new Operation(1, 0, 1, Direction.SOUTH), grid[1][0]));

        HashSet<Operation> visited = new HashSet<>();

        while (!op.isEmpty()) {
            Path path = op.poll();
            Operation currentOperation = path.operation();

            if (visited.contains(currentOperation)) {
                continue;
            }

            visited.add(currentOperation);

            int i = currentOperation.i();
            int j = currentOperation.j();

            if ((i == grid.length - 1) && (j == grid[0].length - 1) && currentOperation.straitStreak() > 3) {
                return String.valueOf(path.score());
            }

            Direction direction = currentOperation.direction();
            int streak = currentOperation.straitStreak();
            long score = path.score();

            EnumSet<Direction> allDirections = EnumSet.allOf(Direction.class);
            allDirections.remove(Direction.getOpposite(direction));

            if (streak < 4) {
                allDirections = EnumSet.of(direction);
            }

            for (Direction d : allDirections) {
                final int straitStreak = d.equals(direction) ? streak + 1 : 1;

                if (straitStreak > 10) {
                    continue;
                }

                int nextI;
                int nextJ;

                switch (d) {
                    case EAST -> {
                        nextI = i;
                        nextJ = j + 1;
                    }
                    case SOUTH -> {
                        nextI = i + 1;
                        nextJ = j;
                    }
                    case WEST -> {
                        nextI = i;
                        nextJ = j - 1;
                    }
                    case NORTH -> {
                        nextI = i - 1;
                        nextJ = j;
                    }
                    default -> throw new IllegalArgumentException(String.valueOf(d));
                }

                if (nextI < 0 || nextI >= grid.length || nextJ < 0 || nextJ >= grid[0].length) {
                    continue;
                }

                op.add(
                        new Path(
                                new Operation(nextI, nextJ, straitStreak, d),
                                score + grid[nextI][nextJ]
                        )
                );
            }
        }

        return "";
    }
}
