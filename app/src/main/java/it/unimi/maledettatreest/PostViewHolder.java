package it.unimi.maledettatreest;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class PostViewHolder extends RecyclerView.ViewHolder{
    private TextView postAuthorTV;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postAuthorTV = itemView.findViewById(R.id.postAuthorTV);
    }

    public void updateContent(JSONObject post){
        try {
            postAuthorTV.setText(post.get("author").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
