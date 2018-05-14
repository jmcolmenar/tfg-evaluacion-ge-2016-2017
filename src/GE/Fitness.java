/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import org.apache.commons.math3.stat.StatUtils;

import java.util.Arrays;

/**
 *
 * @author Carlos García Moreno
 */
public class Fitness {

    private double[] target;
    private double[] prediction;

    /**
     * The constructor convert the target and the prediction to the 
     * same dimesion and length to be compared
     * @param target target preload
     * @param prediction prediction calculated
     */
    public Fitness(String[][] target, String[] prediction) {
        String[] oneDTarget = convert2dTo1d(target);
        String[] auxTarget = Arrays.copyOfRange(oneDTarget, 1, oneDTarget.length);
        String[] auxPrediction = Arrays.copyOfRange(prediction, 1, prediction.length);

        this.target = Arrays.stream(auxTarget)
                .mapToDouble(Double::parseDouble)
                .toArray();
        this.prediction = Arrays.stream(auxPrediction)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    /**
     * Method to get the first colum from a matrix
     * @param matrix a matrix
     * @return return the array of the first column matrix
     */
    private String[] convert2dTo1d(String[][] matrix) {
        String[] oneDimensional = new String[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            oneDimensional[i] = matrix[i][0];
        }
        return oneDimensional;
    }

    /**
     * Calculate the absolute acumulated error
     * @return fitness calculated
     */
    public double ABSAcumulated() {
        if (target.length != prediction.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }

        double totError = 0.0;
        for (int i = 0; i < this.target.length; i++) {
            totError += Math.abs(this.target[i] - this.prediction[i]);
        }

        return totError;
    }

    /**
     * Calculate the mean squared error
     * @return the fitness calculated
     */
    public double meanSquaredError() {
        if (target.length != prediction.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }

        int n = target.length;
        double rss = 0.0;
        for (int i = 0; i < n; i++) {
            rss += Math.sqrt(Math.abs(target[i] - prediction[i]));
        }

        return rss / n;
    }

    /**
     * Calculate the Average error
     * @return the fitness calculated
     */
    public double averageError() {
        if (target.length != prediction.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }

        int n = target.length;
        double totError = 0.0;
        for (int i = 0; i < n; i++) {
            totError += Math.abs(this.target[i] - this.prediction[i]);
        }

        return totError / n;
    }
    
    /**
     * Calculate the r² error
     * @return the fitness calculated
     */
    public double rSquared() {
        if (target.length != prediction.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }

        int n = target.length;

        // first pass
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < n; i++) {
            sumx += prediction[i];
            sumx2 += prediction[i] * prediction[i];
            sumy += target[i];
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (prediction[i] - xbar) * (prediction[i] - xbar);
            yybar += (target[i] - ybar) * (target[i] - ybar);
            xybar += (prediction[i] - xbar) * (target[i] - ybar);
        }
        double slope = xybar / xxbar;
        double intercept = ybar - slope * xbar;

        // more statistical analysis
        double ssr = 0.0; // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = slope * prediction[i] + intercept;
            ssr += (fit - ybar) * (fit - ybar);
        }

        return ssr / yybar;
    }


    /**
     * Method to compute r^2.
     *
     * @param expected
     * @param observed
     * @return
     */
    public static double computeRSquare(double[] expected, double[] observed) {

        // Implementing this method: https://support.office.com/es-es/article/COEFICIENTE-R2-funci%C3%B3n-COEFICIENTE-R2-d7161715-250d-4a01-b80d-a8364f2be08f
        double avgE = StatUtils.mean(expected);
        double avgO = StatUtils.mean(observed);

        double acuNum = 0.0;
        double acuE = 0.0;
        double acuO = 0.0;

        for (int i=0; i<expected.length; i++) {
            acuNum += ((expected[i]-avgE) * (observed[i]-avgO));
            acuE += Math.pow((expected[i]-avgE), 2);
            acuO += Math.pow((observed[i]-avgO), 2);
        }

        double r = acuNum / Math.sqrt(acuE * acuO);

        return Math.pow(r, 2);
    }

    
    /**
     * Validate the value that should be set at solution
     * @param method method used to calculate the fitness
     * @param n fitnes calculated previously
     * @return the value to be set as objective at solution
     */
    public double validate(String method, double n) {
        //Control valid value as fitness
        if (Double.isNaN(n)) {
            return Double.POSITIVE_INFINITY;
        } else {
            double aux;
            switch (method) {
                // R2 best is 1, but we are minimizing
                case "rSquared":
                    aux = (1.0 - n);
                    break;
                default:
                    aux = n;
                    break;
            }
            return aux;
        }
    }

    /**
     * This a simple fitness to test
     * @return the fitness calculated
     */
    public double test() {
        double totError = 0.0;
        for (int i = 0; i < this.target.length; i++) {
            totError += Math.pow(this.target[i] - this.prediction[i], 2);
        }
        return totError;
    }
}
