package com.consoledoom.models;

public class Player {
    private final int id;
    private final String nickname;

    public Player(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
}
