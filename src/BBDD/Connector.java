/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BBDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos García Moreno
 */
public class Connector {
    String url = "db/ge.db";
    
    Connection connect;
    
    public void connect(){
        try{
            connect = DriverManager.getConnection("jdbc:sqlite:"+url);
            if (connect != null)
                System.out.println("Connected to: " + url);            
        }
        catch (SQLException ex) {
            System.err.println("Unable to connect to the database\\n\"+ex.getMessage()");
        }
    }
    
    public void close(){
        try{
            connect.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
