package it.unimi.maledettatreest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unimi.maledettatreest.controller.EditLineDirectionClickListener;
import it.unimi.maledettatreest.model.Line;

public class LineViewHolder extends RecyclerView.ViewHolder {

    private final TextView terminus1Departure, terminus1Arrival, terminus2Departure, terminus2Arrival;
    private final Activity activity;
    private final Context context;
    private final ConstraintLayout terminus1, terminus2;

    public LineViewHolder(Activity activity, Context context, @NonNull View itemView) {
        super(itemView);

        terminus1Departure = itemView.findViewById(R.id.terminus1Departure);
        terminus1Arrival = itemView.findViewById(R.id.terminus1Arrival);
        terminus2Departure = itemView.findViewById(R.id.terminus2Departure);
        terminus2Arrival = itemView.findViewById(R.id.terminus2Arrival);
        terminus1 = itemView.findViewById(R.id.terminus1B);
        terminus2 = itemView.findViewById(R.id.terminus2B);

        this.activity = activity;
        this.context = context;
    }
    public void updateContent(Line line){
        String terminus2Splitted = line.getTerminus(2).getSname().substring(0,line.getTerminus(2).getSname().indexOf(" ")) +
                "\n" + line.getTerminus(2).getSname().substring(line.getTerminus(2).getSname().indexOf(" ")+1);

        String terminus1Splitted = line.getTerminus(1).getSname().substring(0,line.getTerminus(1).getSname().indexOf(" ")) +
                "\n" + line.getTerminus(1).getSname().substring(line.getTerminus(1).getSname().indexOf(" ")+1);

        terminus1Departure.setText(terminus2Splitted);
        terminus1Arrival.setText(terminus1Splitted);

        terminus2Departure.setText(terminus1Splitted);
        terminus2Arrival.setText(terminus2Splitted);

        terminus1.setOnClickListener(new EditLineDirectionClickListener(activity, context, line.getTerminus(1)));
        terminus2.setOnClickListener(new EditLineDirectionClickListener(activity, context, line.getTerminus(2)));
    }
}
