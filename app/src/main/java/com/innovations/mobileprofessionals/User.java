package com.innovations.mobileprofessionals;

public class User {
    private String username;
    private String Email;
    private String Phone;
    private String Password;


    public User() {
    }

    public User(String username, String email, String phone, String password) {
        this.username = username;
        Email = email;
        Phone = phone;
        Password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
