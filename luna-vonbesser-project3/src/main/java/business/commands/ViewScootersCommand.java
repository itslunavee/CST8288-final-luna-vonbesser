package business.commands;

import java.util.List;

import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Scooter;

// command to view all scooters
public class ViewScootersCommand implements CommandInterface {

    private final ScooterDAO scooterDAO;

    public ViewScootersCommand() {
        this.scooterDAO = new ScooterDAOImp();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            // fetch all scooters from the database
            List<Scooter> scooters = scooterDAO.getAllScooters();
            
            // set scooters as a request attribute
            request.setAttribute("scooters", scooters);
            
            // forward to the viewScooters.jsp page
            return "/viewScooters.jsp";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to fetch scooters.");
            return "/error.jsp";
        }
    }

    @Override
    public String getCommandName() {
        return "viewScooters";
    }
}