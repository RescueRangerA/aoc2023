package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day9 extends Day {


    /**
     * Builds a custom Pascal Triangle up to the specified depth.
     * Example:
     *        1
     *       1 -1
     *      1 -2 1
     *     1 -3 3 -1
     *    1 -4 6 -4 1
     *   1 -5 10 -10 5 -1
     *
     * @param depth The depth of the Pascal Triangle.
     * @return The custom Pascal Triangle as a list of lists.
     */
    private static List<List<Integer>> buildCustomPascalTriangle(int depth) {
        List<List<Integer>> pascalTriangle = new ArrayList<>();
        for (int line = 0; line < depth; line++) {
            List<Integer> row = new ArrayList<>();
            int multiplier = 1;
            for (int i = 0; i <= line; i++) {
                row.add(multiplier * calculateBinomialCoefficient(line, i));
                multiplier *= -1;
            }
            pascalTriangle.add(row);
        }

        return pascalTriangle;
    }

    private static int calculateBinomialCoefficient(int n, int k) {
        int res = 1;
        if (k > n - k)
            k = n - k;

        for (int i = 0; i < k; i++) {
            res = res * (n - i);
            res = res / (i + 1);
        }
        return res;
    }

    private static final List<List<Integer>> customPascalTriangle = buildCustomPascalTriangle(30); // prebuild with `magic` depth


    /**
     * Calculates the apex value of a custom Pascal Triangle.
     *        1 <- apex
     *       1 -1
     *      1 -2 1
     *     1 -3 3 -1
     *    1 -4 6 -4 1
     *   1 -5 10 -10 5 -1
     *
     * @param foundation               The list of values forming the foundation of the triangle.
     * @param foundationIndexFromInclusive The starting index of the foundation elements to consider (inclusive).
     * @param foundationIndexToExclusive   The ending index of the foundation elements to consider (exclusive).
     * @return The apex value of the custom Pascal Triangle.
     */
    private static long calculateApexOfCustomPascalTriangle(
            final List<Long> foundation,
            final int foundationIndexFromInclusive,
            final int foundationIndexToExclusive
    ) {
        final int depth = foundationIndexToExclusive - foundationIndexFromInclusive;
        final List<Integer> pascal = customPascalTriangle.get(depth - 1);

        return IntStream.range(0, depth)
                .boxed()
                .reduce(
                        0L,
                        (acc, i) -> acc + pascal.get(i) * foundation.get(foundationIndexToExclusive - (i + 1)),
                        Long::sum
                );
    }

    private Integer findAllZerosDepth(List<Long> sequence) {
        int depth = 0;

        for (int currentDepth = 2; currentDepth < sequence.size(); currentDepth++) {
            long currentApex = calculateApexOfCustomPascalTriangle(sequence, 0, currentDepth);

            if (currentApex != 0L) {
                continue;
            }

            final int finalCurrentDepth = currentDepth;
            boolean matchAll = IntStream
                    .of(1, sequence.size() - finalCurrentDepth)
                    .allMatch(
                            startIndex ->
                                    calculateApexOfCustomPascalTriangle(
                                            sequence,
                                            startIndex,
                                            startIndex + finalCurrentDepth
                                    ) == 0
                    );

            if (matchAll) {
                depth = currentDepth;
                break;
            }
        }

        return depth;
    }

    private List<Long> parseSequenceFromLine(String line) {
        return Arrays.stream(line.split("\\s+"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    protected String solveA(BufferedReader reader) {
        return reader
                .lines()
                .map(this::parseSequenceFromLine)
                .map(sequence -> new Pair<>(sequence, findAllZerosDepth(sequence)))
                .reduce(0L,
                        (sum, pair) -> {
                            List<Long> sequence = pair.getFirst();
                            Integer depth = pair.getSecond();

                            return sum + IntStream.range(0, depth - 1)
                                    .boxed()
                                    .reduce(
                                            0L,
                                            (acc, currentDepthIndex) -> acc + calculateApexOfCustomPascalTriangle(
                                                    sequence,
                                                    sequence.size() - currentDepthIndex - 1,
                                                    sequence.size()
                                            ),
                                            Long::sum
                                    );
                        },
                        Long::sum
                ).toString();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return reader
                .lines()
                .map(this::parseSequenceFromLine)
                .map(sequence -> new Pair<>(sequence, findAllZerosDepth(sequence)))
                .reduce(0L,
                        (sum, pair) -> {
                            List<Long> sequence = pair.getFirst();
                            Integer depth = pair.getSecond();

                            return sum + IntStream
                                    .rangeClosed(0, depth - 2).map(i -> depth - 2 - i) // reversed intStream hack
                                    .boxed()
                                    .reduce(
                                            0L,
                                            (acc, i) -> calculateApexOfCustomPascalTriangle(sequence, 0, i + 1) - acc,
                                            Long::sum
                                    );
                        },
                        Long::sum
                )
                .toString();
    }
}
