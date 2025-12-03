package business.commands;

import java.util.List;
import java.util.Map;

import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewMaintenanceAlertsCommand implements CommandInterface {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        MaintenanceDAO maintenanceDao = new MaintenanceDAOImp();
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        int maintainerId = (int) session.getAttribute("userId");
        List<Map<String, Object>> alerts = maintenanceDao.getMaintenanceAlertsByMaintainer(maintainerId);
        request.setAttribute("alerts", alerts);
        return "viewMaintenanceAlerts.jsp";
    }
    @Override
    public String getCommandName() { return "viewMaintenanceAlerts"; }
}
