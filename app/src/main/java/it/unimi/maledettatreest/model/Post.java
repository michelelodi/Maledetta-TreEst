package it.unimi.maledettatreest.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;

public class Post {
    public static final String AUTHOR = "author";
    public static final String AUTHORNAME = "authorName";
    public static final String COMMENT = "comment";
    public static final String DATETIME = "dateTime";
    public static final String DELAY = "delay";
    public static final String PID = "pid";
    public static final String STATUS = "status";

    private final String TAG = MainActivity.TAG_BASE + "Post";

    private String authorName, comment, dateTime, author, pid, delay, status;

    public Post(JSONObject post){
        Log.d(TAG,"Building Post from JSON");
        try {
            authorName = post.getString(AUTHORNAME);
            if(post.has(COMMENT)) comment = post.getString(COMMENT);
            dateTime = post.getString(DATETIME);
            author = post.getString(AUTHOR);
            pid = post.getString(PID);
            if(post.has(DELAY)) delay = post.getString(DELAY);
            if(post.has(STATUS)) status = post.getString(STATUS);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public String getAuthorName() { return authorName; }

    public String getComment() { return comment; }

    public String getDateTime() { return dateTime; }

    public String getAuthor() { return author; }

    public String getPid() { return pid; }

    public String getDelay() { return delay; }

    public String getStatus() { return status; }
}
