package it.unimi.maledettatreest.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_pictures")
public class UserPictures {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "picture")
    private String picture;

    @ColumnInfo(name = "pversion")
    private String pversion;

    public UserPictures(@NonNull String uid, String picture, String pversion) {
        this.uid = uid;
        this.picture = picture;
        this.pversion = pversion;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public String getPicture() {
        return picture;
    }

    public String getPversion() {
        return pversion;
    }
}
