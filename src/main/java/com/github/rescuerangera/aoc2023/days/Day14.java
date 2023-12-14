package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Day14 extends Day {
    private record Rock(Type type, int i, int j) {

        public static Comparator<Rock> horizontalComparator() {
            return Comparator
                    .comparingInt(Rock::i)
                    .thenComparingInt(Rock::j);
        }

        public static Comparator<Rock> verticalComparator() {
            return Comparator
                    .comparingInt(Rock::j)
                    .thenComparingInt(Rock::i);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Rock rock)) return false;
            return i == rock.i && j == rock.j && type == rock.type;
        }

        private enum Type {
            ROUND, CUBE
        }

        public static Rock ofRound(int i, int j) {
            return new Rock(Type.ROUND, i, j);
        }

        public static Rock ofCube(int i, int j) {
            return new Rock(Type.CUBE, i, j);
        }
    }

    private static Set<Rock> buildRocks(char[][] grid) {
        Set<Rock> rocks = new HashSet<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                char s = grid[i][j];

                if (s == 'O') {
                    rocks.add(Rock.ofRound(i, j));
                } else if (s == '#') {
                    rocks.add(Rock.ofCube(i, j));
                }
            }
        }

        return rocks;
    }

    @Override
    protected String solveA(BufferedReader reader) {
        char[][] grid = reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        Set<Rock> rocks = buildRocks(grid);

        rocks = tiltNorth(rocks, grid);

        return String.valueOf(calculateLoad(rocks, grid));
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[][] grid = reader
                .lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        Set<Rock> rocks = buildRocks(grid);

        Map<Set<Rock>, Set<Rock>> cache = new LinkedHashMap<>();
        Set<Set<Rock>> looper = new HashSet<>();

        int total = 1_000_000_000;

        for (int i = 0; i < total; i++) {
            if (!cache.containsKey(rocks)) {
                Set<Rock> initRocks = new HashSet<>(rocks);
                rocks = doCycle(rocks, grid);
                cache.put(initRocks, rocks);
            } else if (!looper.contains(rocks)) {
                looper.add(rocks);
                rocks = cache.get(rocks);
            } else {
                break;
            }
        }

        for (int i = 0; i < (total - cache.size()) % looper.size(); i++) {
            rocks = cache.get(rocks);
        }

        return String.valueOf(calculateLoad(rocks, grid));
    }

    private static Set<Rock> doCycle(Set<Rock> rocks, char[][] grid) {
        return tiltEast(tiltSouth(tiltWest(tiltNorth(rocks, grid), grid), grid), grid);
    }

    private static Set<Rock> tiltNorth(Set<Rock> rocks, char[][] grid) {
        TreeSet<Rock> treeRocks = new TreeSet<>(Rock.verticalComparator());
        treeRocks.addAll(rocks);

        TreeSet<Rock> newRocks = new TreeSet<>(Rock.verticalComparator());

        for (Rock rock : treeRocks) {
            if (rock.type().equals(Rock.Type.CUBE)) {
                newRocks.add(rock);
                continue;
            }

            Rock lower = newRocks.lower(rock);

            if (lower == null || lower.j() != rock.j()) {
                newRocks.add(Rock.ofRound(0, rock.j()));
            } else {
                newRocks.add(Rock.ofRound(lower.i() + 1, rock.j()));
            }
        }

        return newRocks;
    }

    private static Set<Rock> tiltSouth(Set<Rock> rocks, char[][] grid) {
        TreeSet<Rock> treeRocks = new TreeSet<>(Rock.verticalComparator().reversed());
        treeRocks.addAll(rocks);

        TreeSet<Rock> newRocks = new TreeSet<>(Rock.verticalComparator());

        for (Rock rock : treeRocks) {
            if (rock.type().equals(Rock.Type.CUBE)) {
                newRocks.add(rock);
                continue;
            }

            Rock higher = newRocks.higher(rock);

            if (higher == null || higher.j() != rock.j()) {
                newRocks.add(Rock.ofRound(grid.length - 1, rock.j()));
            } else {
                newRocks.add(Rock.ofRound(higher.i() - 1, rock.j()));
            }
        }

        return newRocks;
    }

    private static Set<Rock> tiltWest(Set<Rock> rocks, char[][] grid) {
        TreeSet<Rock> treeRocks = new TreeSet<>(Rock.horizontalComparator());
        treeRocks.addAll(rocks);

        TreeSet<Rock> newRocks = new TreeSet<>(Rock.horizontalComparator());

        for (Rock rock : treeRocks) {
            if (rock.type().equals(Rock.Type.CUBE)) {
                newRocks.add(rock);
                continue;
            }

            Rock lower = newRocks.lower(rock);

            if (lower == null || lower.i() != rock.i()) {
                newRocks.add(Rock.ofRound(rock.i(), 0));
            } else {
                newRocks.add(Rock.ofRound(rock.i(), lower.j() + 1));
            }
        }

        return newRocks;
    }

    private static Set<Rock> tiltEast(Set<Rock> rocks, char[][] grid) {
        TreeSet<Rock> treeRocks = new TreeSet<>(Rock.horizontalComparator().reversed());
        treeRocks.addAll(rocks);

        TreeSet<Rock> newRocks = new TreeSet<>(Rock.horizontalComparator());

        for (Rock rock : treeRocks) {
            if (rock.type().equals(Rock.Type.CUBE)) {
                newRocks.add(rock);
                continue;
            }

            Rock higher = newRocks.higher(rock);

            if (higher == null || higher.i() != rock.i()) {
                newRocks.add(Rock.ofRound(rock.i(), grid[0].length - 1));
            } else {
                newRocks.add(Rock.ofRound(rock.i(), higher.j() - 1));
            }
        }

        return newRocks;
    }

    private static long calculateLoad(Set<Rock> rocks, char[][] grid) {
        return rocks
                .stream()
                .filter(e -> e.type().equals(Rock.Type.ROUND))
                .reduce(0L, (sum, rock) -> sum + grid[0].length - rock.i(), Long::sum);
    }
}
