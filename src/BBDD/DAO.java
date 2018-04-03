/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BBDD;

import GE.EvaluationCofing;
import GE.GrammaticalEvolution;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Carlos García Moreno
 */
public class DAO {

    String url = "db/ge.db";

    Connection connect;

    public void connect() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (connect != null) {
                Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Connected to: {0}", url);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, "Unable to connect to the database {0}", ex.getMessage());
        }
    }

    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveExperiment(String idExp, EvaluationCofing configuration, CommandLine cmd) {
        try {            
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save experiment to IdEXP: {0}", idExp);
            
            PreparedStatement st
                    = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.NUM_OF_OBJECTIVES);
            st.setString(3, String.valueOf(configuration.numOfObjetives));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.CHROMOSOME_LENGTH);
            st.setString(3, String.valueOf(configuration.chromosomelength));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.MAX_CNT_WRAPPINGS);
            st.setString(3, String.valueOf(configuration.maxCntWrappings));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.CODON_UPPER_BOUND);
            st.setString(3, String.valueOf(configuration.codonUpperBound));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.SENSIBLE_INIT_VALUE);
            st.setString(3, String.valueOf(configuration.siValue));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.SENSIBLE_INIT_PERCENTILE);
            st.setString(3, String.valueOf(configuration.siPercentile));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.MAX_POPULATION_SIZE);
            st.setString(3, String.valueOf(configuration.maxPopulationSize));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.MAX_GENERATIONS);
            st.setString(3, String.valueOf(configuration.maxGenerations));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.PROB_MUTATION);
            st.setString(3, String.valueOf(configuration.probMutation));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, EvaluationCofing.PROB_CROSSOVER);
            st.setString(3, String.valueOf(configuration.probCrossover));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, "Grammar");
            String[] grammarList = cmd.getOptionValue("grammar").split("/");
            st.setString(3, grammarList[grammarList.length - 1]);
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (IdExp, "
                            + "Propiedad, Valor)"
                            + " values (?,?,?)");

            st.setString(1, idExp);
            st.setString(2, "Training");
            String[] trainingList = cmd.getOptionValue("training").split("/");
            st.setString(3, trainingList[trainingList.length - 1]);
            st.execute();            
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void saveResult(String idExp, Solution<Variable<Integer>> solution, GrammaticalEvolution problem) {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save result to IdEXP: {0}", idExp);
            
            PreparedStatement st
                    = connect.prepareStatement("insert into Resultados (IdExp, "
                            + "Genotipo, Fenotipo, Evaluacion, Fitness, GenesUsados)"
                            + " values (?,?,?,?,?,?)");

            st.setString(1, idExp);
            st.setString(2, genotypeToString(solution.getVariables()));
            st.setString(3, problem.generatePhenotype(solution).toString());
            st.setString(4, evaluationToString(solution.getProperties()));
            st.setString(5, String.valueOf(solution.getObjectives().get(0))); //TODO: deberia ser double?¿?¿?¿?
            st.setString(6, String.valueOf(problem.generatePhenotype(solution).getUsedGenes()));
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
    
    public String getMaxExperiment(){
        Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Getting max idExp");
        
        ResultSet result = null;
        String stResult = "";
        try {
            PreparedStatement st = connect.prepareStatement("select max(IdExp) as IdExp from Experimentos");
            result = st.executeQuery();
            stResult = result.getString("IdExp");
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return stResult;
    }

    private String genotypeToString(ArrayList<Variable<Integer>> variables) {
        String result = "";
        for (Variable<Integer> variable : variables) {
            result += variable.getValue() + ";";
        }
        return result.substring(0, result.length() - 1);
    }
    
    private String evaluationToString(HashMap<String, Number> h){
        
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        };

        SortedSet<String> keys = new TreeSet<>(comparator);
        keys.addAll(h.keySet());
        
        String result = "";
        for (Iterator<String> i = keys.iterator(); i.hasNext();) {
            String item = i.next();
             result += String.valueOf(h.get(item)) + ";";
        }
        
        return result.substring(0, result.length() - 1);
    }
}
