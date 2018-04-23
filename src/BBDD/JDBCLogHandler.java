/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BBDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author cgm02
 */
public class JDBCLogHandler extends Handler {

    /**
     * A string that contains the classname of the JDBC driver. This value is
     * filled by the constructor.
     */
    String driverString;

    /**
     * A string that contains the connection string used by the JDBC driver.
     * This value is filled by the constructor.
     */
    String connectionString;

    /**
     * Used to hold the connection to the JDBC data source.
     */
    Connection connection;
    
    /**
     * Used to save the ID_Experimento at logs table.
     */
    String idExperimento;
    
    /**
     * Used to save the execution number of algorithm at logs table.
     */
    int run;
    
    
    /**
     * Setter to set the number of execution algorithm.
     * @param run number of execution
     */
    public void setRun(int run){
        this.run = run;
    }

    /**
     * A SQL statement used to insert into the log table.
     */
    protected final static String insertSQL
            = "insert into logs (ID_Experimento, Run, Logger, Texto)"
            + "values(?,?,?,?)";

    /**
     * A PreparedStatement object used to hold the main insert statement.
     */
    protected PreparedStatement prepInsert;

    /**
     * @param driverString The JDBC driver to use.
     * @param connectionString The connection string that specifies the database
     * to use.
     */
    public JDBCLogHandler(String idExperimento, String driverString,
            String connectionString) throws SQLException {
        try {
            this.driverString = driverString;
            this.connectionString = connectionString;
            this.idExperimento = idExperimento;

            Class.forName(driverString);
            connection = DriverManager.getConnection(connectionString);
            prepInsert = connection.prepareStatement(insertSQL);
        } catch (ClassNotFoundException e) {
            System.err.println("Error on open: " + e);
        } catch (SQLException e) {
            System.err.println("Error on open: " + e);
        }
    }

    /**
     * Internal method used to truncate a string to a specified width. Used to
     * ensure that SQL table widths are not exceeded.
     *
     * @param str The string to be truncated.
     * @param length The maximum length of the string.
     * @return The string truncated.
     */
    static public String truncate(String str, int length) {
        if (str.length() < length) {
            return str;
        }
        return (str.substring(0, length));
    }

    /**
     * Overridden method used to capture log entries and put them into a JDBC
     * database.
     *
     * @param record The log record to be stored.
     */
    @Override
    public void publish(LogRecord record) {
        // first see if this entry should be filtered out
        if (getFilter() != null) {
            if (!getFilter().isLoggable(record)) {
                return;
            }
        }

        // now store the log entry into the table
        try {
            prepInsert.setString(1, this.idExperimento);
            prepInsert.setInt(2, this.run);
            prepInsert.setString(3, truncate(record.getLoggerName(), 63));
            prepInsert.setString(4, truncate(record.getMessage(), 255));
            prepInsert.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error on open: " + e);
        }

    }

    @Override
    public void flush() {
        
    }

    /**
     * Called to close this log handler.
     */
    @Override
    public void close() throws SecurityException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error on close: " + e);
        }
    }

}
