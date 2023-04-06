package za.ac.cput.infrastructure;
/**
 * SQLDBConnector.java
 * Class for connecting to a SQL database
 * Author: Peter Buckingham (220165289)
 * Date: 19 March 2021
 */

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDBConnector implements IDatabaseConnector {
    private static SQLDBConnector instance = null;
    private Connection connection = null;
    private Properties props = null;

    private SQLDBConnector() {
        loadProperties();
        try {
            Class.forName(props.getProperty("db.driver"));
            connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLDBConnector getInstance() {
        if (instance == null) {
            synchronized (SQLDBConnector.class) {
                if (instance == null) {
                    instance = new SQLDBConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProperties() {
        props = new Properties();
        try (InputStream inputStream = SQLDBConnector.class.getClassLoader().getResourceAsStream("mysql-db.properties")) {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}