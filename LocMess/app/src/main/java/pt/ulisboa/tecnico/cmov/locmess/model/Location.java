package pt.ulisboa.tecnico.cmov.locmess.model;

import java.util.List;

/**
 * Created by joaod on 10-Apr-17.
 */

public class Location extends ListItem {
    private String name;
    private double latitude;
    private double longitude;

    private int radius;

    private List<String> ssidList;

    public Location(String name, double latitude, double longitude, int radius) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public Location(String name, List ssidList) {
        this.name = name;
        this.ssidList = ssidList;

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

    public List<String> getSsidList() {
        return ssidList;
    }

    public void setSsidList(List<String> ssidList) {
        this.ssidList = ssidList;
    }
}
