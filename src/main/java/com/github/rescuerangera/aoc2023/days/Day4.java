package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 extends Day {

    private Pair<Set<Integer>, Set<Integer>> parseCardsFromLine(String line) {
        String[] tmpSplit = line.split(":");

        String[] splitCards = tmpSplit[1].trim().split("\\|");

        Set<Integer> winningCards = Arrays
                .stream(splitCards[0].trim().split("\\s+"))
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        Set<Integer> cards = Arrays
                .stream(splitCards[1].trim().split("\\s+"))
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        return new Pair<>(winningCards, cards);
    }

    @Override
    protected String solveA(BufferedReader reader) {
        return reader
                .lines()
                .reduce(0L, (sum, line) -> {
                    long winningNumbersCount = getWinningNumbersCount(line);

                    if (winningNumbersCount > 0) {
                        return sum + (long) Math.pow(2, winningNumbersCount - 1);
                    } else {
                        return sum;
                    }
                }, Long::sum).toString();
    }

    private void insertOrSum(List<Long> list, int index, Long value) {
        if (index >= list.size()) {
            list.add(index, value);
        } else {
            list.set(index, list.get(index) + value);
        }
    }

    private int getWinningNumbersCount(String line) {
        Pair<Set<Integer>, Set<Integer>> parsed = parseCardsFromLine(line);
        Set<Integer> winningCards = parsed.getFirst();
        Set<Integer> cards = parsed.getSecond();
        cards.retainAll(winningCards);
        return cards.size();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        AtomicInteger index = new AtomicInteger();

        return reader
                .lines()
                .map(line -> new AbstractMap.SimpleEntry<>(index.getAndIncrement(), line))
                .collect(
                        Collector.of(
                                ArrayList<Long>::new,
                                (list, entry) -> {
                                    int currentIndex = entry.getKey();
                                    String line = entry.getValue();

                                    insertOrSum(list, currentIndex, 1L);
                                    long currentCardCount = list.get(currentIndex);

                                    int winningNumbersCount = getWinningNumbersCount(line);

                                    IntStream
                                            .range(currentIndex + 1, currentIndex + 1 + winningNumbersCount)
                                            .forEach(rangeIndex -> insertOrSum(list, rangeIndex, currentCardCount));
                                },
                                (list1, list2) -> {
                                    list1.addAll(list2);
                                    return list1;
                                },
                                list -> list
                        )
                )
                .stream()
                .limit(index.get() + 1)
                .reduce(0L, Long::sum)
                .toString();
    }
}
