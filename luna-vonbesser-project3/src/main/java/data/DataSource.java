package data;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// this class handles connecting to our mysql database
// it's a singleton so we only have one connection setup
public class DataSource {
    private static String url;
    private static String user;
    private static String password;
    
    // static block runs when the class is first loaded
    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try {
            // load the database properties from our config file
            Properties props = new Properties();
            InputStream input = DataSource.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input == null) {
                System.err.println("uh oh, couldn't find database.properties file");
                return;
            }

            props.load(input);

            // get our connection details from the properties file
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            // load the mysql driver
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            System.err.println("failed to setup database connection: " + e.getMessage());
        }
    }
    
    // this gives us a fresh connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}