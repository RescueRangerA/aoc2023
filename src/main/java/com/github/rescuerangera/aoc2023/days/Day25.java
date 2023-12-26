package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;
import com.github.rescuerangera.aoc2023.Pair;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 extends Day {

    private Graph<String> parse(BufferedReader reader) {
        return reader
                .lines()
                .map(line ->
                        Arrays.stream(line.split(":"))
                                .map(s ->
                                        Arrays.stream(s.split(" "))
                                                .map(String::trim)
                                                .filter(ss -> !ss.isEmpty())
                                                .toList()
                                ).toList()
                )
                .map(split -> Map.of(split.get(0).get(0), Set.copyOf(split.get(1))))
                .reduce(
                        new Graph<String>(),
                        (graphAccumulator, map) -> {

                            map.forEach((key, value) -> {
                                graphAccumulator.addVertex(key);
                                value.forEach(graphAccumulator::addVertex);
                                value.forEach(v -> {
                                    graphAccumulator.addVertex(v);
                                    graphAccumulator.addEdge(key, v);
                                });
                            });

                            return graphAccumulator;
                        },
                        (graphA, graphB) -> {
                            Graph<String> newGraph = new Graph<>();

                            graphA.getVertices().forEach(newGraph::addVertex);
                            graphB.getVertices().forEach(newGraph::addVertex);

                            graphA.getEdges().forEach(newGraph::addEdge);
                            graphB.getEdges().forEach(newGraph::addEdge);

                            return newGraph;
                        }
                );
    }

    private record Edge<T>(Vertex<T> from, Vertex<T> to) {

    }

    private record Vertex<T>(T data) {

    }


    private static class Graph<T> {
        private final Set<Edge<T>> edges;

        private final Set<Vertex<T>> vertices;

        public Graph() {
            this.edges = new HashSet<>();
            this.vertices = new HashSet<>();
        }

        public static <T> Graph<T> of(Set<Edge<T>> edges, Set<Vertex<T>> vertices) {
            Graph<T> graph = new Graph<>();

            vertices.forEach(graph::addVertex);
            edges.forEach(graph::addEdge);

            return graph;
        }

        public void addEdge(Edge<T> edge) {
            if (!vertices.contains(edge.from()) || !vertices.contains(edge.to())) {
                return;
            }

            edges.add(edge);
        }

        public void addEdge(T from, T to) {
            addEdge(new Edge<>(new Vertex<>(from), new Vertex<>(to)));
        }

        public void addVertex(Vertex<T> vertex) {
            vertices.add(vertex);
        }

        public void addVertex(T value) {
            vertices.add(new Vertex<>(value));
        }

        public Set<Edge<T>> getEdges() {
            return edges;
        }

        public Set<Vertex<T>> getVertices() {
            return vertices;
        }
    }

    private record Subset<T>(Vertex<T> parent, Set<Vertex<T>> items, int rank) {
    }

    private static class KargerMinCut<T> {
        private final Graph<T> graph;

        private Pair<Subset<T>, Subset<T>> lastInvocationSubsets;

        public KargerMinCut(Graph<T> graph) {
            this.graph = graph;
        }

        public void performUntilNumberOfCutsEquals(int numberOfCuts) {
            int current;
            do {
                current = kargerMinCut(graph);
            } while (current != numberOfCuts);
        }

        public long getLastInvocationSubsetSizeMultiplication() {
            return (long) lastInvocationSubsets.getFirst().items().size() * lastInvocationSubsets.getSecond().items().size();
        }

        private int kargerMinCut(Graph<T> graph) {
            int V = graph.getVertices().size();

            List<Subset<T>> subsets = graph.getVertices()
                    .stream()
                    .map(v -> new Subset<>(v, new HashSet<>(Set.of(v)), 0))
                    .collect(Collectors.toList());


            while (subsets.size() > 2) {
                Edge<T> randomEdge = getRandomItemFrom(graph.getEdges());

                Subset<T> subset1 = findSubset(subsets, randomEdge.from());
                Subset<T> subset2 = findSubset(subsets, randomEdge.to());

                if (Objects.equals(subset1, subset2)) {
                    continue;
                }

                combine(subsets, subset1, subset2);
            }

            int cutedges = 0;
            for (Edge<T> edge : graph.getEdges()) {
                Subset<T> subset1 = findSubset(subsets, edge.from());
                Subset<T> subset2 = findSubset(subsets, edge.to());

                if (!Objects.equals(subset1, subset2)) {
                    cutedges++;
                }
            }

            this.lastInvocationSubsets = new Pair<>(
                    subsets.get(0),
                    subsets.get(1)
            );

            return cutedges;
        }

        private <M> M getRandomItemFrom(Set<M> set) {
            if (set == null || set.isEmpty()) {
                throw new IllegalArgumentException("The Set cannot be empty.");
            }
            int randomIndex = new Random().nextInt(set.size());
            int i = 0;
            for (M element : set) {
                if (i == randomIndex) {
                    return element;
                }
                i++;
            }
            throw new IllegalStateException("Something went wrong while picking a random element.");
        }

        private Subset<T> findSubset(List<Subset<T>> subsets, Vertex<T> vertex) {
            return subsets
                    .stream()
                    .filter(subset -> subset.items().contains(vertex))
                    .findFirst()
                    .orElseThrow();
        }

        private void combine(List<Subset<T>> subsets, Subset<T> x, Subset<T> y) {
            subsets.remove(x);
            subsets.remove(y);

            Set<Vertex<T>> items = Stream
                    .concat(x.items().stream(), y.items().stream())
                    .collect(Collectors.toSet());

            if (x.rank() < y.rank()) {
                subsets.add(new Subset<>(y.parent(), items, y.rank()));
            } else if (x.rank() > y.rank()) {
                subsets.add(new Subset<>(x.parent(), items, x.rank()));
            } else {
                subsets.add(new Subset<>(x.parent(), items, x.rank() + 1));
            }
        }
    }

    @Override
    protected String solveA(BufferedReader reader) {
        Graph<String> graph = parse(reader);

        int target = 3;
        KargerMinCut<String> kargerMinCut = new KargerMinCut<>(graph);
        kargerMinCut.performUntilNumberOfCutsEquals(target);

        return String.valueOf(kargerMinCut.getLastInvocationSubsetSizeMultiplication());
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return "";
    }
}
