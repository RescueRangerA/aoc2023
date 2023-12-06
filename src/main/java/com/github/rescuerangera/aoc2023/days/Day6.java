package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

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


    private long[] calculateInterval(Race race) {
        double sqrt = Math.sqrt(race.time * race.time - 4 * race.distance);
        double root1 = ((double) race.time + sqrt) / 2;
        double root2 = ((double) race.time - sqrt) / 2;

        long r2;
        if (root2 % 1 != 0) {
            r2 = (long) Math.ceil(root2);
        } else {
            r2 = (long) root2 + 1;
        }

        long r1;
        if (root1 % 1 != 0) {
            r1 = (long) Math.floor(root1);
        } else {
            r1 = (long) root1 - 1;
        }


        return new long[]{r2, r1};
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
                .map(i -> i[1] - i[0] + 1)
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
                .map(i -> i[1] - i[0] + 1)
                .reduce(1L, (product, value) -> product * value)
                .toString();
    }
}
