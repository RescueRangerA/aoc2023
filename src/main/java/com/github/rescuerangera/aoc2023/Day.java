package com.github.rescuerangera.aoc2023;

import java.io.BufferedReader;

abstract public class Day {
    private String solutionA;

    private String solutionB;

    public Day solve() {
        Utils.withInputOfDay(reader -> setSolutionA(solveA(reader)), getConcreteDay());
        Utils.withInputOfDay(reader -> setSolutionB(solveB(reader)), getConcreteDay());

        return this;
    }

    public Day beautifulPrint() {
        System.out.printf("Day %d:\n    A: %s\n    B: %s\n", getConcreteDay(), getSolutionA(), getSolutionB());

        return this;
    }

    private int getConcreteDay() {
        String classname = this.getClass().getSimpleName();

        int i = classname.length() - 1;
        while (i >= 0 && Character.isDigit(classname.charAt(i))) {
            i--;
        }

        return Integer.parseInt(classname.substring(i + 1));
    }

    abstract protected String solveA(BufferedReader reader);

    abstract protected String solveB(BufferedReader reader);

    private void setSolutionA(String solutionA) {
        this.solutionA = solutionA;
    }

    private void setSolutionB(String solutionB) {
        this.solutionB = solutionB;
    }

    public String getSolutionA() {
        return solutionA;
    }

    public String getSolutionB() {
        return solutionB;
    }
}
