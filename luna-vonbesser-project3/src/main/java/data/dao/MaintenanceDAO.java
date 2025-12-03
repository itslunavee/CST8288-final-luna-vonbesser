package data.dao;

import java.util.List;
import java.util.Map;

public interface MaintenanceDAO {

    // get maintenance alerts assigned to a maintainer
    List<Map<String, Object>> getMaintenanceAlertsByMaintainer(int maintainerId);

    // count completed maintenance by maintainer
    int countCompletedMaintenance(int maintainerId);

    // count high priority maintenance completed
    int countHighPriorityCompleted(int maintainerId);

    // create a new maintenance alert
    boolean createMaintenanceAlert(int scooterId, String alertType, String description, String priority);

    // assign a maintenance task to a maintainer
    boolean assignMaintenanceTask(int alertId, int maintainerId);
}
