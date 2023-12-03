package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Day3 extends Day {
    private boolean isDigitOrDot(char symbol) {
        return isDigit(symbol) || symbol == '.';
    }

    private boolean isDigit(char symbol) {
        return Character.isDigit(symbol);
    }

    private boolean isGear(char symbol) {
        return symbol == '*';
    }

    private Stream<Pair<Integer, Integer>> makeTraverse(int i, int j) {
        return Stream.of(
                new Pair<>(i, j - 1),
                new Pair<>(i - 1, j - 1),
                new Pair<>(i - 1, j),
                new Pair<>(i - 1, j + 1),
                new Pair<>(i, j + 1),
                new Pair<>(i + 1, j - 1),
                new Pair<>(i + 1, j),
                new Pair<>(i + 1, j + 1)
        );
    }

    @Override
    protected String solveA(BufferedReader reader) {
        char[][] symbols = reader.lines().map(String::toCharArray).toArray(char[][]::new);
        boolean[][] visited = new boolean[symbols.length][symbols[0].length];

        long sum = 0L;
        for (int i = 0; i < symbols.length; i++) {
            for (int j = 0; j < symbols[i].length; j++) {
                char symbol = symbols[i][j];

                if (isDigitOrDot(symbol)) {
                    continue;
                }

                sum += makeTraverse(i, j)
                        .reduce(0L,
                                (currentSum, pair) -> {
                                    Long result = check(symbols, pair.getFirst(), pair.getSecond(), visited);

                                    if (result != null) {
                                        currentSum += result;
                                    }

                                    return currentSum;
                                },
                                Long::sum
                        );
            }
        }

        return String.valueOf(sum);
    }

    private Long check(char[][] symbols, int i, int j, boolean[][] visited) {
        if (i < 0 || i > symbols.length - 1) {
            return null;
        }

        if (j < 0 || j > symbols[0].length - 1) {
            return null;
        }

        if (!isDigit(symbols[i][j])) {
            return null;
        }

        if (visited[i][j]) {
            return null;
        }

        while (j > 0 && isDigit(symbols[i][j - 1])) {
            j--;
        }

        StringBuilder numberString = new StringBuilder();
        while (j < symbols[0].length && isDigit(symbols[i][j])) {
            numberString.append(symbols[i][j]);

            visited[i][j] = true;

            j++;
        }

        if (numberString.isEmpty()) {
            return null;
        }

        return Long.parseLong(numberString.toString());
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[][] symbols = reader.lines().map(String::toCharArray).toArray(char[][]::new);
        boolean[][] visited = new boolean[symbols.length][symbols[0].length];

        long sum = 0L;
        for (int i = 0; i < symbols.length; i++) {
            for (int j = 0; j < symbols[i].length; j++) {
                char symbol = symbols[i][j];

                if (isDigitOrDot(symbol) || !isGear(symbol)) {
                    continue;
                }

                List<Long> results = makeTraverse(i, j)
                        .map(pair -> check(symbols, pair.getFirst(), pair.getSecond(), visited))
                        .filter(Objects::nonNull)
                        .toList();

                if (results.size() == 2) {
                    sum += results.get(0) * results.get(1);
                }
            }
        }

        return String.valueOf(sum);
    }
}
