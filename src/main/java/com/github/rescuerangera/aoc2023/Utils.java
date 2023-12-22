package com.github.rescuerangera.aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.function.Consumer;

public class Utils {

    /**
     * Executes the given consumer function on a BufferedReader object initialized with the input file path corresponding to the given day.
     *
     * @param consumer The consumer function to be executed on the BufferedReader object.
     * @param day      The day corresponding to the input file.
     */
    public static void withInputOfDay(Consumer<BufferedReader> consumer, int day) {
        String filePath = "src/main/resources/dayInputs/day" + day;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            consumer.accept(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Calculates the greatest common divisor (GCD) of two numbers.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The GCD of the two numbers.
     */
    public static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * Calculates the least common multiple (LCM) of two numbers.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The LCM of the two numbers.
     */
    public static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    public static long lcm(long[] input) {
        return Arrays.stream(input).reduce(1L, Utils::lcm);
    }
}
