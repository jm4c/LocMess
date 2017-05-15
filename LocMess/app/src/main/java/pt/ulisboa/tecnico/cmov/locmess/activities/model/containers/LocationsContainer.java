package pt.ulisboa.tecnico.cmov.locmess.activities.model.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.Location;


public class LocationsContainer implements Serializable {
    private String locationsHash;
    private List<Location> locations;

    public LocationsContainer() {
        locations = new ArrayList<>();
        locationsHash = "";
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public boolean addLocation(Location location) {
        return locations.add(location);
    }

    public void setLocationsHash(String hash) {
        locationsHash = hash;
    }

    public String getLocationsHash() {
        return locationsHash;
    }

    public Location getLocation(int pos) {
        return locations.get(pos);
    }

    public List<String> listLocations() {
        List<String> locationNames = new ArrayList<>();
        for (Location location : locations)
            locationNames.add(location.getName());
        return locationNames;
    }

    public Location getLocation(String name) {
        for (Location location : locations)
            if (location.getName().equals(name))
                return location;
        return null;
    }

    public boolean removeLocation(String locationName) {
        return locations.remove(getLocation(locationName));
    }
}
