package data.dao;

import model.User;
import data.DataSource;
import java.sql.*;

// this is the actual class that talks to the database
// it implements the UserDAO interface, so it has to provide real code for all those methods
public class UserImp implements UserDAO {

    // this method saves a new user to the database
    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (email, password, name, user_type, credits_balance) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources automatically closes the connection when we're done
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // set the values for the SQL placeholders (the ? marks)
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());  // in real life we'd hash this first!
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType());
            stmt.setDouble(5, user.getCreditsBalance());

            // execute the insert and check if it worked
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // returns true if we actually inserted a row

        } catch (SQLException e) {
            // if something goes wrong, print the error and return false
            System.err.println("error adding user to database: " + e.getMessage());
            return false;
        }
    }

    // this method finds a user by their email address
    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        User user = null;

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // if we found a user, create a User object from the database result
            if (rs.next()) {
                user = new User.UserBuilder(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name")
                )
                        .id(rs.getInt("id"))
                        .userType(rs.getString("user_type"))
                        .creditsBalance(rs.getDouble("credits_balance"))
                        .createdAt(rs.getTimestamp("created_at"))
                        .build();
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting user by email: " + e.getMessage());
        }

        return user;  // returns null if no user found
    }
}
