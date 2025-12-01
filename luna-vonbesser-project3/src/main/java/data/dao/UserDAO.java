package data.dao;

import model.User;

// this interface defines what our user data access object can do
public interface UserDAO {
    
    // add a new user to the database
    boolean addUser(User user);
    
    // find a user by their email address (we use email for login)
    User getUserByEmail(String email);
    
    // we can add more methods later if we need them, like:
    // getUserById, updateUser, deleteUser, etc.
}
