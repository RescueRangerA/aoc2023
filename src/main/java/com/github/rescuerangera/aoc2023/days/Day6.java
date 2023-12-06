package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Interval;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day6 extends Day {
    record Race(Long time, Long distance) {
    }

    private List<Race> parseRaces(BufferedReader reader) throws IOException {
        String line;

        line = reader.readLine();
        long[] time = Arrays.stream(line.split(": ")[1].split("\\s+"))
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .toArray();


        line = reader.readLine();
        long[] distance = Arrays.stream(line.split(": ")[1].split("\\s+"))
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .toArray();

        return IntStream.range(0, time.length).boxed().map(i -> new Race(time[i], distance[i])).toList();
    }


    private Interval calculateInterval(Race race) {
        double sqrt = Math.sqrt(race.time * race.time - 4 * race.distance);
        double rootLeft = ((double) race.time - sqrt) / 2;
        double rootRight = ((double) race.time + sqrt) / 2;

        long left = rootLeft % 1 != 0 ? (long) Math.ceil(rootLeft) : (long) rootLeft + 1;
        long right = rootRight % 1 != 0 ? (long) Math.ceil(rootRight) : (long) rootRight; // not inclusive

        return new Interval(left, right);
    }

    @Override
    protected String solveA(BufferedReader reader) {
        List<Race> races = new ArrayList<>();

        try {
            races = parseRaces(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return races
                .stream()
                .map(this::calculateInterval)
                .map(Interval::getLength)
                .reduce(1L, (product, value) -> product * value)
                .toString();
    }

    private Race parseRace(BufferedReader reader) throws IOException {
        String line;

        line = reader.readLine();
        long time = Long.parseLong(
                Arrays.stream(line.split(": ")[1].split("\\s+"))
                        .filter(s -> !s.isEmpty())
                        .reduce(new StringBuffer(), StringBuffer::append, StringBuffer::append)
                        .toString()
        );


        line = reader.readLine();
        long distance = Long.parseLong(
                Arrays.stream(line.split(": ")[1].split("\\s+"))
                        .filter(s -> !s.isEmpty())
                        .reduce(new StringBuffer(), StringBuffer::append, StringBuffer::append)
                        .toString()
        );

        return new Race(time, distance);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        Race race = null;

        try {
            race = parseRace(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.of(race)
                .filter(Objects::nonNull)
                .map(this::calculateInterval)
                .map(Interval::getLength)
                .reduce(1L, (product, value) -> product * value)
                .toString();
    }
}
