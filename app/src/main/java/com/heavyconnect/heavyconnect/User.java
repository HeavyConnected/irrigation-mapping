package com.heavyconnect.heavyconnect;

/**
 * Created by andremenezes on 8/4/15.
 */
public class User {
    String name, username;
    int password;

    public User (String name, String username, int password){
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String username, int password){
        this.username = username;
        this.password = password;
        this.name = "";
    }
}
