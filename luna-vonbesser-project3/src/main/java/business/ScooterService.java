package business;

import model.Scooter;
import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import com.cesc.business.observer.MaintenanceAlertSystem;
import java.util.List;

// this service handles scooter-related business logic
public class ScooterService {

    private ScooterDAO scooterDao;
    private MaintenanceDAO maintenanceDao;
    private MaintenanceAlertSystem alertSystem;

    public ScooterService() {
        this.scooterDao = new ScooterDAOImp();
        this.maintenanceDao = new MaintenanceDAOImp();
        this.alertSystem = MaintenanceAlertSystem.getInstance();
    }

    // add a new scooter
    public boolean addScooter(Scooter scooter) {
        if (scooter == null) {
            return false;
        }

        // validate required fields
        if (scooter.getVehicleNumber() == null || scooter.getVehicleNumber().trim().isEmpty()) {
            System.err.println("vehicle number is required");
            return false;
        }

        // check if vehicle number already exists
        Scooter existing = scooterDao.getScooterByVehicleNumber(scooter.getVehicleNumber());
        if (existing != null) {
            System.err.println("scooter with vehicle number " + scooter.getVehicleNumber() + " already exists");
            return false;
        }

        try {
            // save to database
            boolean success = scooterDao.addScooter(scooter);

            if (success) {
                // notify observers about new scooter
                alertSystem.triggerNewScooterAlert(
                        scooter.getId(),
                        scooter.getVehicleNumber(),
                        scooter.getSponsorId()
                );

                // check if scooter needs maintenance alerts
                checkMaintenanceAlerts(scooter);
            }

            return success;

        } catch (Exception e) {
            System.err.println("error adding scooter: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // get all scooters
    public List<Scooter> getAllScooters() {
        try {
            List<Scooter> scooters = scooterDao.getAllScooters();

            // check each scooter for maintenance needs
            for (Scooter scooter : scooters) {
                checkMaintenanceAlerts(scooter);
            }

            return scooters;
        } catch (Exception e) {
            System.err.println("error getting all scooters: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // get scooters by sponsor
    public List<Scooter> getScootersBySponsor(int sponsorId) {
        try {
            return scooterDao.getScootersBySponsor(sponsorId);
        } catch (Exception e) {
            System.err.println("error getting scooters by sponsor: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // update scooter
    public boolean updateScooter(Scooter scooter) {
        try {
            boolean success = scooterDao.updateScooter(scooter);

            if (success) {
                // check for maintenance alerts after update
                checkMaintenanceAlerts(scooter);
            }

            return success;
        } catch (Exception e) {
            System.err.println("error updating scooter: " + e.getMessage());
            return false;
        }
    }

    // delete scooter
    public boolean deleteScooter(String vehicleNumber) {
        try {
            return scooterDao.deleteScooter(vehicleNumber);
        } catch (Exception e) {
            System.err.println("error deleting scooter: " + e.getMessage());
            return false;
        }
    }

    // check scooter for maintenance needs and trigger alerts
    private void checkMaintenanceAlerts(Scooter scooter) {
        if (scooter == null) {
            return;
        }

        // check battery
        if (scooter.getCurrentCharge() < 20) {
            alertSystem.triggerBatteryAlert(scooter.getId(), scooter.getCurrentCharge());

            // also create database record
            maintenanceDao.createMaintenanceAlert(
                    scooter.getId(),
                    "BATTERY",
                    "Low battery: " + scooter.getCurrentCharge() + "%",
                    scooter.getCurrentCharge() < 10 ? "HIGH" : "MEDIUM"
            );
        }

        // check tire wear
        if (scooter.getTireWearHours() > 50) {
            alertSystem.triggerMaintenanceAlert(
                    scooter.getId(),
                    "tires",
                    scooter.getTireWearHours()
            );

            maintenanceDao.createMaintenanceAlert(
                    scooter.getId(),
                    "TIRES",
                    "Tire wear: " + scooter.getTireWearHours() + " hours",
                    scooter.getTireWearHours() > 70 ? "HIGH" : "MEDIUM"
            );
        }

        // check brake wear
        if (scooter.getBrakeWearHours() > 30) {
            alertSystem.triggerMaintenanceAlert(
                    scooter.getId(),
                    "brakes",
                    scooter.getBrakeWearHours()
            );

            maintenanceDao.createMaintenanceAlert(
                    scooter.getId(),
                    "BRAKES",
                    "Brake wear: " + scooter.getBrakeWearHours() + " hours",
                    scooter.getBrakeWearHours() > 45 ? "HIGH" : "MEDIUM"
            );
        }
    }

    // simulate scooter usage (for testing/demo)
    public void simulateUsage(int scooterId, double hours) {
        try {
            Scooter scooter = scooterDao.getScooterById(scooterId);
            if (scooter != null) {
                // update usage hours
                scooter.setTotalUsageHours(scooter.getTotalUsageHours() + hours);
                scooter.setTireWearHours(scooter.getTireWearHours() + hours);
                scooter.setBrakeWearHours(scooter.getBrakeWearHours() + (hours * 0.8)); // brakes wear slower

                // reduce battery (about 5% per hour)
                int batteryDrain = (int) (hours * 5);
                scooter.setCurrentCharge(Math.max(0, scooter.getCurrentCharge() - batteryDrain));

                // save changes
                scooterDao.updateScooter(scooter);

                // check for alerts
                checkMaintenanceAlerts(scooter);

                System.out.println("simulated " + hours + " hours of usage for scooter #" + scooterId);
            }
        } catch (Exception e) {
            System.err.println("error simulating usage: " + e.getMessage());
        }
    }

    // charge scooter
    public boolean chargeScooter(int scooterId, int chargeAmount) {
        try {
            Scooter scooter = scooterDao.getScooterById(scooterId);
            if (scooter != null) {
                int newCharge = Math.min(100, scooter.getCurrentCharge() + chargeAmount);
                scooter.setCurrentCharge(newCharge);
                scooter.setStatus("CHARGING");

                boolean success = scooterDao.updateScooter(scooter);

                if (success && newCharge == 100) {
                    scooter.setStatus("AVAILABLE");
                    scooterDao.updateScooter(scooter);
                }

                return success;
            }
            return false;
        } catch (Exception e) {
            System.err.println("error charging scooter: " + e.getMessage());
            return false;
        }
    }
}
