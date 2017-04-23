package pt.tecnico.ulisboa.cmov.lmserver.types;

import java.io.Serializable;

/**
 * Created by joaod on 02-Apr-17.
 */

public class Message implements Serializable {
    private int id;
    private String title;
    private String content;
    private String owner;
    private Location location;
    private String startTime;
    private String endTime;

    public Message(int id, String title, String content, String owner, Location location, String startTime, String endTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
