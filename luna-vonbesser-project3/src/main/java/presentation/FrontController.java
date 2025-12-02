package presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import business.commands.CommandInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// this is the front controller servlet it handles all requests
// and routes them to the appropriate command objects
@WebServlet("/controller")
public class FrontController extends HttpServlet {

    // map of command names to command objects
    private Map<String, CommandInterface> commands;

    @Override
    public void init() throws ServletException {
        super.init();

        // initialize the command map
        commands = new HashMap<>();

        // register all available commands
        commands.put("login", new business.commands.LoginCommand());
        commands.put("register", new business.commands.RegisterCommand());
        commands.put("addScooter", new business.commands.AddScooterCommand());
        commands.put("logout", new business.commands.LogoutCommand());
        commands.put("billing", new business.commands.BillingCommand());
        commands.put("viewScooters", new business.commands.ViewScootersCommand());
        // add more commands here as needed
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // get the command parameter from the request
        String commandName = request.getParameter("command");

        // if no command specified, default to home
        if (commandName == null || commandName.trim().isEmpty()) {
            commandName = "home";
        }

        // get the command object from our map
        CommandInterface command = commands.get(commandName);

        // check if command exists
        if (command == null) {
            // unknown command - show error
            request.setAttribute("error", "unknown command: " + commandName);
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            // execute the command and get the next page
            String nextPage = command.execute(request, response);

            // forward to the next page
            request.getRequestDispatcher(nextPage).forward(request, response);

        } catch (Exception e) {
            // handle any errors that occur during command execution
            e.printStackTrace();
            request.setAttribute("error", "error executing command: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
