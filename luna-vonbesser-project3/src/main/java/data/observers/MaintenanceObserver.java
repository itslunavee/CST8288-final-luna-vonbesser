package data.observers;

import model.Scooter;

public class MaintenanceObserver implements ScooterObserver {
    @Override
    public void onScooterStateChanged(Scooter scooter) {
        if (scooter.needsMaintenance()) {
            // Use MaintenanceDAOImp to persist alert with schema fields
            data.dao.MaintenanceDAOImp dao = new data.dao.MaintenanceDAOImp();
            String alertType = "MAINTENANCE_REQUIRED";
            String description = "Scooter needs maintenance: " + scooter.getVehicleNumber();
            String priority = (scooter.getTireWearHours() > 50 || scooter.getBrakeWearHours() > 30) ? "HIGH" : "NORMAL";
            dao.createMaintenanceAlert(scooter.getId(), alertType, description, priority);
        }
    }
}
