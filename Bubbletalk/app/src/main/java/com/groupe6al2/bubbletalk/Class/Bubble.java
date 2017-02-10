package com.groupe6al2.bubbletalk.Class;

/**
 * Created by Loïc on 10/02/2017.
 */

public class Bubble {

    private String id;
    private int proprio;
    private String avatarMd5;
    private String name;

    public Bubble(String id, String name, int proprio, String avatarMd5) {
        this.id = id;
        this.name = name;
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

    public int getProprio() {
        return proprio;
    }

    public void setProprio(int proprio) {
        this.proprio = proprio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "id='" + id + '\'' +
                ", proprio=" + proprio +
                ", avatarMd5='" + avatarMd5 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}