package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Interval;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day5 extends Day {

    private record Layer(Long source, Long destination, Long length) {
    }

    private List<Layer> buildMap(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        List<Layer> result = new LinkedList<>();

        while (line != null && !line.isEmpty()) {
            String[] split = line.split("\\s+");

            result.add(new Layer(
                    Long.parseLong(split[1]),
                    Long.parseLong(split[0]),
                    Long.parseLong(split[2])
            ));

            line = reader.readLine();
        }

        result.sort(Comparator.comparingLong(a -> a.source));

        return result;
    }

    private Long locateDestination(List<Layer> map, Long source) {
        Layer lastLayer = map.get(map.size() - 1);

        if (source < map.get(0).source || source > lastLayer.source + lastLayer.length) {
            return source;
        }

        for (int i = 0; i < map.size() - 1; i++) {
            Layer a = map.get(i);
            Layer b = map.get(i + 1);

            if (a.source <= source && source < b.source) {
                long diff = source - a.source;

                if (diff <= a.length) {
                    return a.destination + diff;
                } else {
                    return source;
                }
            }
        }

        long diff = source - lastLayer.source;
        if (diff <= lastLayer.length) {
            return lastLayer.destination + diff;
        } else {
            return source;
        }
    }

    @Override
    protected String solveA(BufferedReader reader) {
        Set<Long> seeds = new HashSet<>();
        List<List<Layer>> layers = new LinkedList<>();

        try {
            seeds = Arrays.stream(reader.readLine().split(":")[1].trim().split("\\s+"))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());


            reader.readLine();
            reader.readLine();

            for (int i = 0; i < 7; i++) {
                layers.add(buildMap(reader));
                reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return seeds
                .stream()
                .map(seed -> layers.stream().reduce(seed, (source, layer) -> locateDestination(layer, source), Long::sum))
                .min(Long::compareTo)
                .get()
                .toString();
    }

    static class MapLayer {
        private final TreeMap<Interval, Interval> intervalMap;

        public MapLayer(TreeMap<Interval, Interval> intervalMap) {
            this.intervalMap = intervalMap;

            if (this.intervalMap.firstKey().getStart() > 0) {
                this.intervalMap.put(
                        new Interval(0, this.intervalMap.firstKey().getStart()),
                        new Interval(0, this.intervalMap.firstKey().getStart())
                );
            }

            if (this.intervalMap.lastKey().getEnd() < Long.MAX_VALUE) {
                this.intervalMap.put(
                        new Interval(this.intervalMap.lastKey().getEnd(), Long.MAX_VALUE),
                        new Interval(this.intervalMap.lastKey().getEnd(), Long.MAX_VALUE)
                );
            }

            Iterator<Map.Entry<Interval, Interval>> iterator = this.intervalMap.entrySet().iterator();
            Map.Entry<Interval, Interval> prev = iterator.next();
            Map.Entry<Interval, Interval> current;

            Map<Interval, Interval> intervalsToAdd = new HashMap<>();

            while (iterator.hasNext()) {
                current = iterator.next();

                if (prev.getKey().getEnd() != current.getKey().getStart()) {
                    intervalsToAdd.put(
                            new Interval(prev.getKey().getEnd(), current.getKey().getStart()),
                            new Interval(prev.getKey().getEnd(), current.getKey().getStart())
                    );
                }

                prev = current;
            }

            this.intervalMap.putAll(intervalsToAdd);
        }

        public Map<Interval, Interval> processInterval(Interval interval) {
            Map<Interval, Interval> iim = new HashMap<>();
            boolean found = false;

            for (Map.Entry<Interval, Interval> entry : intervalMap.entrySet()) {
                if (entry.getKey().getStart() <= interval.getStart() && interval.getEnd() < entry.getKey().getEnd()) {
                    iim.put(
                            new Interval(
                                    interval.getStart(),
                                    interval.getEnd()
                            ),
                            new Interval(
                                    interval.getStart() + entry.getValue().getStart() - entry.getKey().getStart(),
                                    interval.getEnd() + entry.getValue().getStart() - entry.getKey().getStart()
                            )
                    );
                    break;
                } else if (entry.getKey().getStart() <= interval.getStart() && interval.getStart() < entry.getKey().getEnd()) {
                    iim.put(
                            new Interval(
                                    interval.getStart(),
                                    entry.getKey().getEnd()
                            ),
                            new Interval(
                                    interval.getStart() + entry.getValue().getEnd() - entry.getKey().getEnd(),
                                    entry.getValue().getEnd()
                            )
                    );
                    found = true;
                } else if (entry.getKey().getStart() <= interval.getEnd() && interval.getEnd() < entry.getKey().getEnd()) {
                    iim.put(
                            new Interval(
                                    entry.getKey().getStart(),
                                    interval.getEnd()
                            ),
                            new Interval(
                                    entry.getValue().getStart(),
                                    interval.getEnd() + entry.getValue().getStart() - entry.getKey().getStart()
                            )
                    );
                    break;
                } else if (found) {
                    iim.put(
                            entry.getKey(),
                            entry.getValue()
                    );
                }
            }

            return iim;
        }
    }

    @Override
    protected String solveB(BufferedReader reader) {
        List<Interval> rangeSeeds = new ArrayList<>();
        List<MapLayer> layers = new ArrayList<>();

        try {
            List<Long> rawSeeds = Arrays.stream(reader.readLine().split(":")[1].trim().split("\\s+"))
                    .map(Long::parseLong)
                    .toList();

            rangeSeeds = IntStream
                    .range(0, rawSeeds.size())
                    .mapToObj(operand -> {
                        if (operand % 2 != 0) {
                            return new Interval(
                                    rawSeeds.get(operand - 1),
                                    rawSeeds.get(operand - 1) + rawSeeds.get(operand)
                            );
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();


            reader.readLine();
            reader.readLine();

            for (int i = 0; i < 7; i++) {
                layers.add(buildMapLayer(reader));
                reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(
                layers.stream().reduce(
                                new TreeSet<>(rangeSeeds),
                                (list, mapLayer) ->
                                        new TreeSet<>(
                                                list.stream().reduce(
                                                                new HashMap<Interval, Interval>(),
                                                                (result, interval) -> {
                                                                    result.putAll(mapLayer.processInterval(interval));

                                                                    return result;
                                                                },
                                                                (a, b) -> {
                                                                    a.putAll(b);

                                                                    return b;
                                                                }
                                                        )
                                                        .values()
                                        ),
                                (a, b) -> {
                                    a.addAll(b);

                                    return a;
                                }
                        )
                        .first()
                        .getStart()
        );
    }

    private MapLayer buildMapLayer(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        TreeMap<Interval, Interval> result = new TreeMap<>();

        while (line != null && !line.isEmpty()) {
            String[] split = line.split("\\s+");

            long source = Long.parseLong(split[1]);
            long destination = Long.parseLong(split[0]);
            long length = Long.parseLong(split[2]);

            result.put(
                    new Interval(source, source + length),
                    new Interval(destination, destination + length)
            );

            line = reader.readLine();
        }

        return new MapLayer(result);
    }
}
