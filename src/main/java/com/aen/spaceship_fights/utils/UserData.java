package com.aen.spaceship_fights.utils;

import java.io.Serializable;

public class UserData implements Serializable {
    private static final long serialVersionUID = 2L;
    private String username;
    public UserData(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}
