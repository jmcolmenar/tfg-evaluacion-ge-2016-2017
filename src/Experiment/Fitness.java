/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiment;

import java.util.Arrays;

/**
 *
 * @author Carlos García Moreno
 */
public class Fitness {

    private double[] target;
    private double[] prediction;

    /**
     * The constructor convert the target and the prediction to the same
     * dimesion and length to be compared
     *
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
     *
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
     *
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
     *
     * @return the fitness calculated
     */
    public double rootMeanSquaredError() {
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
     *
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
     *
     * @return the fitness calculated
     */
    public double rSquared() {
        int n = this.target.length;

        double average = 0;
        for (int i = 0; i < this.target.length; i++) {
            average = average + this.target[i];
        }

        average = average / this.target.length;

        double[] firstErrSquared = new double[n];
        for (int i = 0; i < this.target.length; i++) {
            firstErrSquared[i] = Math.pow(this.target[i] - this.prediction[i], 2);
        }

        double ssRes = 0;
        for (int i = 0; i < firstErrSquared.length; i++) {
            ssRes = ssRes + firstErrSquared[i];
        }

        double[] secondErrSquared = new double[n];
        for (int i = 0; i < this.target.length; i++) {
            secondErrSquared[i] = Math.pow(this.target[i] - average, 2);
        }

        double ssTot = 0;
        for (int i = 0; i < secondErrSquared.length; i++) {
            ssTot = ssTot + secondErrSquared[i];
        }

        double rSquared = (ssRes / ssTot);
        return rSquared;
    }

    /**
     * Validate the value that should be set at solution
     *
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
            if (n < 0) {
                aux = Double.POSITIVE_INFINITY;
            } else {
                aux = n;
            }
            return aux;
        }
    }

    /**
     * This a simple fitness to test
     *
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
