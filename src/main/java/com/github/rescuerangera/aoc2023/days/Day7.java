package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day7 extends Day {

    private static class Hand implements Comparable<Hand> {
        private List<Integer> values;

        private int strength;

        private final static Map<Character, Integer> cartValueMap = Map.ofEntries(
                Map.entry('2', 0),
                Map.entry('3', 1),
                Map.entry('4', 2),
                Map.entry('5', 3),
                Map.entry('6', 4),
                Map.entry('7', 5),
                Map.entry('8', 6),
                Map.entry('9', 7),
                Map.entry('T', 8),
                Map.entry('J', 9),
                Map.entry('Q', 10),
                Map.entry('K', 11),
                Map.entry('A', 12)
        );

        private final static Character JOKER = 'J';

        private final static Map<Character, Integer> cartValueMapWithJoker = Map.ofEntries(
                Map.entry(JOKER, 0),
                Map.entry('2', 1),
                Map.entry('3', 2),
                Map.entry('4', 3),
                Map.entry('5', 4),
                Map.entry('6', 5),
                Map.entry('7', 6),
                Map.entry('8', 7),
                Map.entry('9', 8),
                Map.entry('T', 9),
                Map.entry('Q', 10),
                Map.entry('K', 11),
                Map.entry('A', 12)
        );

        private Hand() {
            values = new ArrayList<>();
            strength = 0;
        }

        public static Hand fromRegularHand(char[] rawHand) {
            if (rawHand.length != 5) {
                throw new IllegalArgumentException();
            }

            Hand hand = new Hand();

            hand.values = IntStream.range(0, rawHand.length)
                    .mapToObj(i -> rawHand[i])
                    .map(cartValueMap::get)
                    .toList();

            hand.strength = calculateStrength(rawHand);

            return hand;
        }

        public static Hand fromJokerHand(char[] rawHand) {
            if (rawHand.length != 5) {
                throw new IllegalArgumentException();
            }

            Hand hand = new Hand();

            hand.values = IntStream.range(0, rawHand.length)
                    .mapToObj(i -> rawHand[i])
                    .map(cartValueMapWithJoker::get)
                    .toList();

            hand.strength = calculateStrengthWithJoker(rawHand);

            return hand;
        }

        public int getStrength() {
            return strength;
        }

        public List<Integer> getValues() {
            return values;
        }

        @Override
        public int compareTo(Hand o) {
            return Comparator
                    .comparingInt(Hand::getStrength)
                    .thenComparing(
                            (a, b) -> {
                                for (int i = 0; i < a.values.size(); i++) {
                                    int cmp = a.getValues().get(i).compareTo(b.getValues().get(i));

                                    if (cmp != 0) {
                                        return cmp;
                                    }
                                }

                                return 0;
                            }
                    )
                    .compare(this, o);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Hand hand)) return false;
            return strength == hand.strength && Objects.equals(values, hand.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values, strength);
        }

        private static int calculateStrength(char[] hand) {
            if (hand.length != 5) {
                throw new IllegalArgumentException();
            }

            Map<Character, Long> occurrenceCount = IntStream.range(0, hand.length)
                    .mapToObj(i -> hand[i])
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            return calculateStrengthFromOccurrence(occurrenceCount);
        }

        private static int calculateStrengthWithJoker(char[] hand) {
            if (hand.length != 5) {
                throw new IllegalArgumentException();
            }

            Map<Character, Long> occurrenceCount = IntStream.range(0, hand.length)
                    .mapToObj(i -> hand[i])
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Long jokerCount = occurrenceCount.remove(JOKER);

            if (jokerCount != null) {
                if (occurrenceCount.entrySet().isEmpty()) {
                    return 6; // All JOKERS detected
                }

                TreeSet<Map.Entry<Character, Long>> entries = new TreeSet<>(Comparator.comparingLong(Map.Entry::getValue));
                entries.addAll(occurrenceCount.entrySet());
                occurrenceCount.put(entries.last().getKey(), entries.last().getValue() + jokerCount);
            }


            return calculateStrengthFromOccurrence(occurrenceCount);
        }

        private static int calculateStrengthFromOccurrence(Map<Character, Long> occurrenceCount) {
            if (occurrenceCount.keySet().size() == 5) {
                return 0;
            } else if (occurrenceCount.keySet().size() == 4) {
                return 1;
            } else if (occurrenceCount.keySet().size() == 3 && occurrenceCount.values().stream().filter(v -> v == 2).count() == 2) {
                return 2;
            } else if (occurrenceCount.keySet().size() == 3 && occurrenceCount.values().stream().filter(v -> v == 3).count() == 1) {
                return 3;
            } else if (occurrenceCount.keySet().size() == 2 && occurrenceCount.values().stream().filter(v -> v == 3).count() == 1) {
                return 4;
            } else if (occurrenceCount.keySet().size() == 2 && occurrenceCount.values().stream().filter(v -> v == 4).count() == 1) {
                return 5;
            } else {
                return 6;
            }
        }
    }

    private String solveWithHandFunction(BufferedReader reader, Function<String, Hand> handFunction) {
        TreeSet<Hand> hands = new TreeSet<>();
        Map<Hand, Long> bids = new HashMap<>();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                Hand hand = handFunction.apply(split[0]);
                Long bid = Long.valueOf(split[1]);

                hands.add(hand);
                bids.put(hand, bid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AtomicLong rank = new AtomicLong(1L);

        return hands
                .stream()
                .reduce(
                        0L,
                        (sum, hand) -> sum + bids.get(hand) * rank.getAndIncrement(),
                        Long::sum
                )
                .toString();
    }

    @Override
    protected String solveA(BufferedReader reader) {
        return solveWithHandFunction(reader, (s -> Hand.fromRegularHand(s.toCharArray())));
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return solveWithHandFunction(reader, (s -> Hand.fromJokerHand(s.toCharArray())));
    }
}
