package it.unimi.maledettatreest;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.unimi.maledettatreest.controller.EditLineDirectionClickListener;
import it.unimi.maledettatreest.model.Line;

public class LineViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = MainActivity.TAG_BASE + "LineViewHolder";

    private TextView lineNameTV;
    private Button terminus1B, terminus2B;
    private Activity activity;

    public LineViewHolder(Activity activity, @NonNull View itemView) {
        super(itemView);
        lineNameTV = itemView.findViewById(R.id.lineNameTV);
        terminus1B = itemView.findViewById(R.id.terminus1B);
        terminus2B = itemView.findViewById(R.id.terminus2B);
        this.activity = activity;
    }
    public void updateContent(Line line){
        Log.d(TAG,"Updating content for line " + line.getName());
        lineNameTV.setText(line.getName());
        terminus1B.setText(line.getTerminus(1).get(Line.SNAME));
        terminus2B.setText(line.getTerminus(2).get(Line.SNAME));
        terminus1B.setOnClickListener(new EditLineDirectionClickListener(activity, line.getTerminus(1)));
        terminus2B.setOnClickListener(new EditLineDirectionClickListener(activity, line.getTerminus(2)));
    }
}