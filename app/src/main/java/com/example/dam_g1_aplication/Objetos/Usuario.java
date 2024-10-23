package com.example.dam_g1_aplication.Objetos;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;


public class Usuario implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("mail")
    private String mail;

    @SerializedName("password")
    private String password;

    @SerializedName("profile_photo")
    private String profilePhoto;

    @SerializedName("biography")
    private String biography;

    // Getters y Setters para editar el usuario
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }


}
