/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Carlos García Moreno
 */
public class VariablesReader {

    private File file;
    private final String splitBy = "\\|";

    /**
     * Constructor who load the file from path
     *
     * @param path path of csv to load
     */
    public VariablesReader(String path) {
        loadFile(path);
    }

    /**
     * Create object file
     *
     * @param path cvs to load path
     */
    private void loadFile(String path) {
        this.file = new File(path);
    }

    /**
     * Method to read the variables of grammar
     * @return array of variables
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String[] read() throws FileNotFoundException, IOException {
        String[] array = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<var>")){
                    int index = line.indexOf("=") + 1;
                    String aux = line.substring(index);
                    aux = aux.replaceAll(" ", "");
                    array = aux.split(splitBy);
                }
            }
        }
        return array;
    }
}
