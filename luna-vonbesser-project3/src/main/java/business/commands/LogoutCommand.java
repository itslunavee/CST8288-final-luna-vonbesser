package business.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Command to handle user logout
public class LogoutCommand implements CommandInterface {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the current session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect to the login page
        return "/login.jsp";
    }

    @Override
    public String getCommandName() {
        return "logout";
    }
}