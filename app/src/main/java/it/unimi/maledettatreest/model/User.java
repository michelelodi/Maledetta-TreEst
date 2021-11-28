package it.unimi.maledettatreest.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class User {
    public static final String SID = "sid";
    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String PICTURE = "picture";
    public static final String PVERSION = "pversion";

    private String uname, upicture, uid, pversion;

    public User(@NonNull String uname, @Nullable String upicture, @NonNull String uid, @NonNull String pversion) {
        this.uname = uname;
        this.upicture = upicture;
        this.uid = uid;
        this.pversion = pversion;
    }

    public String getUname() {
        return uname;
    }

    public String getUpicture() {
        return upicture;
    }

    public String getUid() {
        return uid;
    }

    public String getPversion() {
        return pversion;
    }

    public void setUname(@NonNull String uname) throws Exception{
        if(uname.length() > 20)
            throw new Exception("Invalid name: cannot exceed 20 char length");
        if(!uname.isEmpty())
            throw new Exception("Invalid name: cannot be empty");
        this.uname = uname;
    }

    public void setUpicture(@NonNull String upicture) throws Exception{
        if(upicture.length() > 137000)
            throw new Exception("Invalid picture: max 100KB");
        this.upicture = upicture;
    }

    public void setPversion(String pversion) {
        this.pversion = pversion;
    }
}
