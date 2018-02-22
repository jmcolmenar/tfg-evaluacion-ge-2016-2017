/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import java.util.Arrays;

/**
 *
 * @author Carlos García Moreno
 */
public class Fitness {
    
    private double[] target;
    private double[] prediction;
    
    //The constructor, conver the target and the prediction to the same dimesion and length to be compared
    public Fitness(String[][] target, String[] prediction){
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
    
    //Method to get the first colum from an array
    private String[] convert2dTo1d(String[][] array){
        String[] oneDimensional = new String[array.length];
        for(int i=0; i < array.length; i++)
            oneDimensional[i] = array[i][0];
        return oneDimensional;
    }
    
    //This is a test, TODO: 
    public double test(){
        double totError = 0;
        for(int i = 0; i < this.target.length; i++){
            totError =+ Math.pow(this.target[i] - this.prediction[i], 2);
        }
        return totError;
    }
}
