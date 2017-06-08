package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ashok on 4/14/2017.
 */
public class h2DBConnection {
    private static Connection connection;
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String url = "jdbc:h2:./gmail";
    private static final String user = "sa";
    private static final String pass = "";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null) {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, pass);
        }
        return connection;
    }

}
