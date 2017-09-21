/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Importador.CSVReader;
import java.io.FileNotFoundException;

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
        String[][] matrix;
        matrix = csv.loadMatrix();
        
    }
    
}
