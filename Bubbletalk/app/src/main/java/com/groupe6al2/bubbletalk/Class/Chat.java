package com.groupe6al2.bubbletalk.Class;

/**
 * Created by Loïc on 12/02/2017.
 */

public class Chat {

    private String message;
    private String author;

    private Chat() {
    }

    public Chat(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
