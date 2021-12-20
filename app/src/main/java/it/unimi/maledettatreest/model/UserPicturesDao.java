package it.unimi.maledettatreest.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserPicturesDao {

    @Query("SELECT pversion FROM user_pictures WHERE uid LIKE :uid")
    String getVersion(String uid);

    @Query("SELECT picture FROM user_pictures WHERE uid LIKE :uid")
    String getPicture(String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPicture(UserPictures userPictures);
}
