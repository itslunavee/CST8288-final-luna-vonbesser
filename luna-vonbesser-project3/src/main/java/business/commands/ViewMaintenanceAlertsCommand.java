package business.commands;

import java.util.List;
import java.util.Map;

import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewMaintenanceAlertsCommand implements CommandInterface {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        MaintenanceDAO maintenanceDao = new MaintenanceDAOImp();
        javax.servlet.http.HttpSession session = request.getSession(false);
        int maintainerId = (int) session.getAttribute("userId");
        List<Map<String, Object>> alerts = maintenanceDao.getMaintenanceAlertsByMaintainer(maintainerId);
        request.setAttribute("alerts", alerts);
        return "viewMaintenanceAlerts.jsp";
    }
    @Override
    public String getCommandName() { return "viewMaintenanceAlerts"; }
}
