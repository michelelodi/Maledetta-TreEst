package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.unimi.maledettatreest.LineViewHolder;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.LinesModel;

public class LinesAdapter extends RecyclerView.Adapter<LineViewHolder>{

    private final LayoutInflater inflater;
    private final Activity activity;
    private final Context context;

    public LinesAdapter(Activity activity, Context context){
        inflater = LayoutInflater.from(context);
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LineViewHolder(activity, context,
                inflater.inflate(R.layout.fragment_lines_single_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        holder.updateContent(LinesModel.getInstance(context).get(position));
    }

    @Override
    public int getItemCount() {
        return LinesModel.getInstance(context).getSize();
    }
}
