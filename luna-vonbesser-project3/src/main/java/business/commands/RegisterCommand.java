package business.commands;

import java.io.IOException;

import business.UserService;
import factory.UserFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            String confirmPassword = request.getParameter("confirmPassword");
            String name = request.getParameter("name"); // This is missing in your form!
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

            // Check password confirmation
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "passwords do not match");
                return "register.jsp";
            }

            // Check if name is provided
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "name is required");
                return "register.jsp";
            }

            // create user using factory pattern
            User newUser = UserFactory.createUser(email, password, name, userType);

            // register the user
            boolean success = userService.registerUser(newUser);

            if (success) {
                // Set success message in session so it persists after redirect
                request.getSession().setAttribute("message", "Registration successful! Please login.");
                // Use redirect instead of forward
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return null; // Important: return null when using sendRedirect
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
