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
    //Experiment const
    public static final String ID_EXPERIMENTO = "ID_Experimento";
    public static final String GRAMMAR = "Grammar";
    public static final String TRAINING = "Training";
    public static final String DATABASE = "Database";
    public static final String RUNS = "Runs";
    public static final String FITNESS_METHOD = "Fitness_method";

    //Problem const
    public static final String NUM_OF_OBJECTIVES = "Num_of_objetives";
    public static final String CHROMOSOME_LENGTH = "Chromosome_length";
    public static final String MAX_CNT_WRAPPINGS = "Max_cnt_wrappings";
    public static final String CODON_UPPER_BOUND = "Codon_upper_bound";
    public static final String SENSIBLE_INIT_VALUE = "Sensible_init_value";
    public static final String SENSIBLE_INIT_PERCENTILE = "Sensible_init_percentile";

    //Algorithm const
    public static final String MAX_POPULATION_SIZE = "Max_pupulation_size";
    public static final String MAX_GENERATIONS = "Max_generations";
    public static final String PROB_MUTATION = "Prob_mutation";
    public static final String PROB_CROSSOVER = "Prob_crossover";

    //Experiment vars
    public String idExperimento;
    public String grammar;
    public String training;
    public String database;
    public int runs;
    public String fitnessMethod;

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

    /**
     * Method to load the configuration to the algorithm and problem.
     * @param path route to cnfiguration.properties file
     * @throws IOException
     * @throws EvaluationException 
     */
    public EvaluationCofing(String path) throws IOException, EvaluationException {
        Properties conf = loadProperties(path);

        //experiment configuration
        this.idExperimento = conf.getProperty(ID_EXPERIMENTO);
        this.grammar = conf.getProperty(GRAMMAR);
        this.training = conf.getProperty(TRAINING);
        this.database = conf.getProperty(DATABASE);
        this.runs = Integer.parseInt(conf.getProperty(RUNS));
        this.fitnessMethod = conf.getProperty(FITNESS_METHOD);

        //problem configuration
        this.numOfObjetives = Integer.parseInt(conf.getProperty(NUM_OF_OBJECTIVES));
        this.chromosomelength = Integer.parseInt(conf.getProperty(CHROMOSOME_LENGTH));
        this.maxCntWrappings = Integer.parseInt(conf.getProperty(MAX_CNT_WRAPPINGS));
        this.codonUpperBound = Integer.parseInt(conf.getProperty(CODON_UPPER_BOUND));
        this.siValue = Boolean.valueOf(conf.getProperty(SENSIBLE_INIT_VALUE));
        this.siPercentile = Double.parseDouble(conf.getProperty(SENSIBLE_INIT_PERCENTILE));

        //algorithm configuration
        this.maxPopulationSize = Integer.parseInt(conf.getProperty(MAX_POPULATION_SIZE));
        this.maxGenerations = Integer.parseInt(conf.getProperty(MAX_GENERATIONS));
        this.probMutation = Double.parseDouble(conf.getProperty(PROB_MUTATION));
        this.probCrossover = Double.parseDouble(conf.getProperty(PROB_CROSSOVER));
    }

    /**
     * Method to load file and parse the content to a Properties object.
     * @param path route to cnfiguration.properties file
     * @return return the configuration parsed as Properties object
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private Properties loadProperties(String path) throws FileNotFoundException, IOException {
        Properties config = new Properties();
        config.load(new FileInputStream(path));
        return config;
    }

}
