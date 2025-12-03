package business;

import java.util.List;

import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import data.observers.MaintenanceObserver;
import model.Scooter;

// this service handles scooter-related business logic
public class ScooterService {

    private ScooterDAO scooterDao;
    private MaintenanceDAO maintenanceDao;
    private MaintenanceObserver alertSystem;

    public ScooterService() {
        this.scooterDao = new ScooterDAOImp();
        this.maintenanceDao = new MaintenanceDAOImp();
        this.alertSystem = new MaintenanceObserver();
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
                // observer will be notified on state changes
                scooter.setStatus(scooter.getStatus());
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

            // add observer to each scooter
            for (Scooter scooter : scooters) {
                scooter.addObserver(alertSystem);
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
                scooter.setStatus(scooter.getStatus());
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
    // observer pattern now handles maintenance alerts, so this method is no longer needed

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
