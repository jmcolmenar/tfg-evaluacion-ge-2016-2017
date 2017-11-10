/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Importador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author cgarc
 */
public class CSVReader {
    
    private String csvFile;
    private File file;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
    private String csvSplitBy = ";";
    
    public CSVReader(String csvFile){
        this.csvFile = csvFile;
        loadFile(this.csvFile);
    }
    
    private void loadFile(String path){
        file = new File(path);
    }
    
    private int numberRows() throws FileNotFoundException{//modificar private
        int nRows = 0;
        Scanner inputStream;
        try{
            inputStream = new Scanner(file);
            while(inputStream.hasNext()){
                inputStream.next();
                nRows++;
            }
            inputStream.close();
        }catch(FileNotFoundException e){}
        return nRows;
    }
    
    
    private int numberColumns() throws FileNotFoundException{//modificar private
        int nColumns = 0;
        Scanner inputStream;
        String firstLine;
        String[] values;
        try{
            inputStream = new Scanner(file);
            firstLine = inputStream.next();
            values = firstLine.split(csvSplitBy);
            nColumns = values.length;
        }catch(FileNotFoundException e){}
        return nColumns;
    }
    
    private String[][] loadMatrix() throws FileNotFoundException{
        int rows = this.numberRows();
        int columns = this.numberColumns();
        String[][] matrix = new String[rows][columns];
        
        Scanner inputStream;
        String line;
        String[] values;
        int j=0;
        try{
            inputStream = new Scanner(file);
            while(inputStream.hasNext()){
                line = inputStream.next();
                values = line.split(csvSplitBy);
                for (int i=0;i<values.length;i++){
                    matrix[j][i] = values[i];
                    System.out.println(values[i]);
                }                
                j++;
            }
        }catch(FileNotFoundException e){}  
        
        return matrix;
    }
    
    private List<List<String>> load(){
        File file = new File(csvFile);
        
        
        List<List<String>> lines = new ArrayList<>();
        Scanner inputStream;
        String line;
        String[] values;
        
        
        try{
            inputStream = new Scanner(file);
            
            while(inputStream.hasNext()){
                line = inputStream.next();
                values = line.split(csvSplitBy);
                lines.add(Arrays.asList(values));
            }
            inputStream.close();
                        
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }               
        return lines;
    }
    
}
    
    
    
    
