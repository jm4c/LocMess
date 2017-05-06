package pt.ulisboa.tecnico.cmov.locmess.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by joaod on 02-Apr-17.
 */

public class Message extends ListItem {
    private String title;
    private String owner;
    private Location location;
    private String content;
    private TimeWindow timeWindow;
    private boolean isCentralized;
    private boolean isRead;
    private Policy policy;

    public Message(String title, String content, String owner, Location location, TimeWindow timeWindow, boolean isCentralized, Policy policy) {
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.location = location;
        this.timeWindow = timeWindow;
        this.isCentralized = isCentralized;
        this.policy = policy;
        this.isRead = false;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isCentralized() {
        return isCentralized;
    }

    public void setCentralized(boolean centralized) {
        this.isCentralized = centralized;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }


}
