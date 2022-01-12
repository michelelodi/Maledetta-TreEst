package it.unimi.maledettatreest.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import java.util.ArrayList;
import it.unimi.maledettatreest.MainActivity;

public class UsersModel {

    private static UsersModel instance;
    private final ArrayList<User> users;
    private SessionUser sessionUser;
    private final SharedPreferences prefs;
    private final Looper secondaryThreadLooper;
    private final MaledettaTreEstDB db;

    private UsersModel(Context context) {
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        users = new ArrayList<>();
        String sid = prefs.getString(User.SID, null);
        String uid = prefs.getString(User.UID,null);
        boolean hasPicture = prefs.getBoolean(User.HAS_PICTURE, false);
        String name = prefs.getString(User.NAME,null);
        String pversion = prefs.getString(User.PVERSION,null);

        HandlerThread handlerThread = new HandlerThread("PicturesHandlerThread");
        handlerThread.start();
        secondaryThreadLooper = handlerThread.getLooper();
        db = MaledettaTreEstDB.getDatabase(context);

        sessionUser = new SessionUser(sid, uid, pversion, null, name);
        if(hasPicture)
            new Handler(secondaryThreadLooper).post(() ->
                    MaledettaTreEstDB.databaseWriteExecutor.execute(() ->
                            sessionUser = new SessionUser(sid, uid, pversion,
                                    db.userPicturesDao().getPicture(uid), name)));

    }

    public static synchronized UsersModel getInstance(Context context){
        if(instance == null) instance = new UsersModel(context);
        return instance;
    }

    public User get(int index){
        return users.get(index);
    }

    public void addUser(User user){
        users.add(user);
    }

    public User getUserByUid(String uid) {
        for (User user : users) if(user.getUid().equals(uid)) return user;
        return null;
    }

    public SessionUser getSessionUser(){
        return sessionUser;
    }

    public void setSessionUser(SessionUser sessionUser){
        this.sessionUser = sessionUser;
        if(sessionUser.getSid() != null) prefs.edit().putString(User.SID, sessionUser.getSid()).apply();
        if(sessionUser.getUid() != null) prefs.edit().putString(User.UID, sessionUser.getUid()).apply();
        if(sessionUser.getName() != null) prefs.edit().putString(User.NAME, sessionUser.getName()).apply();
        if(sessionUser.getPicture() != null) new Handler(secondaryThreadLooper).post(() -> {
            db.storePicture(sessionUser.getUid(), sessionUser.getPicture(), sessionUser.getPversion());
            prefs.edit().putBoolean(User.HAS_PICTURE, true).apply();
        });
        prefs.edit().putString(User.PVERSION, sessionUser.getPversion()).apply();
    }
}
