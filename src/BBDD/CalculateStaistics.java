/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BBDD;

import GE.Fitness;
import GE.GrammaticalEvolution;
import Import.CSVReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CalculateStaistics {
    
    private static class Stats{
        
        /**
         * Method to calculate the average value from list
         * @param list list of doubles
         * @return average value
         */
        private static double average(List<Double> list) {
            Double sum = 0.0;
            if(!list.isEmpty()) {
                for (Double mark : list) {
                    sum += mark;
                }
                return sum / list.size();
            }
            return sum;
        }
        
        /**
         * Method to calculate the variance value from list
         * @param list list of doubles
         * @return variance value
         */
        private static double variance(List<Double> list){
            double variance = 0.0;
            for(int i = 0 ; i<10; i++){
                double rango;
                rango = Math.pow(list.get(i) - average(list),2f);
                variance = variance + rango;
            }
            variance = variance / 10f;//suma de diferencias sobre "n" o "n - 1"
            return variance;
        }
        
        /**
         * Method to calculate the deviation of list
         * @param list list of doubles
         * @return deviation value
         */
        private static double deviationStandard(List<Double> list){
            return Math.sqrt(variance(list));
        }
    }
    
    private static class Utilities{
        
        /**
         * Method to translate an array to string formated by CSV
         * @param array array to translate
         * @return string by comma separated
         */
        private static String stringToCSV(double[] array) {
            String result = "";

            if (array.length > 0) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < array.length; i++) {
                    String line = (i + 1) + ";" + array[i];
                    sb.append(line).append("\n");
                }

                result = sb.toString();
            }
            return result;
        }

        /**
         * Method to create a file from string
         * @param s string to copy at file
         * @param path route to create file
         * @throws FileNotFoundException 
         */
        private static void writeCSV(String s, String path) throws FileNotFoundException {
            try (PrintWriter out = new PrintWriter(path)) {
                out.println(s);
            }
        }
        
        /**
         * Method to calculate the CSV path against database
         * @param path database path
         * @param name nambe of CSV
         * @return route
         */
        private static String getCSVpath(String path, String name){
            String aux = path;
            for(int i=aux.length()-1; i>0; i--){
                if(path.charAt(i) == '/')
                    break;
                else
                    aux = aux.substring(0, i);
            }
            
            return aux + name;
        }
    }

    private static final String DATABASE = "database";
    private static final String EXPERIMENTID = "idExperiment";

    private static Connection connect;

    public static void main(String[] args) throws Exception {
        /*CommandLine cmd = startUp(args);
        
        String db = cmd.getOptionValue(DATABASE);
        String exp = cmd.getOptionValue(EXPERIMENTID);*/
        String db ="/home/cgm02/pruebas db/estudios.db";
        String exp = "fit_ABSAcumulated";

        //Connect to database       
        DAO dao = new DAO();
        connect = dao.connect(db);
        
        //create table Estadisticas if not exists
        createTable();
        
        //reset experiment to recalculate
        resetExperiment(exp);
        
        //calculate staistics
        String nameFbyG = exp + "_FbyG.csv";
        String pathFbyG = Utilities.getCSVpath(db, nameFbyG);
        fitnessByGeneration(exp, pathFbyG);
        
        pctInfinityNaN(exp);
        
        timeByExperiment(exp);
        
        numberFoundOptimal(exp);
        
        bestFitnessLastGeneration(exp);
        
        //TODO show results
        
        //close connection
        dao.close();
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

        Option database = new Option("db", DATABASE, true, "database path");
        database.setRequired(true);
        options.addOption(database);

        Option experimentId = new Option("id", EXPERIMENTID, true, "experiment name");
        experimentId.setRequired(true);
        options.addOption(experimentId);

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

        String databaseFilePath = cmd.getOptionValue(DATABASE);
        String experimentName = cmd.getOptionValue(EXPERIMENTID);

        Logger.getLogger(GrammaticalEvolution.class.getName())
                .log(Level.INFO, "Database used: {0}", databaseFilePath);

        Logger.getLogger(GrammaticalEvolution.class.getName())
                .log(Level.INFO, "Experiment used: {0}", experimentName);

        return cmd;
    }
    
    private static void createTable(){
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Create table if not exists Estadisticas");
        try {
            PreparedStatement st;
            st = connect.prepareStatement(  "CREATE TABLE IF NOT EXISTS Estadisticas (\n" +
                                            "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                                            "ID_Experimento TEXT NOT NULL,\n" +
                                            "Tiempo FLOAT NULL,\n" +
                                            "PctOptimos FLOAT NULL,\n" +
                                            "PctInfNaN INTEGER NULL\n" +
                                            ");");
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to reset the previous calculated staistics
     *
     * @param experimentId experiment name used
     */
    private static void resetExperiment(String experimentId) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "reset experiment");
        try {
            PreparedStatement st;
            st = connect.prepareStatement("delete from Estadisticas where ID_Experimento = '" + experimentId + "';");
            st.execute();

            st = connect.prepareStatement("insert into Estadisticas (ID_Experimento) VALUES ('" + experimentId + "');");
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to create the CSV file of fitness by generation
     * @param experimentID experiment name to use
     * @param path route to save the result
     * @throws FileNotFoundException 
     */
    private static void fitnessByGeneration(String experimentID, String path) throws FileNotFoundException {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Creating csv fitness By Generationfor ID_EXPERIMENTO: {0}", experimentID);
        try {
            PreparedStatement st;
            ResultSet result;
            
            int length = getMaxGenerations(experimentID);

            double[] fitGen = new double[length];

            st = connect.prepareStatement("select  Generacion, sum(coalesce(Optimo,0)) as Optimo\n"
                    + "from Generaciones\n"
                    + "where ID_Experimento = '" + experimentID + "'\n"
                    + "group by Generacion\n"
                    + "order by Generacion asc;");
            result = st.executeQuery();

            while (result.next()) {
                fitGen[result.getInt("Generacion")] = result.getDouble("Optimo");
            }
            result.close();
            st.close();

            int runs = getRuns(experimentID);

            for (int i = 0; i < fitGen.length; i++) {
                fitGen[i] = fitGen[i] / runs;
            }
            
            String csv = "Generacion;Optimo\n" + Utilities.stringToCSV(fitGen);
            
            Utilities.writeCSV(csv, path);

        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to calculate the percentage of Infinity and NaN elements at experiment
     * @param experimentID experiment name to use
     */
    private static void pctInfinityNaN(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Percentage infinity or NaN elements for ID_EXPERIMENTO: {0}", experimentID);
        try {
            PreparedStatement st;
            st = connect.prepareStatement("update Estadisticas\n"
                    + "set PctInfNan =     (   select avg(Total.promedio)\n"
                    + "                        from (\n"
                    + "                            select avg(G.number) as promedio\n"
                    + "                            from (\n"
                    + "                                select  substr(T.texto, instr(T.texto, ' %'), -3) as number,\n"
                    + "                                        T.run\n"
                    + "                                from (\n"
                    + "                                    select\n"
                    + "                                        substr( texto,\n"
                    + "                                        instr(texto, 'Infinity or NaN elems.: ') + length('Infinity or NaN elems.: ')\n"
                    + "                                        ) AS texto,\n"
                    + "                                        run\n"
                    + "                                    from logs \n"
                    + "                                    where ID_Experimento = '" + experimentID + "'\n"
                    + "                                    and texto like '%Infinity or NaN elems.: %'\n"
                    + "                                ) AS T\n"
                    + "                            ) AS G\n"
                    + "                        GROUP BY G.run\n"
                    + "                        ) AS Total\n"
                    + "                    )\n"
                    + "where ID_Experimento = '" + experimentID + "';"
            );

            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to calculate the average time by experiment
     * @param experimentID experiment name to use
     */
    private static void timeByExperiment(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Time by experiment for ID_EXPERIMENTO: {0}", experimentID);
        try {
            PreparedStatement st;
            st = connect.prepareStatement("drop table if exists dateDiff;\n"
                    + "create temp table if not exists datediff (id integer, start timestamp, end timestamp, difference timestamp);\n"
                    + "\n"
                    + "insert into datediff (id, start)\n"
                    + "select run, Timestamp \n"
                    + "from Logs \n"
                    + "where ID_Experimento = '" + experimentID + "'\n"
                    + "and Texto like '%Init run%' \n"
                    + "order by run asc;\n"
                    + "\n"
                    + "update datediff\n"
                    + "set end = ( select Timestamp \n"
                    + "            from logs\n"
                    + "            where ID_Experimento = '" + experimentID + "'\n"
                    + "            and run = datediff.id \n"
                    + "            and texto like '%Finished run%');\n"
                    + "\n"
                    + "update datediff\n"
                    + "set difference = (  select cast (( \n"
                    + "                        julianday(D.end) - julianday(D.start)\n"
                    + "                    ) * 24 * 60 as real)\n"
                    + "                    from datediff as D\n"
                    + "                    where datediff.id = D.id);\n"
                    + "\n"
                    + "update Estadisticas\n"
                    + "set Tiempo =    (  select avg(difference) \n"
                    + "                    from datediff\n"
                    + "                )\n"
                    + "where ID_Experimento = '" + experimentID + "';"
            );

            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to calculate the number times that we found and optimal by experiment
     * @param experimentID experiment name to use
     */
    private static void numberFoundOptimal(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Number of times found optimal for ID_EXPERIMENTO: {0}", experimentID);
        try {
            PreparedStatement st;
            st = connect.prepareStatement("update Estadisticas\n"
                    + "set PctOptimos = (   select (100.0 * count(*)) / (select valor from Experimentos where ID_Experimento = '" + experimentID + "' and Propiedad = 'Runs')\n"
                    + "                    from Resultados \n"
                    + "                    where ID_Experimento = '" + experimentID + "' \n"
                    + "                    and Fitness = 0\n"
                    + "                )\n"
                    + "where ID_Experimento = '" + experimentID + "';"
            );

            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
    
    /**
     * Method to calculate the average best fitnes at last generation
     * @param experimentID experiment name used
     * @throws FileNotFoundException 
     */
    private static void bestFitnessLastGeneration(String experimentID) throws FileNotFoundException{
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "Calculate rSquared and typical deviation for ID_EXPERIMENTO: {0}", experimentID);
        
        PreparedStatement st;
        ResultSet result;
            
        try {
            //load target
            String training = getTraining(experimentID);
            CSVReader csv = new CSVReader(training);
            String[][] func = csv.loadMatrix();
            if (func.length == 0)
                throw new Exception("Invalid path '" + training + "' to load training");

            st = connect.prepareStatement(  "select r.fenotipo as Fenotipo,\n" +
                                            "r.Evaluacion as Evaluacion\n" +
                                            "from Resultados r\n" +
                                            "inner join (select min(ID) as optimo\n" +
                                            "from Resultados\n" +
                                            "where ID_Experimento = '" + experimentID + "'\n" +
                                            "group by run) f on (r.ID = f.optimo);");
            result = st.executeQuery();
            
            HashMap<String, String> map = new HashMap<>();//todo duplicados que pasaria
            while (result.next()) {
                map.put(result.getString("Fenotipo"), result.getString("Evaluacion"));
            }
            
            result.close();
            st.close();
            
            ArrayList<Double> list = new ArrayList<>();
            
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //load prediction
                String[] prediction = new String[func.length];
                prediction[0] = pair.getKey().toString();
                String evaluations = pair.getValue().toString();
                String[] aux = evaluations.split(";");
                System.arraycopy(aux, 0, prediction, 1, aux.length);
                
                //calculate fitness r-squared
                Fitness fitness = new Fitness(func, prediction);
                double fValue = fitness.rSquared();
                list.add(fValue);
                
                it.remove(); // avoids a ConcurrentModificationException
            }
            
            double average = Stats.average(list);
            double variance = Stats.variance(list);
            double deviation = Stats.deviationStandard(list);
            
            System.out.println(average + "-" + variance + "-" + deviation);
            //TODO guardar en estadisticas BBDD
        
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }       
    
    /**
     * Method to searh at database the max generations used in a experiment
     * @param experimentID experiment name used
     * @return max generations
     */
    private static int getMaxGenerations(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "getMaxGenerations");
        int length = 0;
                
        try {
            PreparedStatement st;
            st = connect.prepareStatement("select Valor from Experimentos where ID_Experimento = '" + experimentID + "' and Propiedad = 'Max_generations';");
            ResultSet result;
            result = st.executeQuery();
            length = result.getInt("Valor");
            result.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        
        return length;
    }
    
    /**
     * Method to search at database the number of runs used in a experiment
     * @param experimentID experiment name used
     * @return number of runs
     */
    private static int getRuns(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "getRuns");
        int runs = 0;
                
        try {
            PreparedStatement st;
            st = connect.prepareStatement("select Valor from Experimentos where ID_Experimento = '" + experimentID + "' and Propiedad = 'Runs';");
            ResultSet result;
            result = st.executeQuery();
            runs = result.getInt("Valor");
            result.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        
        return runs;
    }
    
    /**
     * Method to search at database the training used in a experiment
     * @param experimentID experiment name used
     * @return training function used
     */
    private static String getTraining(String experimentID) {
        Logger.getLogger(CalculateStaistics.class.getName()).log(Level.INFO, "getTraining");
        String training = "";
                
        try {
            PreparedStatement st;
            st = connect.prepareStatement("select Valor from Experimentos where ID_Experimento = '" + experimentID + "' and Propiedad = 'Training';");
            ResultSet result;
            result = st.executeQuery();
            training = result.getString("Valor");
            result.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(CalculateStaistics.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        
        return training;
    }

}
