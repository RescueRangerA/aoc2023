package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Interval;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 extends Day {
    private enum Category {
        EXTREME, MUSICAL, AERODYNAMIC, SHINY;

        private static Category fromString(String s) {
            return switch (s) {
                case "a" -> Category.AERODYNAMIC;
                case "x" -> Category.EXTREME;
                case "m" -> Category.MUSICAL;
                case "s" -> Category.SHINY;
                default -> throw new IllegalArgumentException(s);
            };
        }
    }

    private record WorkFlow(String name, List<SingleWorkFlow> singleWorkFlows, String elseValue) {
    }

    private record SingleWorkFlow(Category category, char sign, int value, String positive) {

        private Predicate<Part> makeCondition() {
            return part -> switch (sign) {
                case '<' -> part.get(category) < value;
                case '>' -> part.get(category) > value;
                default -> throw new IllegalArgumentException();
            };
        }

        public String invoke(Part part) {
            return makeCondition().test(part) ? positive : null;
        }
    }

    private record Part(int x, int m, int a, int s) {
        public int get(Category category) {
            return switch (category) {
                case SHINY -> s();
                case EXTREME -> x();
                case MUSICAL -> m();
                case AERODYNAMIC -> a();
            };
        }
    }

    private Map<String, WorkFlow> parseWorkFlows(BufferedReader reader) {
        Map<String, WorkFlow> singleWorkflows = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\S+)\\{(\\S+),([a-zAR]+)}");
        Pattern patternSingleWorkFlow = Pattern.compile("([a-z])([<>])(\\d+):([a-zAR]+)");

        try {
            String line;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Matcher matcher = pattern.matcher(line);

                matcher.matches();

                String workflowName = matcher.group(1);

                List<SingleWorkFlow> singleWorkFlows = new ArrayList<>();
                for (String rawSingleWorkFlow : matcher.group(2).split(",")) {
                    Matcher matcherSingleWorkflow = patternSingleWorkFlow.matcher(rawSingleWorkFlow);
                    matcherSingleWorkflow.matches();
                    Category category = Category.fromString(matcherSingleWorkflow.group(1));
                    char sign = matcherSingleWorkflow.group(2).charAt(0);
                    int value = Integer.parseInt(matcherSingleWorkflow.group(3));
                    String positive = matcherSingleWorkflow.group(4);

                    singleWorkFlows.add(new SingleWorkFlow(category, sign, value, positive));
                }

                String elseValue = matcher.group(3);

                singleWorkflows.put(workflowName, new WorkFlow(workflowName, singleWorkFlows, elseValue));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return singleWorkflows;
    }

    protected String solveA(BufferedReader reader) {
        List<Part> parts = new ArrayList<>();

        Map<String, WorkFlow> singleWorkflows = parseWorkFlows(reader);

        try {
            String line;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] split;

                split = line.substring(0, line.length() - 1).split(",");

                parts.add(
                        new Part(
                                Integer.parseInt(split[0].split("=")[1]),
                                Integer.parseInt(split[1].split("=")[1]),
                                Integer.parseInt(split[2].split("=")[1]),
                                Integer.parseInt(split[3].split("=")[1])
                        )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return parts
                .stream()
                .map(part -> {
                    String outcome = "in";

                    while (!outcome.equals("A") && !outcome.equals("R")) {
                        WorkFlow workFlow = singleWorkflows.get(outcome);
                        for (SingleWorkFlow singleWorkFlow : workFlow.singleWorkFlows()) {
                            if ((outcome = singleWorkFlow.invoke(part)) != null) {
                                break;
                            }
                        }

                        if (outcome == null) {
                            outcome = workFlow.elseValue();
                        }
                    }

                    return outcome.equals("A") ? part.x() + part.m() + part.s() + part.a() : 0;
                })
                .reduce(
                        0L,
                        Long::sum,
                        Long::sum
                )
                .toString();
    }

    private static <T> LinkedList<T> appendWithCreation(List<T> list, T value) {
        LinkedList<T> newList = new LinkedList<>(list);
        newList.add(value);

        return newList;
    }

    private static <K, V> HashMap<K, V> putWithCreation(Map<K, V> map, K key, V value) {
        HashMap<K, V> newMap = new HashMap<>(map);
        newMap.put(key, value);

        return newMap;
    }

    @Override
    protected String solveB(BufferedReader reader) {
        Map<String, WorkFlow> singleWorkflows = parseWorkFlows(reader);

        final int MIN = 1;
        final int MAX = 4000;

        Map<Category, Interval> intervalMap = new HashMap<>(
                Map.of(
                        Category.EXTREME, new Interval(MIN, MAX),
                        Category.MUSICAL, new Interval(MIN, MAX),
                        Category.AERODYNAMIC, new Interval(MIN, MAX),
                        Category.SHINY, new Interval(MIN, MAX)
                )
        );

        Queue<Map<Category, Interval>> intervalMaps = new LinkedList<>();
        intervalMaps.add(intervalMap);

        Queue<LinkedList<String>> op = new LinkedList<>();
        op.add(new LinkedList<>(List.of("in")));

        long result = 0L;
        while (!op.isEmpty()) {
            LinkedList<String> ops = op.poll();
            Map<Category, Interval> intervalMapPoll = intervalMaps.poll();
            String outcome = ops.peekLast();

            if (outcome.equals("A")) {
                result += intervalMapPoll.values()
                        .stream()
                        .map(v -> v.getLength() + 1)
                        .reduce(1L, (prod, l) -> prod * l);
                continue;
            } else if (outcome.equals("R")) {
                continue;
            }

            WorkFlow workFlow = singleWorkflows.get(outcome);

            Map<Category, Interval> intervalNegativeCapacitor = new HashMap<>(intervalMapPoll);

            for (SingleWorkFlow singleWorkFlow : workFlow.singleWorkFlows()) {
                Interval interval = intervalNegativeCapacitor.get(singleWorkFlow.category());
                Interval intervalPositive;
                Interval intervalNegative;

                if (singleWorkFlow.sign() == '<') {
                    intervalPositive = new Interval(1, singleWorkFlow.value() - MIN);
                    intervalNegative = new Interval(singleWorkFlow.value(), MAX);
                } else if (singleWorkFlow.sign() == '>') {
                    intervalPositive = new Interval(singleWorkFlow.value() + 1, MAX);
                    intervalNegative = new Interval(MIN, singleWorkFlow.value());
                } else {
                    throw new IllegalArgumentException(String.valueOf(singleWorkFlow.sign()));
                }

                Interval intersection = Interval.intersection(interval, intervalPositive);
                if (intersection != null) {
                    op.add(appendWithCreation(ops, singleWorkFlow.positive()));
                    intervalMaps.add(putWithCreation(intervalNegativeCapacitor, singleWorkFlow.category(), intersection));
                }

                Interval intersectionElse = Interval.intersection(interval, intervalNegative);
                intervalNegativeCapacitor.put(singleWorkFlow.category(), intersectionElse);
                if (intersectionElse == null) {
                    break;
                }
            }

            if (!intervalNegativeCapacitor.containsValue(null)) {
                op.add(appendWithCreation(ops, workFlow.elseValue()));
                intervalMaps.add(new HashMap<>(intervalNegativeCapacitor));
            }
        }

        return String.valueOf(result);

    }
}

