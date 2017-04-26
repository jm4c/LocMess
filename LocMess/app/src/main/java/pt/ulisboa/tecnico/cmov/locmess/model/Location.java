package pt.ulisboa.tecnico.cmov.locmess.model;

/**
 * Created by joaod on 10-Apr-17.
 */

public class Location extends ListItem {
    private String name;
    private double latitude;
    private double longitude;

    private int radius;

    private String ssid;

    public Location(String name, double latitude, double longitude, int radius) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public Location(String name, String ssid) {
        this.name = name;
        this.ssid = ssid;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setCoordinates(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
