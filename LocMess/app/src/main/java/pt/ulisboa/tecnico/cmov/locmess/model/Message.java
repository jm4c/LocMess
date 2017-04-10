package pt.ulisboa.tecnico.cmov.locmess.model;

import android.content.Context;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Date;

/**
 * Created by joaod on 02-Apr-17.
 */

public class Message extends ListItem {

    private String owner;

    private Location location;

    private String content;
    private boolean centralized;
    public Message(String owner, Location location, String content, boolean centralized) {
        this.owner = owner;
        this.location = location;
        this.content = content;
        this.centralized = centralized;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isCentralized() {
        return centralized;
    }

    public void setCentralized(boolean centralized) {
        this.centralized = centralized;
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
}
