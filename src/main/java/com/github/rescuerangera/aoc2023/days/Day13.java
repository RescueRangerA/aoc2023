package com.github.rescuerangera.aoc2023.days;

import com.github.rescuerangera.aoc2023.Day;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day13 extends Day {

    private char[][] parseGrid(BufferedReader reader) throws IOException {
        String line;

        List<String> lines = new ArrayList<>();

        while (true) {
            line = reader.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            lines.add(line);
        }

        return lines
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    private List<char[][]> parseGrids(BufferedReader reader) {
        List<char[][]> grids = new ArrayList<>();

        try {

            while (true) {
                char[][] grid = parseGrid(reader);

                if (grid.length == 0) {
                    break;
                }

                grids.add(grid);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return grids;
    }

    private static boolean rowsAreEquals(char[][] grid, int aRowIndex, int bRowIndex, AtomicInteger errorsCount) {
        boolean match = true;

        for (int i = 0; i < grid[0].length; i++) {
            if (grid[aRowIndex][i] != grid[bRowIndex][i]) {
                if (errorsCount.get() > 0) {
                    errorsCount.decrementAndGet();
                } else {
                    match = false;
                    break;
                }
            }
        }

        return match;
    }

    private static boolean columnsAreEquals(char[][] grid, int aColumnIndex, int bColumnIndex, AtomicInteger errorsCount) {
        boolean match = true;

        for (int i = 0; i < grid.length; i++) {
            if (grid[i][aColumnIndex] != grid[i][bColumnIndex]) {
                if (errorsCount.get() > 0) {
                    errorsCount.decrementAndGet();
                } else {
                    match = false;
                    break;
                }
            }
        }

        return match;
    }

    private static long processGridA(char[][] grid) {
        for (int rowIndex = 0; rowIndex < grid.length - 1; rowIndex++) {
            boolean match = rowsAreEquals(grid, rowIndex, rowIndex + 1, new AtomicInteger());

            if (match) {
                int ii = rowIndex - 1;
                int jj = rowIndex + 2;

                while (ii >= 0 && jj < grid.length && match) {
                    match = rowsAreEquals(grid, ii, jj, new AtomicInteger());

                    ii--;
                    jj++;
                }
            }

            if (match) {
                return (rowIndex + 1) * 100L;
            }
        }

        for (int columnIndex = 0; columnIndex < grid[0].length - 1; columnIndex++) {
            boolean match = columnsAreEquals(grid, columnIndex, columnIndex + 1, new AtomicInteger());

            if (match) {
                int ii = columnIndex - 1;
                int jj = columnIndex + 2;

                while (ii >= 0 && jj < grid[0].length && match) {
                    match = columnsAreEquals(grid, ii, jj, new AtomicInteger());

                    ii--;
                    jj++;
                }
            }

            if (match) {
                return columnIndex + 1;
            }
        }

        return 0L;
    }

    private static long processGridB(char[][] grid) {
        for (int rowIndex = 0; rowIndex < grid.length - 1; rowIndex++) {
            AtomicInteger errorsCount = new AtomicInteger(1);
            boolean match = rowsAreEquals(grid, rowIndex, rowIndex + 1, errorsCount);

            if (match) {
                int ii = rowIndex - 1;
                int jj = rowIndex + 2;

                while (ii >= 0 && jj < grid.length && match) {
                    match = rowsAreEquals(grid, ii, jj, errorsCount);

                    ii--;
                    jj++;
                }
            }

            if (errorsCount.get() > 0) {
                match = false;
            }

            if (match) {
                return (rowIndex + 1) * 100L;
            }
        }

        for (int columnIndex = 0; columnIndex < grid[0].length - 1; columnIndex++) {
            AtomicInteger errorsCount = new AtomicInteger(1);
            boolean match = columnsAreEquals(grid, columnIndex, columnIndex + 1, errorsCount);

            if (match) {
                int ii = columnIndex - 1;
                int jj = columnIndex + 2;

                while (ii >= 0 && jj < grid[0].length && match) {
                    match = columnsAreEquals(grid, ii, jj, errorsCount);

                    ii--;
                    jj++;
                }
            }

            if (errorsCount.get() > 0) {
                match = false;
            }

            if (match) {
                return columnIndex + 1;
            }
        }

        return 0L;
    }

    @Override
    protected String solveA(BufferedReader reader) {
        return parseGrids(reader)
                .stream()
                .reduce(0L, (sum, grid) -> sum + processGridA(grid), Long::sum)
                .toString();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        return parseGrids(reader)
                .stream()
                .reduce(0L, (sum, grid) -> sum + processGridB(grid), Long::sum)
                .toString();
    }
}
