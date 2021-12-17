package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.PostViewHolder;
import it.unimi.maledettatreest.R;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{

    private final String TAG = MainActivity.TAG_BASE + "PostsAdapter";

    private final LayoutInflater inflater;
    private final JSONArray posts;

    public PostsAdapter(Context context, JSONArray posts){
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating LineViewHolder");
        return new PostViewHolder(inflater.inflate(R.layout.single_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Log.d(TAG, "Binding Line " + position + " LineViewHolder");
        try {
            holder.updateContent((JSONObject) posts.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.length();
    }
}
