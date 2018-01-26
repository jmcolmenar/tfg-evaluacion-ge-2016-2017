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
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import net.sourceforge.jeval.EvaluationException;

/**
 *
 * @author cgm02
 */
public class GrammaticalEvolution extends AbstractProblemGE {
    private static final Logger logger = Logger.getLogger(GrammaticalEvolution.class.getName());  
    //protected ScriptEngine evaluator = null;
    protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x //!!!MODIFICAR por mi funcion

    public GrammaticalEvolution(String pathToBnf) {
        super(pathToBnf);
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Problem<Variable<Integer>> clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) throws EvaluationException, IOException{
        //First create the problem
        GrammaticalEvolution problem = new GrammaticalEvolution("test/grammar_example.bnf");
        //Second create the algorithm
        //!!!MODIFICAR crear properties para pasarlas a la creacion del algoritmo
        EvaluationCofing config = new EvaluationCofing(problem);
        SimpleGrammaticalEvolution algorithm = new SimpleGrammaticalEvolution(problem, 100, 1000, (1.0 / problem.getNumberOfVariables()), SinglePointCrossover.DEFAULT_PROBABILITY);
        SimpleGrammaticalEvolution algorithm2 = new SimpleGrammaticalEvolution(problem, config.maxPopulationSize, config.maxGenerationsdouble, config.probMutation, config.probCrossover);
        //Run
        /*algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        for (Solution<Variable<Integer>> solution : solutions) {
            logger.log(Level.INFO, "Fitness = ({0})", solution.getObjectives().get(0));
            logger.log(Level.INFO, "Phenotype = ({0})", problem.generatePhenotype(solution).toString());
        }*/
    }
    /*
    private static Properties loadProperties() throws FileNotFoundException, IOException{
        Properties config = new Properties();
        String path = "src/resources/config.properties";
        config.load(new FileInputStream(path));
        return config;
    }*/
}
