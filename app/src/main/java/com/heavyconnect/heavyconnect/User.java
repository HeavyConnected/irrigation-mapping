package com.heavyconnect.heavyconnect;

/**
 * Created by andremenezes on 8/4/15.
 */
public class User {
    String name, username;
    String password;

    public User (String name, String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.name = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
