package it.unimi.maledettatreest.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SessionUser extends User{
    private String sid;
    private String name;

    public SessionUser(@Nullable String sid, @Nullable String uid, @NonNull String pversion,
                                    @Nullable String picture, @Nullable String name) {
        super(uid, pversion, picture);
        this.sid = sid;
        this.name = name != null ? name : "unnamed";
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
