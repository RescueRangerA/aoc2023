package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Optional;

public class Day1 extends Day {

    private static final Map<String, Integer> digitMap = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    @Override
    protected String solveA(BufferedReader reader) {
        return reader
                .lines()
                .reduce(0L, (sum, line) -> {
                    char[] chars = line.toCharArray();

                    int first = -1;
                    int last = -1;

                    for (char aChar : chars) {
                        if (!Character.isDigit(aChar)) {
                            continue;
                        }

                        int value = Character.getNumericValue(aChar);
                        if (first == -1) {
                            first = value;
                        }
                        last = value;
                    }

                    return sum + 10L * first + last;
                }, Long::sum).toString();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return reader
                .lines()
                .reduce(0L, (sum, line) -> {
                    char[] chars = line.toCharArray();

                    int first = -1;
                    int last = -1;

                    for (int i = 0; i < chars.length; i++) {
                        int value = -1;

                        if (Character.isDigit(chars[i])) {
                            value = Character.getNumericValue(chars[i]);
                        } else {
                            int finalI = i;
                            Optional<Map.Entry<String, Integer>> integerEntry = digitMap
                                    .entrySet()
                                    .stream()
                                    .filter(e -> line.length() - finalI >= e.getKey().length())
                                    .filter(e -> e.getKey().equals(line.substring(finalI, finalI + e.getKey().length())))
                                    .findAny();

                            if (integerEntry.isPresent()) {
                                value = integerEntry.get().getValue();
                            }
                        }

                        if (value == -1) {
                            continue;
                        }

                        if (first == -1) {
                            first = value;
                        }
                        last = value;
                    }

                    return sum + 10L * first + last;
                }, Long::sum).toString();
    }
}
