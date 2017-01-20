package com.groupe6al2.bubbletalk.Class;

/**
 * Created by goasguenl on 20/01/2017.
 */

public class User {

    private String id;
    private String pseudo;
    private boolean usePseudo;
    private String email;
    private String name;
    private String avatar;

    public User(String id, String pseudo, boolean usePseudo, String email, String name, String avatar) {
        this.id = id;
        this.pseudo = pseudo;
        this.usePseudo = usePseudo;
        this.email = email;
        this.name = name;
        this.avatar = avatar;
    }
    public User(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean getUsePseudo() {
        return usePseudo;
    }

    public void setUsePseudo() {
        this.usePseudo = usePseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
