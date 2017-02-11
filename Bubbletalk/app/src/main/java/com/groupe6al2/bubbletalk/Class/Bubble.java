package com.groupe6al2.bubbletalk.Class;

/**
 * Created by Loïc on 10/02/2017.
 */

public class Bubble {

    private String id;
    private String proprio;
    private String avatarMd5;
    private String name;
    private String latitude;
    private String longitude;
    private String active;
    private String description;

    public Bubble(String id, String name, String description, String proprio, String avatarMd5, String latitude, String longitude, String active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.proprio = proprio;
        this.avatarMd5 = avatarMd5;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
    }

    public Bubble(String id, String name, String description, String proprio, String avatarMd5) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.proprio = proprio;
        this.avatarMd5 = avatarMd5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarMd5() {
        return avatarMd5;
    }

    public void setAvatarMd5(String avatarMd5) {
        this.avatarMd5 = avatarMd5;
    }

    public String getProprio() {
        return proprio;
    }

    public void setProprio(String proprio) {
        this.proprio = proprio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getActive() { return active; }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "id='" + id + '\'' +
                ", proprio=" + proprio +
                ", avatarMd5='" + avatarMd5 + '\'' +
                ", name='" + name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", active='" + active + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
