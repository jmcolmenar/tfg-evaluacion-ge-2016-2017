/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiment;

import Database.DAO;
import Database.JDBCLogHandler;
import FileRepository.CSVReader;
import FileRepository.VariablesReader;
import Experiment.Observers.GenerationObserver;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;

/**
 *
 * @author Carlos García Moreno
 */
public class GrammaticalEvolution extends AbstractProblemGE {

    private static final String CONFIGURATION = "configuration";

    private static final Logger logger = Logger.getLogger(GrammaticalEvolution.class.getName());
    protected static String[][] func;
    private static EvaluationCofing configuration;
    private static HashMap<String, Integer> vars = new HashMap<String, Integer>();
    private Scope scope;

    public GrammaticalEvolution(String pathToBnf) throws IOException {
        super(pathToBnf, configuration.numOfObjetives, configuration.chromosomelength, configuration.maxCntWrappings, configuration.codonUpperBound);
        this.setSensibleInitialization(configuration.siValue, configuration.siPercentile);
        setupScope(configuration.grammar);
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        String originalFunction = phenotype.toString();

        //Create array of predition
        String[] prediction = new String[func.length];
        prediction[0] = originalFunction;

        //Evaluation from phenotype
        for (int i = 1; i < func.length; i++) {
            setEvaluationVars(i);
            double funcI;
                try {
                    Expression expr = Parser.parse(originalFunction, this.scope);
                    funcI = expr.evaluate();
                    if (Double.isNaN(funcI)) {
                        funcI = Double.POSITIVE_INFINITY;
                    }
                } catch (parsii.tokenizer.ParseException ex) {
                    //Invalid phenotype
                    funcI = Double.POSITIVE_INFINITY;
                    for (int z = i; z<func.length; z++){
                        prediction[z] = String.valueOf(funcI);
                        solution.getProperties().put(String.valueOf(z), (double) funcI);
                    }
                    break;
                }

            //Add to prediction array the evaluation calculated
            prediction[i] = String.valueOf(funcI);
            solution.getProperties().put(String.valueOf(i), (double) funcI);
        }
        //Calculate fitness
        Fitness fitness = new Fitness(func, prediction);
        double fValue = Double.NaN;

        try {
            Method method = fitness.getClass().getMethod(configuration.fitnessMethod);
            fValue = (double) method.invoke(fitness);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GrammaticalEvolution.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Validate value to set objective
        double objValue = fitness.validate(configuration.fitnessMethod, fValue);
        //Set objectives
        solution.getObjectives().set(0, objValue);
    }

    /**
     * Method to set the variables for the next evaluation
     *
     * @param index idex of func matrix
     */
    private void setEvaluationVars(int index) {

        Iterator iterator = vars.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String key = pair.getKey().toString();
            int keyPosition = Integer.parseInt(pair.getValue().toString());
            double value = Double.parseDouble(func[index][keyPosition]);
            this.scope.getVariable(key).withValue(value);
        }
    }

