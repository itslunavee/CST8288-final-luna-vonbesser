package business.commands;

import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScheduleMaintenanceCommand implements CommandInterface {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        MaintenanceDAO maintenanceDao = new MaintenanceDAOImp();
        int alertId = Integer.parseInt(request.getParameter("alertId"));
        int maintainerId = (int) request.getSession(false).getAttribute("userId");
        boolean success = maintenanceDao.assignMaintenanceTask(alertId, maintainerId);
        request.setAttribute("success", success);
        return "scheduleMaintenance.jsp";
    }
    @Override
    public String getCommandName() { return "scheduleMaintenance"; }
}
