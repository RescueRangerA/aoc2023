package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 extends Day {

    @Override
    protected String solveA(BufferedReader reader) {
        return reader
                .lines()
                .reduce(0L,
                        (sum, line) -> {
                            String[] split = line.split(" ");
                            char[] records = split[0].toCharArray();

                            List<Integer> strings = Arrays
                                    .stream(split[1].split(","))
                                    .map(Integer::valueOf)
                                    .toList();

                            return sum + calculateNumberOfPossibilities(records, strings);
                        },
                        Long::sum
                ).toString();

    }

    @Override
    protected String solveB(BufferedReader reader) {
        return reader
                .lines()
                .reduce(0L,
                        (sum, line) -> {
                            String[] split = line.split(" ");
                            char[] baseRecords = split[0].toCharArray();
                            char[] records = new char[baseRecords.length * 5 + 4];
                            for (int i = 0; i < 5; i++) {
                                System.arraycopy(
                                        baseRecords,
                                        0,
                                        records,
                                        i * baseRecords.length + i,
                                        baseRecords.length
                                );

                                if (i > 0) {
                                    records[i * baseRecords.length + (i - 1)] = '?';
                                }
                            }

                            List<Integer> strings = repeatListNTimes(
                                    Arrays
                                            .stream(split[1].split(","))
                                            .map(Integer::valueOf)
                                            .toList(),
                                    5
                            );

                            return sum + calculateNumberOfPossibilities(records, strings);
                        },
                        Long::sum
                ).toString();
    }

    private static final Comparator<Integer> reversedCmp = (a, b) -> Integer.compare(b, a);

    private static <T> List<T> repeatListNTimes(List<T> originalList, int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> originalList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static Long calculateNumberOfPossibilities(char[] records, List<Integer> strings) {
        List<TreeMap<Integer, Long>> streaks = new ArrayList<>();
        TreeMap<Integer, Long> newMap1 = new TreeMap<>(reversedCmp);
        newMap1.put(0, 1L);
        streaks.add(newMap1);
        for (int i = 1; i < strings.size() + 1; i++) {
            streaks.add(new TreeMap<>(reversedCmp));
        }

        for (char symbol : records) {
            if (symbol == '?') {
                List<TreeMap<Integer, Long>> streaks1 = calcSharp(streaks, strings);
                List<TreeMap<Integer, Long>> streaks2 = calcDot(streaks, strings);

                streaks = new ArrayList<>();
                for (int j = 0; j < strings.size() + 1; j++) {
                    TreeMap<Integer, Long> streaksMap1 = streaks1.get(j);
                    TreeMap<Integer, Long> streaksMap2 = streaks2.get(j);

                    for (Map.Entry<Integer, Long> e : streaksMap2.entrySet()) {
                        streaksMap1.merge(e.getKey(), e.getValue(), Long::sum);
                    }

                    streaks.add(streaksMap1);
                }
            } else if (symbol == '#') {
                streaks = calcSharp(streaks, strings);
            } else if (symbol == '.') {
                streaks = calcDot(streaks, strings);
            }
        }

        streaks = calcDot(streaks, strings);

        return streaks.get(strings.size()).get(0);
    }

    private static List<TreeMap<Integer, Long>> calcSharp(List<TreeMap<Integer, Long>> streaks, List<Integer> strings) {
        List<TreeMap<Integer, Long>> newStreaks = new ArrayList<>();

        for (int j = 0; j < streaks.size(); j++) {
            TreeMap<Integer, Long> streakM = streaks.get(j);

            Integer expectedStreak = 0;
            if (j < strings.size()) {
                expectedStreak = strings.get(j);
            }

            TreeMap<Integer, Long> newStreakM = new TreeMap<>(reversedCmp);
            for (Map.Entry<Integer, Long> e : streakM.entrySet()) {
                if (e.getKey() >= expectedStreak) {
                    continue;
                }

                newStreakM.put(
                        e.getKey() + 1,
                        e.getValue()
                );
            }

            newStreakM.put(0, 0L);
            newStreaks.add(newStreakM);
        }

        return newStreaks;
    }

    private static List<TreeMap<Integer, Long>> calcDot(List<TreeMap<Integer, Long>> streaks, List<Integer> strings) {
        List<TreeMap<Integer, Long>> newStreaks = new ArrayList<>();

        TreeMap<Integer, Long> newMap = new TreeMap<>(reversedCmp);
        newMap.put(0, 0L);
        newStreaks.add(newMap);

        for (int j = 0; j < streaks.size(); j++) {
            TreeMap<Integer, Long> streakM = streaks.get(j);

            TreeMap<Integer, Long> newStreakM = newStreaks.get(j);
            newStreakM.put(0, newStreakM.getOrDefault(0, 0L) + streakM.getOrDefault(0, 0L));

            Integer expectedStreak = 0;
            if (j < strings.size()) {
                expectedStreak = strings.get(j);
            }

            Long l = streakM.getOrDefault(expectedStreak, 0L);
            TreeMap<Integer, Long> newMap1 = new TreeMap<>(reversedCmp);
            newMap1.put(0, l);
            newStreaks.add(newMap1);
        }

        return newStreaks;
    }
}