    @Override
    public GrammaticalEvolution clone() {
        GrammaticalEvolution clone = null;
        try {
            clone = new GrammaticalEvolution(super.pathToBnf);
        } catch (IOException ex) {
            Logger.getLogger(GrammaticalEvolution.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    /**
     * Main method who control the executino of algorithm
     *
     * @param args arguments to init the algorithm
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {
        //Load properties configuration
        CommandLine cmd = startUp(args);
        configuration = new EvaluationCofing(cmd.getOptionValue(CONFIGURATION));

        //Connect to BBDD
        DAO dao = new DAO();
        Connection connect = dao.connect(configuration.database);
        if (dao.existsID_Experimento(configuration.idExperimento)) {
            throw new Exception("Ya existe el ID_Experimento: " + configuration.idExperimento + " en la base de datos");
        }
        //dao.dropTables();
        dao.createTables();

        //Save to BBDD experiment configuration
        dao.saveExperiment(configuration);

        //First create the problem
        GrammaticalEvolution problem
                = new GrammaticalEvolution(configuration.grammar);

        //Second create the algorithm
        SimpleGrammaticalEvolution algorithm
                = new SimpleGrammaticalEvolution(problem,
                        configuration.maxPopulationSize,
                        configuration.maxGenerations,
                        configuration.probMutation,
                        configuration.probCrossover);

        //Load target
        CSVReader csv = new CSVReader(configuration.training);
        func = csv.loadMatrix();
        vars = getVariables(func);

        // set up the JDBCLogger handler
        JDBCLogHandler jdbcHandler
                = new JDBCLogHandler(
                        configuration.idExperimento,
                        "org.sqlite.JDBC",
                        "jdbc:sqlite:" + configuration.database,
                        connect);

        //Add JDBC to save logger at database
        logger.addHandler(jdbcHandler);

        //Add JDBC to save logger at database
        Logger sgaLogger = GetSimpleGeneticAlgorithmLogger();
        sgaLogger.addHandler(jdbcHandler);

        //Setup observers
        GenerationObserver go = setGenerationObserver(algorithm);

        for (int i = 0; i < configuration.runs; i++) {
            //Set run
            int run = i + 1;
            jdbcHandler.setRun(run);

            logger.info(String.format("Init run: %s", run));

            go.resetObserver();

            algorithm.initialize();
            Solutions<Variable<Integer>> solutions = algorithm.execute();

            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save results for ID_Experimento: {0}", configuration.idExperimento);
            for (Solution<Variable<Integer>> solution : solutions) {
                //Save to BBDD the solution
                dao.saveResult(configuration.idExperimento, run, solution, problem);
                logger.info(String.format("Fitness = (%s)", solution.getObjectives().get(0)));
                logger.info(String.format("Phenotype = (%s)", problem.generatePhenotype(solution).toString()));
            }
            dao.saveBestGenerationObjective(configuration.idExperimento, run, go.getBestObjectives());
            logger.info(String.format("Finished run: %s", run));
        }
        //Close database connection
        dao.close();
    }

    /**
     * Method to get the variables used to study
     *
     * @param phenotype matrix with the variables used and the evaluation of the
     * optimum phenotype to study
     * @return
     */
    private static HashMap<String, Integer> getVariables(String[][] phenotype) {
        String[] lineVars = phenotype[0];

        HashMap<String, Integer> aux = new HashMap<>();
        for (int i = 1; i < lineVars.length; i++) {
            aux.put(lineVars[i].toUpperCase(), i);
        }
        return aux;
    }

    /**
     * Method to get the logger from class SimpleGeneticAlgorithm
     *
     * @return asociated logger
     */
    private static Logger GetSimpleGeneticAlgorithmLogger() {
        LogManager manager = LogManager.getLogManager();
        return manager.getLogger("jeco.core.algorithm.ga.SimpleGeneticAlgorithm");
    }

    /**
     * Method to get the initials arguments
     *
     * @param args arguments to read
     * @return object CommandLine with the arguments readed
     * @throws Exception
     */
    private static CommandLine startUp(String[] args) throws Exception {
        Options options = new Options();

        Option grammar = new Option("c", CONFIGURATION, true, "configuration properties file path");
        grammar.setRequired(true);
        options.addOption(grammar);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utilityname", options);

            System.exit(1);
            return null;
        }

        String configurationFilePath = cmd.getOptionValue(CONFIGURATION);

        Logger.getLogger(GrammaticalEvolution.class.getName())
                .log(Level.INFO, "Configuration used: {0}", configurationFilePath);

        return cmd;
    }

    /**
     * Method to setup the generation observer
     *
     * @param algorithm SimpleGrammaticalEvolution object
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static GenerationObserver setGenerationObserver(SimpleGrammaticalEvolution algorithm) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        //generation observer
        GenerationObserver go = new GenerationObserver(configuration.maxGenerations);
        Field f = algorithm.getClass().getDeclaredField("algorithm"); //NoSuchFieldException
        f.setAccessible(true);
        SimpleGeneticAlgorithm<Variable<Integer>> ga = (SimpleGeneticAlgorithm<Variable<Integer>>) f.get(algorithm);
        ga.addObserver(go);
        return go;
    }

    /**
     * Method to read the grammar variables and setup an initial start to evaluation
     * @param path grammar file path
     * @throws IOException 
     */
    private void setupScope(String path) throws IOException {
        VariablesReader vReader = new VariablesReader(path);        
        String[] arrayVariables = vReader.read();
        
        this.scope = new Scope();
        
        for (String arrayVariable : arrayVariables) {
            this.scope.getVariable(arrayVariable).withValue(Double.POSITIVE_INFINITY);
        }
    }
}
