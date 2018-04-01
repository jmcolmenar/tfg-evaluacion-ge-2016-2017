/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import BBDD.DAO;
import Import.CSVReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
                if (aux.equals("NaN")) {//revisar
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
            solution.getProperties().put("E" + i, (double)funcI);
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
        //Initial configuration
        CommandLine cmd = startUp(args);
        //Load properties
        configuration = new EvaluationCofing();
        //First create the problem
        GrammaticalEvolution problem = new GrammaticalEvolution(cmd.getOptionValue("grammar"));
        //Second create the algorithm
        SimpleGrammaticalEvolution algorithm = new SimpleGrammaticalEvolution(problem, configuration.maxPopulationSize, configuration.maxGenerations, configuration.probMutation, configuration.probCrossover);
        //Load target
        CSVReader csv = new CSVReader(cmd.getOptionValue("target"));
        func = csv.loadMatrix();
        vars = getVariables(func);
        //Run
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        for (Solution<Variable<Integer>> solution : solutions) {
            //int prueba1 = solution.getVariable(0).getValue();//genotipo
            //int prueba2 = problem.generatePhenotype(solution).getUsedGenes();//genes usados
            logger.log(Level.INFO, "Fitness = ({0})", solution.getObjectives().get(0));
            logger.log(Level.INFO, "Phenotype = ({0})", problem.generatePhenotype(solution).toString());
        }
    }

    //Method to check the initial arguments
    private static CommandLine startUp(String[] args) throws Exception {
        Options options = new Options();

        Option grammar = new Option("g", "grammar", true, "grammar file path");
        grammar.setRequired(true);
        options.addOption(grammar);

        Option target = new Option("t", "target", true, "target file path");
        target.setRequired(true);
        options.addOption(target);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }

        String grammarFilePath = cmd.getOptionValue("grammar");
        String targetFilePath = cmd.getOptionValue("target");

        System.out.println("Grammar used: " + grammarFilePath);
        System.out.println("Target used: " + targetFilePath);

        return cmd;
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
