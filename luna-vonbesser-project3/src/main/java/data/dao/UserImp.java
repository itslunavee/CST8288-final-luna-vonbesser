package data.dao;

import model.User;
import data.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// this is the actual class that talks to the database
// it implements the UserDAO interface, so it has to provide real code for all those methods
public class UserImp implements UserDAO {

    // this method saves a new user to the database
    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (email, password, name, user_type, credits_balance) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources automatically closes the connection when we're done
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // set the values for the SQL placeholders (the ? marks)
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());  // in real life we'd hash this first!
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getUserType());
            stmt.setDouble(5, user.getCreditsBalance());

            // execute the insert and check if it worked
            int rowsAffected = stmt.executeUpdate();
            
            // get the auto-generated id from the database
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;  // returns true if we actually inserted a row
            }

        } catch (SQLException e) {
            // if something goes wrong, print the error and return false
            System.err.println("error adding user to database: " + e.getMessage());
        }

        return false;
    }

    // this method finds a user by their email address
    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // if we found a user, create a User object from the database result
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting user by email: " + e.getMessage());
        }

        return null;  // returns null if no user found
    }
    
    // this method finds a user by their id
    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            // if we found a user, create a User object from the database result
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting user by id: " + e.getMessage());
        }

        return null;  // returns null if no user found
    }
    
    // this method updates an existing user's information in the database
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET email = ?, name = ?, user_type = ?, credits_balance = ? WHERE id = ?";
        
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // set the values for the SQL placeholders
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getUserType());
            stmt.setDouble(4, user.getCreditsBalance());
            stmt.setInt(5, user.getId());

            // execute the update and check if it worked
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // returns true if we actually updated a row

        } catch (SQLException e) {
            System.err.println("error updating user in database: " + e.getMessage());
        }

        return false;
    }
    
    // this method deletes a user from the database by their id
    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE id = ?";
        
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            
            // execute the delete and check if it worked
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // returns true if we actually deleted a row

        } catch (SQLException e) {
            System.err.println("error deleting user from database: " + e.getMessage());
        }

        return false;
    }
    
    // this method retrieves all users from the database, ordered by name
    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM Users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DataSource.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through all the results and create User objects
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("error getting all users from database: " + e.getMessage());
        }

        return users;
    }
    
    // this method checks if an email already exists in the database
    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM Users WHERE email = ?";
        
        try (Connection conn = DataSource.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // check if the count is greater than 0
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error checking if email exists in database: " + e.getMessage());
        }

        return false;
    }
    
    // helper method to create a User object from the current row of a ResultSet
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User.UserBuilder builder = new User.UserBuilder(
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name")
        );
        
        builder.id(rs.getInt("id"))
               .userType(rs.getString("user_type"))
               .creditsBalance(rs.getDouble("credits_balance"))
               .createdAt(rs.getTimestamp("created_at"));
        
        return builder.build();
    }
}
