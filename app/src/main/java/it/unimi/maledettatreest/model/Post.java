package it.unimi.maledettatreest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Post {

    public static final String AUTHOR = "author";
    public static final String AUTHOR_NAME = "authorName";
    public static final String COMMENT = "comment";
    public static final String DATETIME = "datetime";
    public static final String DELAY = "delay";
    public static final String FOLLOWING_AUTHOR = "followingAuthor";
    public static final String STATUS = "status";

    private String author, authorName, comment, datetime, delay,
            followingAuthor, status, pversion, picture;

    public Post(JSONObject post){
        picture = null;

        try {
            author = post.getString(AUTHOR);
            authorName = post.getString(AUTHOR_NAME);
            comment = post.has(COMMENT) ? post.getString(COMMENT) : null;
            datetime = post.getString(DATETIME);
            delay = post.has(DELAY) ? post.getString(DELAY) : null;
            followingAuthor = post.getString(FOLLOWING_AUTHOR);
            status = post.has(STATUS) ? post.getString(STATUS) : null;
            pversion = post.getString(User.PVERSION);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public String getAuthor() { return author; }

    public String getAuthorName() { return authorName; }

    public String getComment() { return comment; }

    public String getDatetime() { return datetime; }

    public String getDelay() { return delay; }

    public String getFollowingAuthor() { return followingAuthor; }

    public String getStatus() { return status; }

    public String getPversion() { return pversion; }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public static ArrayList<Post> getPostsFromJSONArray(JSONArray jsonArray){
        ArrayList<Post> posts = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                posts.add(new Post((JSONObject) jsonArray.get(i)));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return posts;
    }
}
