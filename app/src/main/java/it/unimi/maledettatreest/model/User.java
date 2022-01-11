package it.unimi.maledettatreest.model;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

public class User {

    public static final String SID = "sid";
    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String PICTURE = "picture";
    public static final String PVERSION = "pversion";

    private String uid, pversion, picture;

    public User(String uid, String pversion, String picture) {
        this.uid = uid;
        this.pversion = pversion;
        this.picture = picture;
    }

    public User(JSONObject user){
        try {
            uid = user.getString(UID);
            pversion = user.getString(PVERSION);
            picture = user.getString(PICTURE);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public String getUid() {
        return uid;
    }

    public String getPversion() {
        return pversion;
    }

    public String getPicture() {
        return picture;
    }

    public void setUid(@NonNull String uid){
        this.uid = uid;
    }

    public void setPicture(String picture){
        this.picture = picture;
    }

    public void setPversion(String pversion){
        this.pversion = pversion;
    }
}