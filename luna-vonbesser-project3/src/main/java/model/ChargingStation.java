package model;

// this is the charging station model class - it holds all the data for a charging station
public class ChargingStation {
    private int id;
    private String locationName;
    private double latitude;
    private double longitude;
    private int maxCapacity;

    public ChargingStation() {}

    public ChargingStation(int id, String locationName, double latitude, double longitude, int maxCapacity) {
        this.id = id;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxCapacity = maxCapacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    @Override
    public String toString() {
        return "ChargingStation{" +
                "id=" + id +
                ", locationName='" + locationName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
