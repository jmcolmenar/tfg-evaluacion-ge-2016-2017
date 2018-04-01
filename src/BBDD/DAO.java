/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BBDD;

import GE.GrammaticalEvolution;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 *
 * @author Carlos García Moreno
 */
public class DAO {
    String url = "db/ge.db";
    
    Connection connect;
    
    public void connect(){
        try{
            connect = DriverManager.getConnection("jdbc:sqlite:"+url);
            if (connect != null)
                System.out.println("Connected to: " + url);            
        }
        catch (SQLException ex) {
            System.err.println("Unable to connect to the database\\n" + ex.getMessage());
        }
    }
    
    public void close(){
        try{
            connect.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveExperiment(){
        
    }
    
    public void saveResult(Solution<Variable<Integer>> solution, GrammaticalEvolution problem){
        try {
            PreparedStatement st = 
                    connect.prepareStatement("insert into Resultados (IdExp, " + 
                            "Genotipo, Fenotipo, Eval, Fitness, GenesUsados)" + 
                            " values (?,?,?,?,?,?)");
            st.setString(1, "Not implemented");
            st.setString(2, "Not implemented");
            st.setString(3, problem.generatePhenotype(solution).toString());
            st.setString(4, "Not implemented");
            st.setDouble(5, solution.getObjectives().get(0));
            st.setString(6, String.valueOf(problem.generatePhenotype(solution).getUsedGenes()));
            st.execute();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } 
    }    
}
