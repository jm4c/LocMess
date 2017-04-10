package pt.ulisboa.tecnico.cmov.locmess.model;

/**
 * Created by joaod on 02-Apr-17.
 */

public class Message extends ListItem {

    private String owner;
    private Location location;
    private String content;
    private boolean isCentralized;
    private boolean isRead;

    public Message(String title, String content, String owner, Location location, boolean isCentralized) {
        setTitle(title);
        this.content = content;
        this.owner = owner;
        this.location = location;
        this.isCentralized = isCentralized;
        this.isRead = false;

        setSubTitle(owner + " — " + content);
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
}
