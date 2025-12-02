package data.dao;

import model.User;
import java.util.List;

// updated interface with all needed methods
public interface UserDAO {

    // add a new user to the database
    boolean addUser(User user);

    // find a user by email
    User getUserByEmail(String email);

    // find a user by id
    User getUserById(int id);

    // update user information
    boolean updateUser(User user);

    // delete a user
    boolean deleteUser(int userId);

    // get all users (for admin)
    List<User> getAllUsers();

    // check if email exists
    boolean emailExists(String email);
}
