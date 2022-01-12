package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.LinkedList;

import it.unimi.maledettatreest.PostViewHolder;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Post;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{

    private final LayoutInflater inflater;
    private final LinkedList<Post> posts;
    private final Context context;

    public PostsAdapter(Context context, LinkedList<Post> posts){
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(inflater.inflate(R.layout.single_post, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.updateContent(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
