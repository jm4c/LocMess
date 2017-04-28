package pt.tecnico.ulisboa.cmov.lmserver.types;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


@XmlRootElement(name = "message")
public class Message implements Serializable {
    private int id;
    private String title;
    private String content;
    private String owner;
    private Location location;
    private String startTime;
    private String endTime;
    private HashMap<String, String> policy;
    private boolean isWhitelist;

    public Message(int id, String title, String content, String owner, Location location, String startTime, String endTime, HashMap<String, String> policy, boolean isWhitelist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.policy = policy;
        this.isWhitelist = isWhitelist;
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

    public HashMap<String, String> getPolicy() {
        return policy;
    }

    public void setPolicy(HashMap<String, String> policy) {
        this.policy = policy;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public void setWhitelist(boolean whitelist) {
        isWhitelist = whitelist;
    }
}
