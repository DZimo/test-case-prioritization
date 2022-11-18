package de.uni_passau.fim.se2.test_prioritization;

import java.util.ArrayList;

public class Fitness {

    public static double getFitness(double m, double n, boolean[][] coverageMatrix, int[] ordering){
       if(TestCaseOrdering.isFitnessMinimizing()){
           return getFitnessMinimising(m,n,coverageMatrix,ordering);
       }
       else {
           return getFitnessMaximising(m,n,coverageMatrix,ordering);
       }
    }

    private static double getFitnessMaximising(double m, double n, boolean[][] coverageMatrix, int[] ordering){
        return 1- ((1/(n*m)) * countTL(coverageMatrix,ordering))  + (1/(2*n));
    }

    private static double getFitnessMinimising(double m, double n, boolean[][] coverageMatrix, int[] ordering) {
        return ((1/(n*m)) * countTL(coverageMatrix,ordering))  + (1/(2*n));
    }


     private static double countTL(boolean[][] coverageMatrix, int[] randomSolution ){

        ArrayList<Integer> statementResolved = new ArrayList<Integer>();
        for (int i=0;i<coverageMatrix[0].length;i++){
            statementResolved.add(0);
        }

        ArrayList<Integer> temporarySolution = new ArrayList<Integer>();
        for (int i=0;i<coverageMatrix.length;i++){
            temporarySolution.add(0);
        }

        int solution=0;
        int count;
        for (int i = 0; i < coverageMatrix.length; i++) {
            count=0;
            for (int j = 0; j < coverageMatrix[0].length; j++) {
                if(coverageMatrix[randomSolution[i]][j]==true && statementResolved.get(j)==0){
                    statementResolved.set(i,1);
                    count++;
                }
            }
            temporarySolution.set(i,count);
        }
        for (int i = 0; i <coverageMatrix.length ; i++) {
            solution = solution + temporarySolution.get(i) * (i + 1);
        }

        return solution;
    }

}
