package business;

import model.User;
import data.dao.UserDAO;
import data.dao.UserImp;
import business.strategies.CreditStrategyInterface;
import factory.StrategyFactory;

// this service handles user-related business logic
public class UserService {

    private UserDAO userDao;

    public UserService() {
        this.userDao = new UserImp();
    }

    // register a new user
    public boolean registerUser(User user) {
        if (user == null) {
            return false;
        }

        // check if email already exists
        try {
            User existingUser = userDao.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                System.err.println("email already registered: " + user.getEmail());
                return false;
            }
        } catch (Exception e) {
            System.err.println("error checking existing user: " + e.getMessage());
        }

        // assign appropriate credit strategy based on user type
        CreditStrategyInterface strategy = StrategyFactory.createCreditStrategy(user.getUserType());
        user.setCreditStrategy(strategy);

        // save to database
        try {
            return userDao.addUser(user);
        } catch (Exception e) {
            System.err.println("error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // get user by id
    public User getUserById(int userId) {
        try {
            User user = userDao.getUserById(userId);
            if (user != null) {
                // set the appropriate credit strategy
                CreditStrategyInterface strategy = StrategyFactory.createCreditStrategy(user.getUserType());
                user.setCreditStrategy(strategy);
            }
            return user;
        } catch (Exception e) {
            System.err.println("error getting user by id: " + e.getMessage());
            return null;
        }
    }

    // update user information
    public boolean updateUser(User user) {
        try {
            return userDao.updateUser(user);
        } catch (Exception e) {
            System.err.println("error updating user: " + e.getMessage());
            return false;
        }
    }

    // delete user (admin only)
    public boolean deleteUser(int userId) {
        try {
            return userDao.deleteUser(userId);
        } catch (Exception e) {
            System.err.println("error deleting user: " + e.getMessage());
            return false;
        }
    }

    // get all users (for admin reports)
    public java.util.List<User> getAllUsers() {
        try {
            java.util.List<User> users = userDao.getAllUsers();

            // set credit strategy for each user
            for (User user : users) {
                CreditStrategyInterface strategy = StrategyFactory.createCreditStrategy(user.getUserType());
                user.setCreditStrategy(strategy);
            }

            return users;
        } catch (Exception e) {
            System.err.println("error getting all users: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // calculate user's earned credits using their strategy
    public double calculateUserCredits(int userId) {
        User user = getUserById(userId);
        if (user != null && user.getCreditStrategy() != null) {
            return user.calculateEarnedCredits();
        }
        return 0.0;
    }

    // check if user is sponsor
    public boolean isSponsor(int userId) {
        User user = getUserById(userId);
        return user != null && "SPONSOR".equals(user.getUserType());
    }

    // check if user is maintainer
    public boolean isMaintainer(int userId) {
        User user = getUserById(userId);
        return user != null && "MAINTAINER".equals(user.getUserType());
    }
}
