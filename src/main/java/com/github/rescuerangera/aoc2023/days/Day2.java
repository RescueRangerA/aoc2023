package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class Day2 extends Day {

    enum Color {
        RED("red"), GREEN("green"), BLUE("blue");

        private final String title;

        Color(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public static Color fromTitle(String title) {
            for (Color color : Color.values()) {
                if (color.getTitle().equalsIgnoreCase(title)) {
                    return color;
                }
            }
            throw new IllegalArgumentException(title);
        }
    }

    private static final EnumMap<Color, Integer> colors = new EnumMap<>(Map.of(
            Color.RED, 12,
            Color.GREEN, 13,
            Color.BLUE, 14
    ));

    @Override
    protected String solveA(BufferedReader reader) {
        return reader.lines().reduce(0L, (sum, line) -> {
            String[] split = line.split(": ");

            String game = split[0];
            long gameId = Long.parseLong(game.split(" ")[1]);

            String rawGames = split[1];

            boolean gameIsPossible = true;

            for (String rawRounds : rawGames.split("; ")) {
                String[] rawCountAndColors = rawRounds.split(", ");

                for (String rawCountAndColor : rawCountAndColors) {
                    String[] rawCountAndColorSplit = rawCountAndColor.trim().split(" ");

                    long count = Long.parseLong(rawCountAndColorSplit[0]);
                    Color color = Color.fromTitle(rawCountAndColorSplit[1]);

                    if (count > colors.get(color)) {
                        gameIsPossible = false;
                        break;
                    }
                }

                if (!gameIsPossible) {
                    break;
                }
            }

            if (gameIsPossible) {
                sum += gameId;
            }

            return sum;
        }, Long::sum).toString();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return reader.lines().reduce(0L, (sum, line) -> {
            String[] split = line.split(": ");

            String game = split[0];
            Long gameId = Long.valueOf(game.split(" ")[1]);

            String rawGames = split[1];

            EnumMap<Color, Long> maxs = new EnumMap<>(Color.class);
            Arrays.stream(Color.values()).forEach(c -> maxs.put(c, 0L));

            for (String rawRounds : rawGames.split("; ")) {
                String[] rawCountAndColors = rawRounds.split(", ");

                for (String rawCountAndColor : rawCountAndColors) {
                    String[] rawCountAndColorSplit = rawCountAndColor.trim().split(" ");

                    Long count = Long.valueOf(rawCountAndColorSplit[0]);
                    Color color = Color.fromTitle(rawCountAndColorSplit[1]);

                    if (count > maxs.get(color)) {
                        maxs.put(color, count);
                    }
                }
            }

            return sum + maxs.values().stream().reduce(1L, (a, b) -> a * b);
        }, Long::sum).toString();
    }
}
