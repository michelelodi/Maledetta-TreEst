package it.unimi.maledettatreest;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class PostViewHolder extends RecyclerView.ViewHolder{

    public static final String POST_AUTHOR = "author";
    public static final String POST_AUTHOR_NAME = "authorName";
    public static final String POST_COMMENT = "comment";
    public static final String POST_DATETIME = "datetime";
    public static final String POST_DELAY = "delay";
    public static final String POST_FOLLOWING_AUTHOR = "followingAuthor";
    public static final String POST_PVERSION = "pversion";
    public static final String POST_STATUS = "status";

    private final TextView postAuthorTV;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postAuthorTV = itemView.findViewById(R.id.postAuthorTV);
    }

    public void updateContent(JSONObject post){
        try {
            postAuthorTV.setText(post.get(POST_AUTHOR).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
