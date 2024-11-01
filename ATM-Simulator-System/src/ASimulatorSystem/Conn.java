package ASimulatorSystem;

import java.sql.*;

public class Conn {
    Connection c;
    Statement s;

    public Conn() {
        try {
            // Load the driver (optional in JDBC 4.0+)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            c = DriverManager.getConnection("jdbc:mysql:///bankmanagementsystem", "root", "Bg1403@#");

            // Create a statement object
            s = c.createStatement();

            System.out.println("Database connected successfully!");

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        }
    }
}
