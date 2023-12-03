package com.github.rescuerangera.aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.function.Consumer;

public class Utils {
    public static void withInputOfDay(Consumer<BufferedReader> consumer, int day) {
        String filePath = "src/main/resources/dayInputs/day" + day;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            consumer.accept(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
