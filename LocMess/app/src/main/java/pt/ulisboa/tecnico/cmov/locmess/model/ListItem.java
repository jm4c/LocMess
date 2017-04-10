package pt.ulisboa.tecnico.cmov.locmess.model;

import android.widget.ImageView;

/**
 * Created by joaod on 10-Apr-17.
 */

public abstract class ListItem {
    private String title;
    private String subTitle;
    private ImageView thumbnail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public ImageView getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageView thumbnail) {
        this.thumbnail = thumbnail;
    }
}
