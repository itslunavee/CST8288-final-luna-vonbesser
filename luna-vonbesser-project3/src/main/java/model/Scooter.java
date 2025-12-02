package model;

// this is the scooter model class - it holds all the data for an e-scooter
public class Scooter {

    private int id;
    private String vehicleNumber;
    private String make;
    private String model;
    private String color;
    private int batteryCapacity;  // in watt-hours
    private int currentCharge;    // percentage, 0-100
    private String status;        // AVAILABLE, IN_USE, CHARGING, MAINTENANCE
    private int sponsorId;        // which user owns/sponsors this scooter
    private Integer stationId;    // which charging station it's at (null if in transit)
    private double totalUsageHours;  // total hours the scooter has been used
    private double tireWearHours;    // how many hours on these tires
    private double brakeWearHours;   // how many hours on these brakes
    private String lastKnownLocation;  // gps coordinates maybe

    // empty constructor - sometimes frameworks need this
    public Scooter() {
        // nothing to do here really
    }

    // builder pattern
    public static class ScooterBuilder {

        private String vehicleNumber;
        private String make;
        private String model;
        private int sponsorId;
        private String color = "Black";      // default color
        private int batteryCapacity = 250;   // default battery size
        private int currentCharge = 100;     // new scooters start fully charged
        private String status = "AVAILABLE"; // default status
        private Integer stationId = null;    // might not be at a station initially
        private double totalUsageHours = 0.0;  // brand new scooter
        private double tireWearHours = 0.0;    // new tires
        private double brakeWearHours = 0.0;   // new brakes
        private String lastKnownLocation = null;  // no location yet

        // constructor with the required fields 
        public ScooterBuilder(String vehicleNumber, String make, String model, int sponsorId) {
            this.vehicleNumber = vehicleNumber;
            this.make = make;
            this.model = model;
            this.sponsorId = sponsorId;
        }

        // all the optional field setters 
        public ScooterBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ScooterBuilder batteryCapacity(int capacity) {
            this.batteryCapacity = capacity;
            return this;
        }

        public ScooterBuilder currentCharge(int charge) {
            this.currentCharge = charge;
            return this;
        }

        public ScooterBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ScooterBuilder stationId(Integer id) {
            this.stationId = id;
            return this;
        }

        public ScooterBuilder totalUsageHours(double hours) {
            this.totalUsageHours = hours;
            return this;
        }

        public ScooterBuilder tireWearHours(double hours) {
            this.tireWearHours = hours;
            return this;
        }

        public ScooterBuilder brakeWearHours(double hours) {
            this.brakeWearHours = hours;
            return this;
        }

        public ScooterBuilder lastKnownLocation(String location) {
            this.lastKnownLocation = location;
            return this;
        }

        // this actually creates the Scooter object when we're done building
        // it takes all the values from the builder and puts them into a new Scooter
        public Scooter build() {
            Scooter scooter = new Scooter();
            scooter.setVehicleNumber(this.vehicleNumber);
            scooter.setMake(this.make);
            scooter.setModel(this.model);
            scooter.setSponsorId(this.sponsorId);
            scooter.setColor(this.color);
            scooter.setBatteryCapacity(this.batteryCapacity);
            scooter.setCurrentCharge(this.currentCharge);
            scooter.setStatus(this.status);
            scooter.setStationId(this.stationId);
            scooter.setTotalUsageHours(this.totalUsageHours);
            scooter.setTireWearHours(this.tireWearHours);
            scooter.setBrakeWearHours(this.brakeWearHours);
            scooter.setLastKnownLocation(this.lastKnownLocation);
            return scooter;
        }

        public Object id(int aInt) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    // --- getters and setters ---
    // these let other parts of the code access and change the scooter's data
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public int getCurrentCharge() {
        return currentCharge;
    }

    public void setCurrentCharge(int currentCharge) {
        this.currentCharge = currentCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(int sponsorId) {
        this.sponsorId = sponsorId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public double getTotalUsageHours() {
        return totalUsageHours;
    }

    public void setTotalUsageHours(double totalUsageHours) {
        this.totalUsageHours = totalUsageHours;
    }

    public double getTireWearHours() {
        return tireWearHours;
    }

    public void setTireWearHours(double tireWearHours) {
        this.tireWearHours = tireWearHours;
    }

    public double getBrakeWearHours() {
        return brakeWearHours;
    }

    public void setBrakeWearHours(double brakeWearHours) {
        this.brakeWearHours = brakeWearHours;
    }

    public String getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(String lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    // some handy methods that use the scooter's data
    // check if the scooter needs maintenance
    public boolean needsMaintenance() {
        // if tires worn out or brakes worn out or battery low
        return tireWearHours > 50 || brakeWearHours > 30 || currentCharge < 20;
    }

    // check if scooter is available for use
    public boolean isAvailable() {
        return "AVAILABLE".equals(status) && currentCharge > 20;
    }

    // get estimated range based on battery charge
    public double getEstimatedRange() {
        // rough estimate: 1km per 10 watt-hours of battery
        return (batteryCapacity * (currentCharge / 100.0)) / 10.0;
    }

    // this helps us print out the scooter info for debugging
    @Override
    public String toString() {
        return "Scooter{"
                + "id=" + id
                + ", vehicleNumber='" + vehicleNumber + '\''
                + ", make='" + make + '\''
                + ", model='" + model + '\''
                + ", color='" + color + '\''
                + ", battery=" + currentCharge + "%"
                + ", status='" + status + '\''
                + ", location=" + (stationId != null ? "Station #" + stationId : lastKnownLocation)
                + '}';
    }

    // example of how to use the builder pattern:
    /*
    Scooter scooter = new Scooter.ScooterBuilder("ESC-123", "Xiaomi", "Mi 3", 5)
        .color("Black")
        .batteryCapacity(300)
        .currentCharge(85)
        .stationId(3)
        .build();
     */
}
