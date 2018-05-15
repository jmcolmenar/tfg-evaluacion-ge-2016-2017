/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import BBDD.DAO;
import BBDD.JDBCLogHandler;
import Import.CSVReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Carlos García Moreno
 */
public class GrammaticalEvolution extends AbstractProblemGE {
    
    private static final String CONFIGURATION = "configuration";

    private static final Logger logger = Logger.getLogger(GrammaticalEvolution.class.getName());
    protected Evaluator evaluator;
    protected static String[][] func;
    private static EvaluationCofing configuration;
    private static HashMap<String, Integer> vars = new HashMap<String, Integer>();

    public GrammaticalEvolution(String pathToBnf) {
        super(pathToBnf, configuration.numOfObjetives, configuration.chromosomelength, configuration.maxCntWrappings, configuration.codonUpperBound);
        this.setSensibleInitialization(configuration.siValue, configuration.siPercentile);
        this.evaluator = new Evaluator();
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        String originalFunction = phenotype.toString();

        //Create array of predition
        String[] prediction = new String[func.length];
        prediction[0] = originalFunction;

        //Evaluation from phenotype
        for (int i = 1; i < func.length; i++) {
            String currentFunction = calculateFunctionValued(originalFunction, i);
            double funcI;
            try {
                String aux = this.evaluator.evaluate(currentFunction);
                if (aux.equals("NaN")) {
                    funcI = Double.POSITIVE_INFINITY;
                } else {
                    funcI = Double.valueOf(aux);
                }
            } catch (EvaluationException ex) {
                funcI = Double.POSITIVE_INFINITY;
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
     * Method to replace the unknowns variables by values
     * @param originalFunction function of unknowns variables
     * @param index index of func matrix, indicate the index 
     *              of value used at previous evaluation
     * @return function with the knowns values to be evaluated
     */
    private String calculateFunctionValued(String originalFunction, int index) {
        String newFunction = originalFunction;

        Iterator iterator = vars.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String key = pair.getKey().toString().toUpperCase();
            int keyPosition = Integer.parseInt(pair.getValue().toString());
            newFunction = newFunction.replace(key, func[index][keyPosition]);
        }
        return newFunction;
    }

    @Override
    public GrammaticalEvolution clone() {
        GrammaticalEvolution clone = new GrammaticalEvolution(super.pathToBnf);
        return clone;
    }

    /**
     * Main method who control the executino of algorithm
     * @param args arguments to init the algorithm
     * @throws EvaluationException
     * @throws IOException
     * @throws Exception 
     */
    public static void main(String[] args) throws EvaluationException, IOException, Exception {
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

        for (int i = 0; i < configuration.runs; i++) {
            //Set run
            int run = i + 1;
            jdbcHandler.setRun(run);

            logger.info(String.format("Init run: %s", (i + 1)));

            algorithm.initialize();
            Solutions<Variable<Integer>> solutions = algorithm.execute();
            
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save results for ID_Experimento: {0}", configuration.idExperimento);
            for (Solution<Variable<Integer>> solution : solutions) {
                //Save to BBDD the solution
                dao.saveResult(configuration.idExperimento, run, solution, problem);
                logger.info(String.format("Fitness = (%s)", solution.getObjectives().get(0)));
                logger.info(String.format("Phenotype = (%s)", problem.generatePhenotype(solution).toString()));
            }
        }
        //Close database connection
        dao.close();
    }

    /**
     * Method to get the variables used to study
     * @param phenotype matrix with the variables used and the evaluation 
     *                  of the optimum phenotype to study
     * @return 
     */
    private static HashMap<String, Integer> getVariables(String[][] phenotype) {
        String[] lineVars = phenotype[0];

        HashMap<String, Integer> aux = new HashMap<>();
        for (int i = 1; i < lineVars.length; i++) {
            aux.put(lineVars[i], i);
        }
        return aux;
    }

    /**
     * Method to get the logger from class SimpleGeneticAlgorithm
     * @return asociated logger
     */
    private static Logger GetSimpleGeneticAlgorithmLogger() {
        LogManager manager = LogManager.getLogManager();
        return manager.getLogger("jeco.core.algorithm.ga.SimpleGeneticAlgorithm");
    }

    /**
     * Method to get the initials arguments
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
}
