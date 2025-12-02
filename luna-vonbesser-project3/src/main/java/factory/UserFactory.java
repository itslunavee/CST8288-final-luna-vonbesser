package factory;

import model.User;

// simple factory to create different types of users
public class UserFactory {

    // creates a user object based on the user type
    public static User createUser(String email, String password, String name, String userType) {
        // im reusing the same User class for now
        return new User.UserBuilder(email, password, name)
                .userType(userType)
                .build();
    }
}
