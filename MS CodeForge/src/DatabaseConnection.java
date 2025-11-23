import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ms_codeforge";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection;
    
    static {
        initializeDatabase();
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
                // Test the connection
                if (!connection.isValid(2)) {
                    throw new SQLException("Database connection is not valid");
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    private static void initializeDatabase() {
        try {
            // Test connection and create database if it doesn't exist
            Connection testConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USERNAME, PASSWORD);
            Statement stmt = testConn.createStatement();
            
            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ms_codeforge");
            stmt.close();
            testConn.close();
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Utility method for counting records
    public static int getCount(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
    
    // Utility method for executing updates
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
        }
    }
    
    // Check if database is connected
    public static boolean isConnected() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Get database metadata
    public static void printDatabaseInfo() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName());
            System.out.println("URL: " + metaData.getURL());
        } catch (SQLException e) {
            System.err.println("Error getting database info: " + e.getMessage());
        }
    }
}