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

/**
 *
 * @author Carlos García Moreno
 */
public class EvaluationCofing {
    
    private static final String PATH = "src/Resources/config.properties";
        
    //Problem const
    public static final String NUM_OF_OBJECTIVES = "NUM_OF_OBJECTIVES";
    public static final String CHROMOSOME_LENGTH = "CHROMOSOME_LENGTH";
    public static final String MAX_CNT_WRAPPINGS = "MAX_CNT_WRAPPINGS";
    public static final String CODON_UPPER_BOUND = "CODON_UPPER_BOUND";
    public static final String SENSIBLE_INIT_VALUE = "SENSIBLE_INIT_VALUE";
    public static final String SENSIBLE_INIT_PERCENTILE = "SENSIBLE_INIT_PERCENTILE";
    
    //Algorithm const
    public static final String MAX_POPULATION_SIZE = "MAX_POPULATION_SIZE";
    public static final String MAX_GENERATIONS = "MAX_GENERATIONS";
    public static final String PROB_MUTATION = "PROB_MUTATION";
    public static final String PROB_CROSSOVER = "PROB_CROSSOVER";
        
    //Problem vars
    public int numOfObjetives;
    public int chromosomelength;    
    public int maxCntWrappings;
    public int codonUpperBound;
    public boolean siValue;
    public double siPercentile;
    
    //Algorithm vars
    public int maxPopulationSize;
    public int maxGenerations;
    public double probMutation;
    public double probCrossover;

    public EvaluationCofing() throws IOException, EvaluationException {
        Properties conf = loadProperties();
        this.numOfObjetives = Integer.parseInt(conf.getProperty(NUM_OF_OBJECTIVES));
        this.chromosomelength = Integer.parseInt(conf.getProperty(CHROMOSOME_LENGTH));
        this.maxCntWrappings = Integer.parseInt(conf.getProperty(MAX_CNT_WRAPPINGS));
        this.codonUpperBound = Integer.parseInt(conf.getProperty(CODON_UPPER_BOUND));
        this.siValue = Boolean.valueOf(conf.getProperty(SENSIBLE_INIT_VALUE));
        this.siPercentile = Double.parseDouble(conf.getProperty(SENSIBLE_INIT_PERCENTILE));
        this.maxPopulationSize = Integer.parseInt(conf.getProperty(MAX_POPULATION_SIZE));
        this.maxGenerations = Integer.parseInt(conf.getProperty(MAX_GENERATIONS));
        this.probMutation = Double.parseDouble(conf.getProperty(PROB_MUTATION));
        this.probCrossover = Double.parseDouble(conf.getProperty(PROB_CROSSOVER));
    }
    
    private Properties loadProperties() throws FileNotFoundException, IOException{
        Properties config = new Properties();
        config.load(new FileInputStream(PATH));
        return config;
    }
    
}
