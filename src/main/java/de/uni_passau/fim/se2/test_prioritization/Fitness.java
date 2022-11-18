package de.uni_passau.fim.se2.test_prioritization;

import java.util.ArrayList;

public class Fitness {

    public static double getFitness(double m, double n, boolean[][] coverageMatrix, int[] ordering) {
        if (TestCaseOrdering.isFitnessMinimizing()) {
            return getFitnessMinimising(m, n, coverageMatrix, ordering);
        } else {
            return getFitnessMaximising(m, n, coverageMatrix, ordering);
        }
    }

    private static double getFitnessMaximising(double m, double n, boolean[][] coverageMatrix, int[] ordering) {

        ArrayList<Integer> statementResolved = new ArrayList<>();
        for (int i = 0; i < coverageMatrix[0].length; i++) {
            statementResolved.add(0);
        }

        ArrayList<Integer> temporarySolution = new ArrayList<>();
        for (int i = 0; i < coverageMatrix.length; i++) {
            temporarySolution.add(0);
        }
        int solution = 0;
        int count;
        for (int i = 0; i < coverageMatrix.length; i++) {
            count = 0;
            for (int j = 0; j < coverageMatrix[0].length; j++) {
                if (coverageMatrix[ordering[i]][j] == true && statementResolved.get(j) == 0) {
                    statementResolved.set(j, 1);
                    count++;
                }
            }
            temporarySolution.set(i, count);
        }
        for (int i = 0; i < coverageMatrix.length; i++) {
            solution = solution + temporarySolution.get(i) * (i + 1);
        }
        double statementIgnoredCounter = 0d;
        for (int i = 0; i < statementResolved.size(); i++) {
            if (statementResolved.get(i) == 0) {
                statementIgnoredCounter++;
            }
        }
        return 1 - ((1d / (n * (m - statementIgnoredCounter))) * solution) + (1d / (2 * n));
    }

    private static double getFitnessMinimising(double m, double n, boolean[][] coverageMatrix, int[] ordering) {

        ArrayList<Integer> statementResolved = new ArrayList<>();
        for (int i = 0; i < coverageMatrix[0].length; i++) {
            statementResolved.add(0);
        }

        ArrayList<Integer> temporarySolution = new ArrayList<>();
        for (int i = 0; i < coverageMatrix.length; i++) {
            temporarySolution.add(0);
        }
        int solution = 0;
        int count;
        for (int i = 0; i < coverageMatrix.length; i++) {
            count = 0;
            for (int j = 0; j < coverageMatrix[0].length; j++) {
                if (coverageMatrix[ordering[i]][j] == true && statementResolved.get(j) == 0) {
                    statementResolved.set(j, 1);
                    count++;
                }
            }
            temporarySolution.set(i, count);
        }

        for (int i = 0; i < coverageMatrix.length; i++) {
            solution = solution + temporarySolution.get(i) * (i + 1);
        }
        double statementIgnoredCounter = 0d;
        for (int i = 0; i < statementResolved.size(); i++) {
            if (statementResolved.get(i) == 0) {
                statementIgnoredCounter++;
            }
        }

        return ((1d / (n * (m - statementIgnoredCounter))) * solution) + (1d / (2 * n));
    }


}
