/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Observers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Carlos García Moreno
 */
public class GenerationObserver implements Observer{

    private static final String CURRENTGENERATION = "CurrentGeneration";
    private static final String BESTOBJECTIVE = "BestObjective";
    
    private Double[] bestOjectives;
    private final int lentgh;
    
    public GenerationObserver(int length){
        this.lentgh = length;
    }
    
    @Override
    public void update(Observable obs, Object obj) {
        HashMap<String, String> auxMap = (HashMap<String, String>) obj;
        this.bestOjectives[Integer.parseInt(auxMap.get(CURRENTGENERATION)) - 1] 
                = Double.parseDouble(auxMap.get(BESTOBJECTIVE));
    }
    
    /**
     * Method to get the best objectives saved
     * @return return the best objectives in a matrix
     */
    public Double[] getBestObjectives(){
        Double[] aux = Arrays.stream(this.bestOjectives)
                     .filter(s -> (s != null))
                     .toArray(Double[]::new);
        return aux;
    }
    
    /**
     * Method to reset the Observer to start a new run
     */
    public void resetObserver(){
        this.bestOjectives = new Double[this.lentgh];
    }
}
