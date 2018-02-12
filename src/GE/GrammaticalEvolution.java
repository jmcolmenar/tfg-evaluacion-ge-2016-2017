/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GE;

import Importador.CSVReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Carlos García Moreno
 */
public class GrammaticalEvolution extends AbstractProblemGE {
    private static final Logger logger = Logger.getLogger(GrammaticalEvolution.class.getName());  
    protected Evaluator evaluator;
    protected static String[][] func;

    public GrammaticalEvolution(String pathToBnf) {
        super(pathToBnf);
        this.evaluator = new Evaluator();
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        String originalFunction = phenotype.toString();
        //TODO error
        for (int i = 0; i < func.length; i++){
            String currentFunction = originalFunction.replace("X", String.valueOf(i));
            double funcI;//TODO use to solution
            try{
                String aux = this.evaluator.evaluate(currentFunction);
                if (aux.equals("NaN")) {
                    funcI = Double.POSITIVE_INFINITY;
                } else {
                    funcI = Double.valueOf(aux);
                }
            } catch (EvaluationException ex) {
                Logger.getLogger(GrammaticalEvolution.class.getName()).log(Level.SEVERE, null, ex);
                funcI = Double.POSITIVE_INFINITY;
            }
            //TODO calcualte fitness
        }
        //add solution errors...
    }

    @Override
    public Problem<Variable<Integer>> clone() {
        GrammaticalEvolution clone = new GrammaticalEvolution(super.pathToBnf);
        return clone;
    }
    
    public static void main(String[] args) throws EvaluationException, IOException{
        //First create the problem
        GrammaticalEvolution problem = new GrammaticalEvolution("test/grammar_example.bnf");
        //Second create the algorithm
        
        //load properties
        EvaluationCofing config = new EvaluationCofing(problem);
        
        //load algorithm
        SimpleGrammaticalEvolution algorithm = new SimpleGrammaticalEvolution(problem, config.maxPopulationSize, config.maxGenerationsdouble, config.probMutation, config.probCrossover);
        
        //load optimumPhenotype
        CSVReader csv = new CSVReader("Tablas-entrada/y=x+7.csv");
        func = csv.loadMatrix();
        
        //Run
        algorithm.initialize();
        /*Solutions<Variable<Integer>> solutions = algorithm.execute();
        for (Solution<Variable<Integer>> solution : solutions) {
            logger.log(Level.INFO, "Fitness = ({0})", solution.getObjectives().get(0));
            logger.log(Level.INFO, "Phenotype = ({0})", problem.generatePhenotype(solution).toString());
        }*/
    }
}
