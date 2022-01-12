package it.unimi.maledettatreest.model;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import it.unimi.maledettatreest.MainActivity;

public class UsersModel {

    private static UsersModel instance;
    private final ArrayList<User> users;
    private SessionUser sessionUser;
    private final SharedPreferences prefs;

    private UsersModel(Context context) {
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        users = new ArrayList<>();
        String sid = prefs.getString(User.SID, null);
        String uid = prefs.getString(User.UID,null);
        String picture = prefs.getString(User.PICTURE,null);
        String name = prefs.getString(User.NAME,null);
        String pversion = prefs.getString(User.PVERSION,null);

        sessionUser = new SessionUser(sid, uid, pversion, picture, name);
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
        prefs.edit().putString(User.SID, sessionUser.getSid()).apply();
    }
}
