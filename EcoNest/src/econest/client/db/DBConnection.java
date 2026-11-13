package econest.client.db;

import econest.client.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// one shared object that gives out connections (singleton).
// change the user/password to match your MySQL, or set the ECONEST_DB_* env variables
public final class DBConnection {

    private static final String URL  = System.getenv().getOrDefault("ECONEST_DB_URL",
            "jdbc:mysql://localhost:3306/econest_db?useSSL=false&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("ECONEST_DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("ECONEST_DB_PASS", "$my5qlP455W0RD!");

    private static DBConnection instance;

    private DBConnection() { }

    public static synchronized DBConnection getInstance() {
        if (instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new DatabaseException("Cannot connect to the database: " + e.getMessage(), e);
        }
    }
}
