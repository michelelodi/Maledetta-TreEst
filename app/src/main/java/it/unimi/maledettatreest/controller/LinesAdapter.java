package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.unimi.maledettatreest.LineViewHolder;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.LinesModel;

public class LinesAdapter extends RecyclerView.Adapter<LineViewHolder>{
    private final String TAG = MainActivity.TAG_BASE + "LinesAdapter";

    private final LayoutInflater inflater;

    public LinesAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating LineViewHolder");
        return new LineViewHolder(inflater.inflate(R.layout.fragment_lines_single_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        Log.d(TAG, "Binding Line " + position + " LineViewHolder");
        holder.updateContent(LinesModel.getInstance().get(position));
    }

    @Override
    public int getItemCount() {
        return LinesModel.getInstance().getSize();
    }
}
