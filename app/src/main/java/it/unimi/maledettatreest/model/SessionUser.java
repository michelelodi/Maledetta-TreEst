package it.unimi.maledettatreest.model;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class SessionUser extends User{
    private String sid;
    private String name;

    public SessionUser(String sid, @Nullable String uid, String pversion, @Nullable String picture, @Nullable String name) {
        super(uid, pversion, picture);
        this.sid = sid;
        this.name = name != null ? name : "unnamed";

    }

    public SessionUser(JSONObject user) {
        super(user);
    }

    public String getName(){
        return name;
    }

    public String getSid(){
        return sid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
