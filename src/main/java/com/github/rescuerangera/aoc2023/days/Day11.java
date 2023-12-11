package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 extends Day {
    private String solveWithMultiplier(BufferedReader reader, long multiplier) {
        Set<Pair<Integer, Integer>> galaxies = new HashSet<>();
        AtomicInteger indexI = new AtomicInteger();
        AtomicInteger indexJ = new AtomicInteger();

        AtomicInteger minI = new AtomicInteger();
        AtomicInteger maxI = new AtomicInteger();
        AtomicInteger minJ = new AtomicInteger();
        AtomicInteger maxJ = new AtomicInteger();

        reader.lines().forEach(line ->
                {
                    indexJ.set(0);
                    line.chars().forEach(i -> {
                        char s = (char) i;

                        if (s == '#') {
                            galaxies.add(new Pair<>(indexI.get(), indexJ.get()));

                            minI.set(Math.min(minI.get(), indexI.get()));
                            maxI.set(Math.max(maxI.get(), indexI.get()));

                            minJ.set(Math.min(minJ.get(), indexJ.get()));
                            maxJ.set(Math.max(maxJ.get(), indexJ.get()));
                        }

                        indexJ.incrementAndGet();
                    });

                    indexI.incrementAndGet();
                }
        );

        TreeSet<Integer> emptyRows = IntStream
                .range(minI.get(), maxI.get())
                .boxed()
                .collect(Collectors.toCollection(TreeSet::new));

        emptyRows.removeAll(galaxies.stream().map(Pair::getFirst).collect(Collectors.toSet()));

        TreeSet<Integer> emptyColumns = IntStream
                .range(minJ.get(), maxJ.get())
                .boxed()
                .collect(Collectors.toCollection(TreeSet::new));

        emptyColumns.removeAll(galaxies.stream().map(Pair::getSecond).collect(Collectors.toSet()));


        return galaxies.stream()
                .flatMap(e1 -> galaxies.stream()
                        .filter(e2 -> !e1.equals(e2))
                        .map(e2 -> Set.of(e1, e2))
                )
                .collect(Collectors.toSet())
                .stream()
                .reduce(0L,
                        (sum, pairOfGalaxies) -> sum + calculateDistance(
                                pairOfGalaxies,
                                emptyRows,
                                emptyColumns,
                                multiplier
                        ),
                        Long::sum
                )
                .toString();
    }

    private static long calculateDistance(
            Set<Pair<Integer, Integer>> pairOfGalaxies,
            TreeSet<Integer> emptyRows,
            TreeSet<Integer> emptyColumns,
            long multiplier
    ) {
        Iterator<Pair<Integer, Integer>> iterator = pairOfGalaxies.iterator();
        Pair<Integer, Integer> galaxy1 = iterator.next();
        Pair<Integer, Integer> galaxy2 = iterator.next();

        long distance = Math.abs(galaxy1.getFirst() - galaxy2.getFirst()) + Math.abs(galaxy1.getSecond() - galaxy2.getSecond());

        distance += emptyRows.subSet(
                        Math.min(galaxy1.getFirst(), galaxy2.getFirst()),
                        true,
                        Math.max(galaxy1.getFirst(), galaxy2.getFirst()),
                        true
                )
                .size() * (multiplier - 1);

        distance += emptyColumns.subSet(
                        Math.min(galaxy1.getSecond(), galaxy2.getSecond()),
                        true,
                        Math.max(galaxy1.getSecond(), galaxy2.getSecond()),
                        true
                )
                .size() * (multiplier - 1);

        return distance;
    }

    @Override
    protected String solveA(BufferedReader reader) {
        return solveWithMultiplier(reader, 2);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return solveWithMultiplier(reader, 1_000_000);
    }
}
