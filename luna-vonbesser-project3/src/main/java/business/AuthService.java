package business;

import model.User;
import data.dao.UserDAO;
import data.dao.UserImp;
import java.sql.SQLException;

// this service handles user authentication (login) business logic
public class AuthService {

    private UserDAO userDao;

    public AuthService() {
        this.userDao = new UserImp();
    }

    // authenticate a user by email and password
    public User authenticate(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            // get user by email from database
            User user = userDao.getUserByEmail(email);

            // check if user exists and password matches
            // in real life, we'd compare hashed passwords!
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }

        } catch (Exception e) {
            System.err.println("authentication error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // check if a user is authorized for certain actions
    public boolean isAuthorized(User user, String requiredRole) {
        if (user == null || requiredRole == null) {
            return false;
        }

        // check if user has the required role/type
        return user.getUserType().equalsIgnoreCase(requiredRole)
                || requiredRole.equalsIgnoreCase("ANY");
    }

    // validate session (check if user is logged in)
    public boolean validateSession(Integer userId, String userEmail) {
        if (userId == null || userEmail == null) {
            return false;
        }

        try {
            User user = userDao.getUserById(userId);
            return user != null && user.getEmail().equals(userEmail);

        } catch (Exception e) {
            System.err.println("session validation error: " + e.getMessage());
            return false;
        }
    }
}
