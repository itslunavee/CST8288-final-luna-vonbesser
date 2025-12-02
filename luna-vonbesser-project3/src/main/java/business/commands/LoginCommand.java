package business.commands;

import business.AuthService;
import model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// this is a concrete command for handling user login
// it encapsulates all the login logic in one object
public class LoginCommand implements CommandInterface{

    private AuthService authService;

    public LoginCommand() {
        // create the service we need
        this.authService = new AuthService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        // get the parameters from the request
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // basic validation - check if email and password were provided
        if (email == null || password == null || email.trim().isEmpty()) {
            request.setAttribute("error", "email and password are required");
            return "login.jsp";  // go back to login page
        }

        // try to authenticate the user
        User user = authService.authenticate(email, password);

        if (user == null) {
            // authentication failed
            request.setAttribute("error", "invalid email or password");
            return "login.jsp";
        }

        // login successful - set up the session
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userType", user.getUserType());

        // welcome message
        request.setAttribute("message", "welcome back, " + user.getName() + "!");

        // go to dashboard
        return "dashboard.jsp";
    }

    @Override
    public String getCommandName() {
        return "login";
    }
}
