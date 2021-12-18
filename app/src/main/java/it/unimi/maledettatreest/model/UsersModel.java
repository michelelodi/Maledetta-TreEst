package it.unimi.maledettatreest.model;

import java.util.ArrayList;

public class UsersModel {

    private static UsersModel instance;
    private final ArrayList<User> users;

    public UsersModel() {
        users = new ArrayList<>();
    }

    public static synchronized UsersModel getInstance(){
        if(instance == null) instance = new UsersModel();
        return  instance;
    }

    public User get(int index){
        return users.get(index);
    }

    public int getSize(){
        return users.size();
    }

    public void addUser(User user){
        users.add(user);
    }

    public User getUserByUid(String uid) {
        for (User user : users) if(user.getUid().equals(uid)) return user;
        return null;
    }
}
