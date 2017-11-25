/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Solution;

/**
 *
 * @author cgarc
 */
public class Result {
    private String[][] matrix;
    private String function;
    
    public Result(String[][] matrix){
        this.matrix = matrix;
        this.function = matrix[0][0]; //.replace("#y=", "")
    }
        
    private int getRows(){
        return this.matrix.length;
    }
    
    private int getColumns(){
        return this.matrix[0].length;
    }
    
    private float evaluateLine(){
        return 0;
    }
    
    //override next
    
    public void evaluateMatrix(){
        
    }
    
    public void calculateFitness(){
        
    }
    
}
