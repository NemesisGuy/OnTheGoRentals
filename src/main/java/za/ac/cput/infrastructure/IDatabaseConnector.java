package za.ac.cput.infrastructure;
/**
 * IDatabaseConnector.java
 * Interface for the DatabaseConnector
 * Author: Peter Buckingham (220165289)
 * Date: 19 March 2021
 */

import java.sql.Connection;


public interface IDatabaseConnector {
    Connection getConnection();

    void closeConnection();
}