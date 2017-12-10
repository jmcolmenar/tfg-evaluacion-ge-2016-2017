/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Solution;

import java.util.Iterator;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author cgarc
 */
public class MatrixFitness {
    
    private class Fitness{
        
        private String[] arrayFitness;
        Evaluator evaluator;
        
        public Fitness(String[] array){
            this.evaluator = new Evaluator();
            this.arrayFitness = array;
        }
        
        public void evaluate(String expression){
            try{
                evaluator.evaluate(expression);
            }
            catch(EvaluationException e){
                System.out.println(e);
            }
        }
    }
    
    private class MatrixFitnessIterator implements Iterator<Fitness>{

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Fitness next() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private String[][] matrix;
    
    public MatrixFitness(String[][] matrix){
        this.matrix = matrix;
    }
    
    public void evaluate(){
        MatrixFitnessIterator it = new MatrixFitnessIterator();
        while (it.hasNext()){
            
        }
    }
}
