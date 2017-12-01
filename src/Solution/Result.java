/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Solution;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 *
 * @author cgarc
 */
public class Result implements Iterator<String>{
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

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String next() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private class MatrixIterator<String> implements Iterator<String>{
        
        private Queue<String[]> rowsQueue = new ArrayDeque<>();
        
        private MatrixIterator(String[] matrixResult){
            rowsQueue.add(matrixResult);
        }
        
        @Override
        public boolean hasNext() {
            return (!rowsQueue.isEmpty());
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String next() {
            String[] aux = rowsQueue.remove();
            
            //return rowsQueue.add(aux.getNext());
            return (String) "";
        }
        
    }
    
}
