/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Experiment.EvaluationCofing;
import Experiment.GrammaticalEvolution;
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

/**
 *
 * @author Carlos García Moreno
 */
public class DAO {

    Connection connect;

    /**
      * Method to entablish a connection database.
      * @param path database route
      * @return the connection to database
      */
    public Connection connect(String path) {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + path);
            if (connect != null) {
                Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Connected to: {0}", path);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, "Unable to connect to the database {0}", ex.getMessage());
        }
        return connect;
    }

    /**
     * Method to close a connection database.
     */
    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to drop the created tables.
     */
    public void dropTables() {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Drop tables");
            PreparedStatement st = connect.prepareStatement("DROP TABLE if exists Experimentos;");
            st.execute();

            st = connect.prepareStatement("DROP TABLE  if exists Resultados;");
            st.execute();

            st = connect.prepareStatement("DROP TABLE  if exists Logs;");
            st.execute();
            
            st = connect.prepareStatement("DROP TABLE  if exists Generaciones;");
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to create tables if not exists.
     */
    public void createTables() {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Create init tables (if not exists)");

            PreparedStatement st;
            st = connect.prepareStatement("CREATE TABLE IF NOT EXISTS Experimentos ("
                    + "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "ID_Experimento TEXT NOT NULL,"
                    + "Propiedad TEXT NOT NULL,"
                    + "Valor TEXT NOT NULL );"
            );
            st.execute();

            st = connect.prepareStatement("CREATE TABLE IF NOT EXISTS Resultados ("
                    + "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "ID_Experimento TEXT NOT NULL,"
                    + "Run INTEGER NOT NULL,"
                    + "Genotipo TEXT NOT NULL,"
                    + "Fenotipo TEXT NOT NULL,"
                    + "Evaluacion TEXT NOT NULL,"
                    + "Fitness DOUBLE NOT NULL,"
                    + "GenesUsados INTEGER NOT NULL );"
            );
            st.execute();

            st = connect.prepareStatement("CREATE TABLE IF NOT EXISTS Logs ("
                    + "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "ID_Experimento TEXT NOT NULL,"
                    + "Run INTEGER NOT NULL,"
                    + "Logger TEXT NOT NULL,"
                    + "Texto TEXT NOT NULL,"
                    + "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP );"
            );
            st.execute();
            
            st = connect.prepareStatement("CREATE TABLE IF NOT EXISTS Generaciones ("
                    + "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "ID_Experimento TEXT NOT NULL,"
                    + "Run INTEGER NOT NULL,"
                    + "Generacion INTEGER NOT NULL,"
                    + "Optimo DOUBLE NOT NULL);"
            );
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to sabe the configuration of experiment.
     * @param configuration experiment configuration
     */
    public void saveExperiment(EvaluationCofing configuration) {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save experiment as ID_Experimento: {0}", configuration.idExperimento);

            PreparedStatement st;

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, "Grammar");
            st.setString(3, String.valueOf(configuration.grammar));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, "Training");
            st.setString(3, String.valueOf(configuration.training));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.DATABASE);
            st.setString(3, String.valueOf(configuration.database));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.RUNS);
            st.setString(3, String.valueOf(configuration.runs));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.FITNESS_METHOD);
            st.setString(3, String.valueOf(configuration.fitnessMethod));
            st.execute();
            
            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.NUM_OF_OBJECTIVES);
            st.setString(3, String.valueOf(configuration.numOfObjetives));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.CHROMOSOME_LENGTH);
            st.setString(3, String.valueOf(configuration.chromosomelength));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.MAX_CNT_WRAPPINGS);
            st.setString(3, String.valueOf(configuration.maxCntWrappings));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.CODON_UPPER_BOUND);
            st.setString(3, String.valueOf(configuration.codonUpperBound));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.SENSIBLE_INIT_VALUE);
            st.setString(3, String.valueOf(configuration.siValue));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.SENSIBLE_INIT_PERCENTILE);
            st.setString(3, String.valueOf(configuration.siPercentile));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.MAX_POPULATION_SIZE);
            st.setString(3, String.valueOf(configuration.maxPopulationSize));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.MAX_GENERATIONS);
            st.setString(3, String.valueOf(configuration.maxGenerations));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.PROB_MUTATION);
            st.setString(3, String.valueOf(configuration.probMutation));
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, EvaluationCofing.PROB_CROSSOVER);
            st.setString(3, String.valueOf(configuration.probCrossover));
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to save the results of experiment
     * @param id_Experimento experiment identifier
     * @param run number of execution
     * @param solution solution to save
     * @param problem problem to save
     */
    public void saveResult(String id_Experimento, int run, Solution<Variable<Integer>> solution, GrammaticalEvolution problem) {
        try {
            PreparedStatement st
                    = connect.prepareStatement("insert into Resultados (ID_Experimento, Run,"
                            + "Genotipo, Fenotipo, Evaluacion, Fitness, GenesUsados)"
                            + " values (?,?,?,?,?,?,?)");

            st.setString(1, id_Experimento);
            st.setInt(2, run);
            st.setString(3, genotypeToString(solution.getVariables()));
            st.setString(4, problem.generatePhenotype(solution).toString());
            st.setString(5, evaluationToString(solution.getProperties()));
            st.setDouble(6, solution.getObjectives().get(0));
            st.setString(7, String.valueOf(problem.generatePhenotype(solution).getUsedGenes()));
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to check if exists an experiment
     * @param id_Experimento experiment identifier
     * @return 0 if not exists; >0 if exists
     */
    public boolean existsID_Experimento(String id_Experimento) {
        Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Searching ID_Experimento: " + id_Experimento);

        PreparedStatement st;
        ResultSet result;
        int existsTable = 0;
        int existsExperiment = 0;
        try {
            st = connect.prepareStatement("SELECT count(*) as existe FROM sqlite_master WHERE type='table' AND name='Experimentos'");
            result = st.executeQuery();
            existsTable = result.getInt("existe");
            
            if (existsTable > 0){
                st = connect.prepareStatement("select count(*) as existe from Experimentos where ID_Experimento = '" + id_Experimento + "'");
                result = st.executeQuery();
                existsExperiment = result.getInt("existe");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        return (existsExperiment > 0);
    }

    /**
     * Transforme a genotpe to string by ; separated value
     * @param variables
     * @return comma separated string
     */
    private String genotypeToString(ArrayList<Variable<Integer>> variables) {
        String result = "";
        for (Variable<Integer> variable : variables) {
            result += variable.getValue() + ";";
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * Transforme an evaluation map to string by ; separated value
     * @param h hashmpa of evaluations
     * @return comma separated string
     */
    private String evaluationToString(HashMap<String, Number> h) {

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

        if (result != "") {
            return result.substring(0, result.length() - 1);
        } else {
            return "Invalid phenotype";
        }
    }
    
    /**
     * Save the best objectives by generation
     * @param id_Experimento experiment identifier
     * @param run number of execution
     * @param array array of objectives by generation
     */
    public void saveBestGenerationObjective(String id_Experimento, int run, Double[] array){
        Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save better goal by generation, ID_EXPERIMENTO: " + id_Experimento);
        
        PreparedStatement st;
        try {            
            for (int i = 0; i < array.length; i++){
                st = connect.prepareStatement("insert into Generaciones(ID_Experimento, Run,"
                            + "Generacion, Optimo)"
                            + " values (?,?,?,?)");
                
            st.setString(1, id_Experimento);
            st.setInt(2, run);
            st.setInt(3, i);
            st.setDouble(4, array[i]);
            st.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}
