package it.unimi.maledettatreest.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserPictures.class}, version = 1, exportSchema = false)
public abstract class MaledettaTreEstDB extends RoomDatabase {

    public abstract UserPicturesDao userPicturesDao();

    private static volatile MaledettaTreEstDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MaledettaTreEstDB getDatabase(final Context context) {
        if (INSTANCE == null)
            synchronized (MaledettaTreEstDB.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MaledettaTreEstDB.class, "maledetta_treest_database")
                        .build();
            }
        return INSTANCE;
    }

    public void storePicture(JSONObject getUserPictureResponse){
        databaseWriteExecutor.execute(() -> {
            try {
                INSTANCE.userPicturesDao().insertPicture(new UserPictures(getUserPictureResponse.get("uid").toString(),
                        getUserPictureResponse.get("picture").toString(), getUserPictureResponse.get("pversion").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
