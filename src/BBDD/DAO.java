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

/**
 *
 * @author Carlos García Moreno
 */
public class DAO {

    Connection connect;

    public Connection connect(String url) {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (connect != null) {
                Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Connected to: {0}", url);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, "Unable to connect to the database {0}", ex.getMessage());
        }
        return connect;
    }

    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //TODO si no existen las tablas crearlas y si existen dejarlas o que¿?
    public void dropTables() {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Drop tables");
            PreparedStatement st = connect.prepareStatement("DROP TABLE if exists Experimentos;");
            st.execute();

            st = connect.prepareStatement("DROP TABLE  if exists Resultados;");
            st.execute();

            st = connect.prepareStatement("DROP TABLE  if exists Logs;");
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void createTables() {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Create init tables");

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
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void saveExperiment(EvaluationCofing configuration) {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save experiment as ID_Experimento: {0}", configuration.idExperimento);

            PreparedStatement st;

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, "Grammar");
            String[] grammarList = configuration.grammar.split("/");
            st.setString(3, grammarList[grammarList.length - 1]);
            st.execute();

            st = connect.prepareStatement("insert into Experimentos (ID_Experimento, "
                    + "Propiedad, Valor)"
                    + " values (?,?,?)");

            st.setString(1, configuration.idExperimento);
            st.setString(2, "Training");
            String[] trainingList = configuration.training.split("/");
            st.setString(3, trainingList[trainingList.length - 1]);
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

    public void saveResult(String ID_Experimento, int run, Solution<Variable<Integer>> solution, GrammaticalEvolution problem) {
        try {
            Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Save result for ID_Experimento: {0}", ID_Experimento);

            PreparedStatement st
                    = connect.prepareStatement("insert into Resultados (ID_Experimento, Run,"
                            + "Genotipo, Fenotipo, Evaluacion, Fitness, GenesUsados)"
                            + " values (?,?,?,?,?,?,?)");

            st.setString(1, ID_Experimento);
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

    public boolean existsID_Experimento(String id_Experimento) {
        Logger.getLogger(DAO.class.getName()).log(Level.INFO, "Searching ID_Experimento: " + id_Experimento);

        ResultSet result = null;
        int existe = 0;
        try {
            PreparedStatement st = connect.prepareStatement("select count(*) as existe from Experimentos where ID_Experimento = '" + id_Experimento + "'");
            result = st.executeQuery();
            existe = result.getInt("existe");
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        return (existe > 0);
    }

    private String genotypeToString(ArrayList<Variable<Integer>> variables) {
        String result = "";
        for (Variable<Integer> variable : variables) {
            result += variable.getValue() + ";";
        }
        return result.substring(0, result.length() - 1);
    }

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
}
