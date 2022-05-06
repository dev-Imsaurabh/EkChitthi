package com.mac.ekchitthi.login;

public class User_Details_Model {
    public String username,phoneNumber,email,uid,image;

    public User_Details_Model() {
    }

    public User_Details_Model(String username, String phoneNumber, String email, String uid, String image) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.uid = uid;
        this.image=image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
