package business.commands;

import business.UserService;
import model.User;
import factory.UserFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// this is a concrete command for handling user registration
public class RegisterCommand implements CommandInterface {

    private UserService userService;

    public RegisterCommand() {
        this.userService = new UserService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        // get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String userType = request.getParameter("userType");

        // basic validation
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "email is required");
            return "register.jsp";
        }

        if (password == null || password.length() < 6) {
            request.setAttribute("error", "password must be at least 6 characters");
            return "register.jsp";
        }

        // check if user already exists (we'd need a method for this in userService)
        // for now, we'll skip this check
        // create user using factory pattern
        User newUser = UserFactory.createUser(email, password, name, userType);

        // register the user
        boolean success = userService.registerUser(newUser);

        if (success) {
            request.setAttribute("message", "registration successful! please login.");
            return "login.jsp";
        } else {
            request.setAttribute("error", "registration failed - email might already exist");
            return "register.jsp";
        }
    }

    @Override
    public String getCommandName() {
        return "register";
    }
}
