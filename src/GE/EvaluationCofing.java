/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author cgm02
 */
public class EvaluationCofing {
    
    private final String PATH = "src/resources/config.properties";
    
    public int maxPopulationSize;
    public int maxGenerationsdouble;
    public double probMutation;
    public double probCrossover;

    public EvaluationCofing(GrammaticalEvolution problem) throws IOException, EvaluationException {
        Properties conf = loadProperties();
        maxPopulationSize = Integer.parseInt(conf.getProperty("maxPopulationSize"));
        maxGenerationsdouble = Integer.parseInt(conf.getProperty("maxGenerationsdouble"));
        probMutation = Double.parseDouble(conf.getProperty("probMutation"));
        probCrossover = Double.parseDouble(conf.getProperty("probCrossover"));
    }
   /*
    private double calculateProbMutation(String exp, GrammaticalEvolution problem) throws EvaluationException{
        Evaluator evaluator = new Evaluator();
        int numberOfVariables = problem.getNumberOfVariables();
        String evaluate = evaluator.evaluate(exp + "/" + numberOfVariables);
        return Double.parseDouble(evaluate);
    }
     */       
    private Properties loadProperties() throws FileNotFoundException, IOException{
        Properties config = new Properties();
        config.load(new FileInputStream(PATH));
        return config;
    }
    
}
