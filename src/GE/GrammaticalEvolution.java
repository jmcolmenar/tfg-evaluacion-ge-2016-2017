/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import BBDD.DAO;
import Import.CSVReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.apache.commons.cli.*;

/**
 *
 * @author Carlos García Moreno
 */
public class GrammaticalEvolution extends AbstractProblemGE {

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
                if (aux.equals("NaN")) {//TODO revisar valores menores que 0
                    funcI = Double.POSITIVE_INFINITY;
                } else {
                    funcI = Double.valueOf(aux);
                }
            } catch (EvaluationException ex) {
                Logger.getLogger(GrammaticalEvolution.class.getName()).log(Level.SEVERE, null, ex);
                funcI = Double.POSITIVE_INFINITY;
            }
            //Add to prediction array the evaluation calculated
            prediction[i] = String.valueOf(funcI);
            solution.getProperties().put(String.valueOf(i), (double)funcI);
        }
        //Calculate fitness
        Fitness fitness = new Fitness(func, prediction);
        double fValue = fitness.r2();
        
        //Control valid value as fitness
        if (Double.isNaN(fValue))
            solution.getObjectives().set(0, Double.POSITIVE_INFINITY);
        else
            solution.getObjectives().set(0, fValue);
    }
    
    //Method to replace the unknowns variables by values
    private String calculateFunctionValued(String originalFunction, int index){
        String newFunction = originalFunction;
        
        Iterator iterator = vars.entrySet().iterator();
        while (iterator.hasNext()){            
            Map.Entry pair = (Map.Entry)iterator.next();
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

    public static void main(String[] args) throws EvaluationException, IOException, Exception {
        //Load properties
        configuration = new EvaluationCofing();
        //Connect to BBDD
        DAO dao = new DAO();
        dao.connect(configuration.database);
        dao.dropTables();
        dao.createTables();
        //Save to BBDD experiment configuration
        dao.saveExperiment(configuration);
        for (int i = 0; i < configuration.runs; i++)
        {
            //First create the problem
            GrammaticalEvolution problem = new GrammaticalEvolution(configuration.grammar);
            //Second create the algorithm
            SimpleGrammaticalEvolution algorithm = new SimpleGrammaticalEvolution(problem,
                    configuration.maxPopulationSize, configuration.maxGenerations, configuration.probMutation, configuration.probCrossover);
            //Load target
            CSVReader csv = new CSVReader(configuration.training);
            func = csv.loadMatrix();
            vars = getVariables(func);
            //Run
            algorithm.initialize();
            Solutions<Variable<Integer>> solutions = algorithm.execute();
            for (Solution<Variable<Integer>> solution : solutions) {
                //Save to BBDD the solution
                dao.saveResult(configuration.idExperimento, (i + 1), solution, problem);
                logger.log(Level.INFO, "Fitness = ({0})", solution.getObjectives().get(0));
                logger.log(Level.INFO, "Phenotype = ({0})", problem.generatePhenotype(solution).toString());
            }
        }
        //Close database connection
        dao.close();
    }
    
    //Method to get the variables
    private static HashMap<String, Integer> getVariables(String[][] phenotype){        
        String[] lineVars = phenotype[0];
        
        HashMap<String, Integer> aux = new HashMap<>();
        for(int i = 1; i < lineVars.length; i++)
            aux.put(lineVars[i], i);
        return aux;
    }        
}
