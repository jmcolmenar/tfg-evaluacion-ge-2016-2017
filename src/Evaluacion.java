/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Importador.CSVReader;
import Solution.Result;
import java.io.FileNotFoundException;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;


/**
 *
 * @author cgarc
 */
public class Evaluacion {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException{
        // TODO code application logic here
        
        //PRUEBAS
        CSVReader csv = new CSVReader("Tablas-entrada/y=x+7.csv");
        String[][] matrix = csv.loadMatrix();
        
        String function = matrix[0][0];
        String value = matrix[1][1];
        String auxFunction = function.replace("#y=", "");
        auxFunction = auxFunction.replace("x", value);
        System.out.println(function);
        System.out.println(auxFunction);
        
        //System.out.println(matrix.length);
        //System.out.println(matrix[0].length);
        Evaluator evaluator = new Evaluator();
        try{
            String evaluate = evaluator.evaluate(auxFunction);
            System.out.println(evaluate);
        }catch(EvaluationException e){
            System.out.println(e);
        }
        
        System.out.println(matrix.length);
        
        //Result r = new Result(matrix);
    }
 
    
    //metodo for anidados
    
    //estructura para comprar diferencias/ varias diferencias con el optimo
    
    //metodo exportar resultados
}
