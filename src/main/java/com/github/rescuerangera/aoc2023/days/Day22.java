package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Interval;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * ideas and a lot of code took from <a href="https://github.com/ash42/adventofcode/blob/main/adventofcode2023/src/nl/michielgraat/adventofcode2023/day22/Day22.java">here</a>
 */
public class Day22 extends Day {
    private record Brick(Interval x, Interval y, Interval z) implements Comparable<Brick> {

        @Override
        public int compareTo(Brick o) {
            return Comparator
                    .comparing(Brick::z)
                    .thenComparing(Brick::x)
                    .thenComparing(Brick::y)
                    .compare(this, o)
                    ;
        }

        public Brick copyWithZShift(long zShift) {
            return new Brick(x, y, new Interval(z.getStart() - zShift, z.getEnd() - zShift));
        }
    }

    private TreeSet<Brick> parseBrick(BufferedReader reader) {
        return reader
                .lines()
                .map(line ->
                        Arrays.stream(line.split("~"))
                                .map(s ->
                                        Arrays.stream(s.split(","))
                                                .map(Integer::valueOf)
                                                .toList()
                                ).toList()
                )
                .map(split -> new Brick(
                        new Interval(split.get(0).get(0), split.get(1).get(0)),
                        new Interval(split.get(0).get(1), split.get(1).get(1)),
                        new Interval(split.get(0).get(2), split.get(1).get(2))
                ))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private boolean isBricksIntersectAtXAndY(Brick a, Brick b) {
        return Interval.intersection(a.x(), b.x()) != null
                && Interval.intersection(a.y(), b.y()) != null;
    }

    private Map<Long, List<Brick>> buildBricksByZLevelStart(BufferedReader reader) {
        TreeSet<Brick> bricks = parseBrick(reader);

        Map<Long, List<Brick>> brickZStarts = new HashMap<>();
        Map<Long, List<Brick>> bricksZEnds = new HashMap<>();

        for (Brick currentBrick : bricks) {

            for (long z = currentBrick.z().getStart(); z >= 1; z--) {
                if (z > 1 && bricksZEnds.containsKey(z - 1)) {
                    Brick newBrick = currentBrick.copyWithZShift(currentBrick.z().getStart() - z);
                    List<Brick> levelLowerBricks = bricksZEnds.getOrDefault(z - 1, new ArrayList<>());

                    if (levelLowerBricks.stream().anyMatch(v -> isBricksIntersectAtXAndY(v, currentBrick))) {
                        brickZStarts.putIfAbsent(newBrick.z().getStart(), new ArrayList<>());
                        brickZStarts.get(z).add(newBrick);

                        bricksZEnds.putIfAbsent(newBrick.z().getEnd(), new ArrayList<>());
                        bricksZEnds.get(newBrick.z().getEnd()).add(newBrick);

                        break;
                    }
                } else if (z == 1) {
                    Brick newBrick = currentBrick.copyWithZShift(currentBrick.z().getStart() - 1);

                    brickZStarts.putIfAbsent(newBrick.z().getStart(), new ArrayList<>());
                    brickZStarts.get(z).add(newBrick);

                    bricksZEnds.putIfAbsent(newBrick.z().getEnd(), new ArrayList<>());
                    bricksZEnds.get(newBrick.z().getEnd()).add(newBrick);
                }
            }
        }

        return brickZStarts;
    }

    private Map<Brick, List<Brick>> buildSupportBricks(Map<Long, List<Brick>> bricksZStarts) {
        Map<Brick, List<Brick>> supportBricks = new HashMap<>();

        long maxZ = bricksZStarts.keySet().stream().mapToLong(z -> z).max().orElse(0L);

        for (long z = 1; z <= maxZ; z++) {
            List<Brick> currentLevel = bricksZStarts.getOrDefault(z, new ArrayList<>());

            for (Brick current : currentLevel) {
                List<Brick> potentialSupporters = bricksZStarts.getOrDefault(
                        current.z().getEnd() + 1,
                        new ArrayList<>()
                );

                for (Brick potentialSupporter : potentialSupporters) {
                    if (isBricksIntersectAtXAndY(current, potentialSupporter)) {
                        supportBricks.putIfAbsent(potentialSupporter, new ArrayList<>());
                        supportBricks.get(potentialSupporter).add(current);
                    }
                }
            }
        }

        return supportBricks;
    }

    private int countRemovableBricks(Map<Long, List<Brick>> bricksZStarts, Map<Brick, List<Brick>> supportBricks) {
        List<Brick> supportingBricks = supportBricks
                .values()
                .stream()
                .flatMap(List::stream).distinct()
                .toList();

        int counter = 0;
        List<Brick> bricks = bricksZStarts.values().stream().flatMap(List::stream).distinct().toList();
        for (Brick brick : bricks) {
            if (!supportingBricks.contains(brick)) {
                counter++;
            } else {
                if (
                        supportBricks
                                .entrySet()
                                .stream()
                                .filter(e -> e.getValue().contains(brick))
                                .allMatch(e -> e.getValue().size() > 1)
                ) {
                    counter++;
                }
            }
        }

        return counter;
    }

    private Map<Brick, List<Brick>> buildSupportedBricks(
            Map<Long, List<Brick>> bricksZStarts,
            Map<Brick, List<Brick>> supportBricks
    ) {
        Map<Brick, List<Brick>> supportedBricks = new HashMap<>();

        List<Brick> bricks = bricksZStarts.values().stream().flatMap(List::stream).distinct().toList();
        for (Brick brick : bricks) {
            supportedBricks.put(
                    brick,
                    supportBricks
                            .entrySet()
                            .stream()
                            .filter(e -> e.getValue().contains(brick))
                            .map(Map.Entry::getKey).distinct().toList()
            );
        }

        return supportedBricks;
    }

    private int getNrOfFallenBricks(
            Brick brick,
            Map<Brick, List<Brick>> supportedBricks,
            Map<Brick, List<Brick>> supportBricks
    ) {
        Set<Brick> removed = new HashSet<>();
        getRemovedBricks(brick, supportedBricks, supportBricks, removed);
        removed.remove(brick);

        return removed.size();
    }

    private void getRemovedBricks(
            Brick brick,
            Map<Brick, List<Brick>> supportedBricks,
            Map<Brick, List<Brick>> supportBricks,
            Set<Brick> removed
    ) {
        List<Brick> currentSupportedBricks = supportedBricks.get(brick);

        if (!currentSupportedBricks.isEmpty()) {
            removed.add(brick);
            List<Brick> nextBricksToRemove = new ArrayList<>();

            for (Brick supported : currentSupportedBricks) {
                if (removed.containsAll(supportBricks.get(supported))) {
                    nextBricksToRemove.add(supported);
                }
            }

            if (!nextBricksToRemove.isEmpty()) {
                removed.addAll(nextBricksToRemove);
                for (Brick next : nextBricksToRemove) {
                    getRemovedBricks(next, supportedBricks, supportBricks, removed);
                }
            }
        }
    }

    @Override
    protected String solveA(BufferedReader reader) {
        Map<Long, List<Brick>> brickZStarts = buildBricksByZLevelStart(reader);

        return String.valueOf(
                countRemovableBricks(brickZStarts, buildSupportBricks(brickZStarts))
        );
    }

    @Override
    protected String solveB(BufferedReader reader) {
        Map<Long, List<Brick>> brickZStarts = buildBricksByZLevelStart(reader);
        Map<Brick, List<Brick>> supportBricks = buildSupportBricks(brickZStarts);
        Map<Brick, List<Brick>> supportedBricks = buildSupportedBricks(brickZStarts, supportBricks);
        List<Brick> allBricks = brickZStarts.values().stream().flatMap(List::stream).distinct().toList();

        return String.valueOf(
                allBricks
                        .stream()
                        .mapToInt(brick -> getNrOfFallenBricks(brick, supportedBricks, supportBricks))
                        .sum()
        );
    }
}
