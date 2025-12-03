package business.commands;

import java.io.IOException;

import business.UserService;
import factory.UserFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

// this is a concrete command for handling user registration
public class RegisterCommand implements CommandInterface {

    private final UserService userService;

    public RegisterCommand() {
        this.userService = new UserService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        try {
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

            // create user using factory pattern
            User newUser = UserFactory.createUser(email, password, name, userType);

            // register the user
            boolean success = userService.registerUser(newUser);

            if (success) {
                request.setAttribute("message", "Registration successful! Please login.");
                response.sendRedirect("login.jsp");
                return null; // response.sendRedirect handles the redirection
            } else {
                request.setAttribute("error", "Registration failed - email might already exist");
                return "register.jsp";
            }
        } catch (IOException e) {
            request.setAttribute("error", "An unexpected error occurred. Please try again.");
            return "register.jsp";
        }
    }

    @Override
    public String getCommandName() {
        return "register";
    }
}
