package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;
import com.github.rescuerangera.aoc2023.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 extends Day {

    @Override
    protected String solveA(BufferedReader reader) {
        char[] rawInstructions = new char[]{};
        LinkedHashMap<String, Pair<String, String>> stringPairMap = new LinkedHashMap<>();
        long counter = 0;

        try {
            rawInstructions = reader.readLine().toCharArray();

            reader.readLine();

            String line;
            Pattern pattern = Pattern.compile("(\\S{3}) = \\((\\S{3}), (\\S{3})\\)");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                if (!matcher.matches()) {
                    throw new RuntimeException();
                }

                stringPairMap.put(
                        matcher.group(1),
                        new Pair<>(
                                matcher.group(2),
                                matcher.group(3)
                        )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String current = "AAA";

        while (!current.equals("ZZZ")) {
            char i = rawInstructions[(int) (counter % rawInstructions.length)];

            if (i == 'L') {
                current = stringPairMap.get(current).getFirst();
            } else if (i == 'R') {
                current = stringPairMap.get(current).getSecond();
            } else {
                throw new RuntimeException();
            }
            counter++;
        }

        return String.valueOf(counter);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        char[] rawInstructions = new char[]{};
        LinkedHashMap<String, Pair<String, String>> stringPairMap = new LinkedHashMap<>();
        long counter = 0;

        try {
            rawInstructions = reader.readLine().toCharArray();

            reader.readLine();

            String line;
            Pattern pattern = Pattern.compile("(\\S{3}) = \\((\\S{3}), (\\S{3})\\)");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                if (!matcher.matches()) {
                    throw new RuntimeException();
                }

                stringPairMap.put(
                        matcher.group(1),
                        new Pair<>(
                                matcher.group(2),
                                matcher.group(3)
                        )
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> currents = stringPairMap.keySet().stream().filter(s -> s.endsWith("A")).toList();
        List<Long> loops = currents.stream().map(i -> 0L).collect(Collectors.toList());
        List<Boolean> done = currents.stream().map(i -> false).collect(Collectors.toList());
        counter++;

        while (!currents.stream().allMatch(s -> s.endsWith("Z"))) {
            char i = rawInstructions[(int) (counter % rawInstructions.length)];

            if (i == 'L') {
                currents = currents.stream().parallel().map(current -> stringPairMap.get(current).getFirst()).toList();
            } else if (i == 'R') {
                currents = currents.stream().parallel().map(current -> stringPairMap.get(current).getSecond()).toList();
            } else {
                throw new RuntimeException();
            }
            counter++;

            for (int j = 0; j < currents.size(); j++) {
                if (currents.get(j).endsWith("Z")) {
                    long hhh = loops.get(j);

                    if (hhh == 0) {
                        loops.set(j, counter);
                    } else if (!done.get(j) && counter - hhh != hhh) {
                        loops.set(j, counter - hhh);
                        done.set(j, true);
                    }
                }
            }

            if (done.stream().allMatch(k -> k)) {
                break;
            }
        }

        return loops.stream().reduce(1L, Utils::lcm).toString();
    }
}
