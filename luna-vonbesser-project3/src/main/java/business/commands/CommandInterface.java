package business.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// this is the command interface
// it's part of the Command pattern that encapsulate requests as objects
public interface CommandInterface {
    
    // execute the command and return the next page to go to
    String execute(HttpServletRequest request, HttpServletResponse response);
    
    // optional: get the command name for debugging
    String getCommandName();
}