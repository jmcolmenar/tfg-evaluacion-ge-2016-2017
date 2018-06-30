/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Carlos García Moreno
 */
public class CSVReader {

    private String csvFile;
    private File file;
    private String csvSplitBy = ";";

    /**
     * Constructor who load the file from path @csvfile
     * @param csvFile path of csv to load
     */
    public CSVReader(String csvFile) {
        this.csvFile = csvFile;
        loadFile(this.csvFile);
    }

    /**
     * Create object file
     * @param path cvs to load path
     */
    private void loadFile(String path) {
        file = new File(path);
    }

    /**
     * Get number of Rows from a CSV file
     * @return number of rows
     * @throws FileNotFoundException 
     */
    private int numberRows() throws FileNotFoundException {//modificar private
        int nRows = 0;
        Scanner inputStream;
        try {
            inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                inputStream.next();
                nRows++;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
        }
        return nRows;
    }

    /**
     * Get number of colummns from a CVS file
     * @return return number of columns
     * @throws FileNotFoundException 
     */
    private int numberColumns() throws FileNotFoundException {
        int nColumns = 0;
        Scanner inputStream;
        String firstLine;
        String[] values;
        try {
            inputStream = new Scanner(file);
            firstLine = inputStream.next();
            values = firstLine.split(csvSplitBy);
            nColumns = values.length;
        } catch (FileNotFoundException e) {
        }
        return nColumns;
    }

    /**
     * Read CVS and create a matrix with the complete information
     * @return return a matrix String[][] created by the preload csv file
     * @throws FileNotFoundException 
     */
    public String[][] loadMatrix() throws FileNotFoundException {
        int rows = this.numberRows();
        int columns = this.numberColumns();
        String[][] matrix = new String[rows][columns];

        Scanner inputStream;
        String line;
        String[] values;
        int j = 0;
        try {
            inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                line = inputStream.next();
                values = line.split(csvSplitBy);
                for (int i = 0; i < values.length; i++) {
                    matrix[j][i] = values[i];
                }
                j++;
            }
        } catch (FileNotFoundException e) {
        }

        return matrix;
    }
}
