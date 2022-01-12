package it.unimi.maledettatreest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.unimi.maledettatreest.controller.EditLineDirectionClickListener;
import it.unimi.maledettatreest.model.Line;

public class LineViewHolder extends RecyclerView.ViewHolder {

    private final TextView lineNameTV;
    private final Button terminus1B, terminus2B;
    private final Activity activity;
    private final Context context;

    public LineViewHolder(Activity activity, Context context, @NonNull View itemView) {
        super(itemView);

        lineNameTV = itemView.findViewById(R.id.lineNameTV);
        terminus1B = itemView.findViewById(R.id.terminus1B);
        terminus2B = itemView.findViewById(R.id.terminus2B);
        this.activity = activity;
        this.context = context;
    }
    public void updateContent(Line line){
        lineNameTV.setText(line.getName());
        terminus1B.setText(line.getTerminus(1).getSname());
        terminus2B.setText(line.getTerminus(2).getSname());
        terminus1B.setOnClickListener(new EditLineDirectionClickListener(activity, context, line.getTerminus(1)));
        terminus2B.setOnClickListener(new EditLineDirectionClickListener(activity, context, line.getTerminus(2)));
    }
}
