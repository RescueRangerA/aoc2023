package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.rescuerangera.aoc2023.Utils.lcm;
import static java.util.stream.Collectors.toMap;

public class Day20 extends Day {

    private static class FlipFlop {
        private boolean status;

        private final List<String> outputs;

        public FlipFlop(List<String> outputs) {
            status = false;
            this.outputs = outputs;
        }

        public List<String> getOutputs() {
            return outputs;
        }

        public int processSignal(int signal) {
            if (signal != -1) {
                return 0;
            }

            status = !status;

            return !status ? -1 : 1;
        }
    }

    private static class Conjunction {
        private final Map<String, Integer> inputs;

        private final List<String> outputs;

        public Conjunction(List<String> outputs) {
            inputs = new HashMap<>();
            this.outputs = outputs;
        }

        public List<String> getOutputs() {
            return outputs;
        }

        public void addInput(String input) {
            inputs.put(input, -1);
        }

        public int processSignal(String input, int signal) {
            inputs.put(input, signal);

            return inputs.values().stream().allMatch(s -> s == 1) ? -1 : 1;
        }
    }

    private record Broadcaster(List<String> outputs) {
    }

    private record Signal(String from, String dest, int signal) {

    }

    @Override
    protected String solveA(BufferedReader reader) {
        Broadcaster broadcaster = null;

        Map<String, FlipFlop> flipFlops = new HashMap<>();
        Map<String, Conjunction> conjunctions = new HashMap<>();

        try {
            String line;
            String[] split;
            while ((line = reader.readLine()) != null) {
                split = line.split(" -> ");
                List<String> outputs = Arrays.stream(split[1].split(",")).map(String::trim).toList();

                if (line.startsWith("br")) {
                    broadcaster = new Broadcaster(outputs);
                } else if (line.startsWith("%")) {
                    split = line.split(" -> ");

                    String name = split[0].substring(1).trim();
                    flipFlops.put(name, new FlipFlop(outputs));
                } else if (line.startsWith("&")) {
                    split = line.split(" -> ");

                    String name = split[0].substring(1).trim();
                    conjunctions.put(name, new Conjunction(outputs));
                }
            }

            flipFlops.forEach((key, value) -> value.getOutputs().forEach(o -> {
                if (conjunctions.containsKey(o)) {
                    conjunctions.get(o).addInput(key);
                }
            }));

            conjunctions.forEach((key, value) -> value.getOutputs().forEach(o -> {
                if (conjunctions.containsKey(o)) {
                    conjunctions.get(o).addInput(key);
                }
            }));
        } catch (IOException e) {

        }

        Objects.requireNonNull(broadcaster);

        long counterLow = 0L;
        long counterHigh = 0L;

        for (int i = 0; i < 1000; i++) {
            Queue<Signal> operations = new LinkedList<>();
            broadcaster.outputs().forEach(o -> operations.add(new Signal("", o, -1)));

            counterLow++;

            while (!operations.isEmpty()) {
                Signal signal = operations.poll();

                switch (signal.signal()) {
                    case 1:
                        counterHigh++;
                        break;
                    case -1:
                        counterLow++;
                        break;
                    default:
                        throw new RuntimeException(String.valueOf(signal.signal()));
                }

                if (flipFlops.containsKey(signal.dest())) {
                    FlipFlop flipFlop = flipFlops.get(signal.dest());

                    int newSignal = flipFlop.processSignal(signal.signal());

                    if (newSignal == 0) {
                        continue;
                    }

                    flipFlop.getOutputs().forEach(o -> operations.add(new Signal(signal.dest(), o, newSignal)));
                } else if (conjunctions.containsKey(signal.dest())) {
                    Conjunction conjunction = conjunctions.get(signal.dest());

                    int newSignal = conjunction.processSignal(signal.from(), signal.signal());

                    if (newSignal == 0) {
                        continue;
                    }

                    conjunction.getOutputs().forEach(o -> operations.add(new Signal(signal.dest(), o, newSignal)));
                }
            }
        }

        return String.valueOf(counterLow * counterHigh);
    }

    @Override
    protected String solveB(BufferedReader reader) {
        Broadcaster broadcaster = null;

        Map<String, FlipFlop> flipFlops = new HashMap<>();
        Map<String, Conjunction> conjunctions = new HashMap<>();

        try {
            String line;
            String[] split;
            while ((line = reader.readLine()) != null) {
                split = line.split(" -> ");
                List<String> outputs = Arrays.stream(split[1].split(",")).map(String::trim).toList();

                if (line.startsWith("br")) {
                    broadcaster = new Broadcaster(outputs);
                } else if (line.startsWith("%")) {
                    split = line.split(" -> ");

                    String name = split[0].substring(1).trim();
                    flipFlops.put(name, new FlipFlop(outputs));
                } else if (line.startsWith("&")) {
                    split = line.split(" -> ");

                    String name = split[0].substring(1).trim();
                    conjunctions.put(name, new Conjunction(outputs));
                }
            }

            flipFlops.forEach((key, value) -> value.getOutputs().forEach(o -> {
                if (conjunctions.containsKey(o)) {
                    conjunctions.get(o).addInput(key);
                }
            }));

            conjunctions.forEach((key, value) -> value.getOutputs().forEach(o -> {
                if (conjunctions.containsKey(o)) {
                    conjunctions.get(o).addInput(key);
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(broadcaster);

        long result = 0L;

        String rxInput = Stream.concat(
                flipFlops.entrySet().stream().filter(e -> e.getValue().getOutputs().contains("rx")).map(Map.Entry::getKey),
                conjunctions.entrySet().stream().filter(e -> e.getValue().getOutputs().contains("rx")).map(Map.Entry::getKey)
        ).findAny().get();

        Map<String, Long> lcms = Stream.concat(
                flipFlops.entrySet().stream().filter(e -> e.getValue().getOutputs().contains(rxInput)).map(Map.Entry::getKey),
                conjunctions.entrySet().stream().filter(e -> e.getValue().getOutputs().contains(rxInput)).map(Map.Entry::getKey)
        ).collect(toMap(e -> e, e -> 0L));

        for (long i = 1; i < Long.MAX_VALUE; i++) {
            Queue<Signal> operations = new LinkedList<>();
            broadcaster.outputs().forEach(o -> operations.add(new Signal("", o, -1)));

            while (!operations.isEmpty()) {
                Signal signal = operations.poll();
                boolean isHigh = signal.signal() > 0;

                if (isHigh && lcms.containsKey(signal.from())) {
                    lcms.put(signal.from(), i);
                    if (lcms.values().stream().allMatch(e -> e != 0L)) {
                        result = lcm(lcms.values().stream().mapToLong(e -> e).toArray());
                        break;
                    }
                }

                if (flipFlops.containsKey(signal.dest())) {
                    FlipFlop flipFlop = flipFlops.get(signal.dest());

                    int newSignal = flipFlop.processSignal(signal.signal());

                    if (newSignal == 0) {
                        continue;
                    }

                    flipFlop.getOutputs().forEach(o -> operations.add(new Signal(signal.dest(), o, newSignal)));
                } else if (conjunctions.containsKey(signal.dest())) {
                    Conjunction conjunction = conjunctions.get(signal.dest());

                    int newSignal = conjunction.processSignal(signal.from(), signal.signal());

                    if (newSignal == 0) {
                        continue;
                    }

                    conjunction.getOutputs().forEach(o -> operations.add(new Signal(signal.dest(), o, newSignal)));
                }
            }

            if (result > 0) {
                break;
            }
        }

        return String.valueOf(result);
    }
}
