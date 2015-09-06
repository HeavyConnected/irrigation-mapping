package com.heavyconnect.heavyconnect.entities;

/**
 * Created by andremenezes on 8/4/15.
 */
public class User {
    private String name;
    private String username;
    private String password;
    private String token;

    public User(){

    }

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

    public void setToken(String token) {
        this.token = token;
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

    public String getToken(){
        return token;
    }
}
