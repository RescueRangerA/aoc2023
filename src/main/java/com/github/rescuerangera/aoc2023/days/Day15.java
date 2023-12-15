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

public class Day15 extends Day {
    private record Lens(String label, int focalLength) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Lens lens)) return false;
            return Objects.equals(label, lens.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }
    }


    private static int hash(String message) {
        return message
                .chars()
                .reduce(
                        0,
                        (sum, v) -> ((sum + v) * 17) % 256
                );
    }

    protected String solveA(BufferedReader reader) {
        String line = "";

        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays
                .stream(line.split(","))
                .reduce(
                        0L,
                        (sum, message) -> sum + hash(message),
                        Long::sum
                )
                .toString();
    }

    private long calculateFocusingPower(int boxNumber, int slotNumber, Lens lens) {
        return (long) boxNumber * slotNumber * lens.focalLength();
    }

    @Override
    protected String solveB(BufferedReader reader) {
        String line = "";

        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int BOX_COUNT = 256;
        Map<Integer, List<Lens>> boxes = new HashMap<>();
        for (int i = 0; i < BOX_COUNT ; i++) {
            boxes.put(i, new LinkedList<>());
        }

        for (String lof: line.split(",")) {
            String label;

            if ( lof.endsWith("-") ) {
                label = lof.substring(0, lof.length() - 1);

                List<Lens> lenses = boxes.get(hash(label));

                lenses.remove(new Lens(label, 0));
            } else {
                label = lof.substring(0, lof.length() - 2);
                int focalLength = Integer.parseInt(lof.substring(lof.length() - 1));

                List<Lens> lenses = boxes.get(hash(label));
                Lens newLens = new Lens(label, focalLength);

                int pos = lenses.indexOf(newLens);

                if ( pos == -1 ) {
                    lenses.add(newLens);
                } else {
                    lenses.set(pos, newLens);
                }
            }
        }

        long focusingPower = 0L;
        for (int boxIndex = 0; boxIndex < BOX_COUNT ; boxIndex++) {
            List<Lens> lenses = boxes.get(boxIndex);

            int slot = 0;
            for (Lens lens: lenses) {
                focusingPower += calculateFocusingPower(boxIndex + 1, slot + 1, lens);
                slot++;
            }
        }

        return String.valueOf(focusingPower);
    }
}
