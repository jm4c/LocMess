package pt.ulisboa.tecnico.cmov.locmess.activities.model.types;

import java.io.Serializable;
import java.util.List;


public class Location implements Serializable{
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

    public Location(String name, List<String> ssidList) {
        this.name = name;
        this.ssidList = ssidList;

    }

    public Location() {
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


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public void setLongitude(double longitude) {
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
