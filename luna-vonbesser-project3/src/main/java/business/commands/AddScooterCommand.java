package business.commands;

import business.ScooterService;
import model.Scooter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// this command handles adding a new scooter (for sponsors only)
public class AddScooterCommand implements CommandInterface {
    
    private ScooterService scooterService;
    
    public AddScooterCommand() {
        this.scooterService = new ScooterService();
    }
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        // check if user is logged in and is a sponsor
        HttpSession session = request.getSession(false);
        if (session == null) {
            request.setAttribute("error", "you must be logged in");
            return "login.jsp";
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        
        if (userId == null) {
            request.setAttribute("error", "session expired - please login again");
            return "login.jsp";
        }
        
        if (!"SPONSOR".equals(userType)) {
            request.setAttribute("error", "only sponsors can add scooters");
            return "dashboard.jsp";
        }
        
        // get form data
        String vehicleNumber = request.getParameter("vehicleNumber");
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String color = request.getParameter("color");
        String batteryCapacityStr = request.getParameter("batteryCapacity");
        
        // validate required fields
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            request.setAttribute("error", "vehicle number is required");
            return "addScooter.jsp";
        }
        
        // parse battery capacity
        int batteryCapacity = 250;  // default
        try {
            batteryCapacity = Integer.parseInt(batteryCapacityStr);
        } catch (NumberFormatException e) {
            // use default
        }
        
        // create scooter using builder pattern
        Scooter scooter = new Scooter.ScooterBuilder(vehicleNumber, make, model, userId)
            .color(color)
            .batteryCapacity(batteryCapacity)
            .build();
        
        // save to database
        boolean success = scooterService.addScooter(scooter);
        
        if (success) {
            request.setAttribute("message", "scooter added successfully!");
        } else {
            request.setAttribute("error", "failed to add scooter - vehicle number might already exist");
        }
        
        return "addScooter.jsp";
    }
    
    @Override
    public String getCommandName() {
        return "addScooter";
    }
}
