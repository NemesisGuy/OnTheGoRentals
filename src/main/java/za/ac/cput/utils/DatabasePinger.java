package za.ac.cput.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabasePinger {
    public static void main(String[] args) {

//        String jdbcUrl = "jdbc:mysql://localhost:3306/app_db";
        String jdbcUrl = "jdbc:mysql://localhost:6033/app_db";
        String username = "root";
        String password = "my_secret_password";
        //    String username = "root";wattwarden
        //  String password = "my_secret_password";wattwarden
        //db.url = jdbc:mysql:;
        //librarydatabase.nemesisnet.co.za:3306/LiberLendDatabase
        //      String username = "CPUTUser";
        //  String password = "CPUT-User@LiberLendDatabase";
        //    String username = "db_user";
        //     String password = "db_user_pass";


        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("---- Ping ----");
            System.out.println("Database ping successful!");
            System.out.println();

            System.out.println("---- Connection ----");
            System.out.println("Connection: " + conn);
            System.out.println("Connection URL: " + conn.getMetaData().getURL());
            System.out.println("Connection Username: " + conn.getMetaData().getUserName());
            System.out.println();

            System.out.println("---- Connection Driver ----");

            System.out.println("Connection Driver: " + conn.getMetaData().getDriverName());
            System.out.println("Connection Driver Version: " + conn.getMetaData().getDriverVersion());
            System.out.println("Connection Driver Major Version: " + conn.getMetaData().getDriverMajorVersion());
            System.out.println("Connection Driver Minor Version: " + conn.getMetaData().getDriverMinorVersion());
            System.out.println();

            System.out.println("---- Connection Database Product ----");

            System.out.println("Connection Database Product Name: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Connection Database Product Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("Connection Database Major Version: " + conn.getMetaData().getDatabaseMajorVersion());
            System.out.println("Connection Database Minor Version: " + conn.getMetaData().getDatabaseMinorVersion());
            System.out.println("Connection Database Product Name: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Connection Database Product Version: " + conn.getMetaData().getDatabaseProductVersion());


        } catch (Exception e) {

            System.err.println("Error pinging database: " + e.getMessage());
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
