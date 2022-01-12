package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import androidx.navigation.Navigation;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Direction;
import it.unimi.maledettatreest.model.LinesModel;

public class EditLineDirectionClickListener implements View.OnClickListener {

    private final Direction direction;
    private final Activity activity;
    private final Context context;

    public EditLineDirectionClickListener(Activity activity, Context context, Direction direction) {
        this.direction = direction;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        LinesModel.getInstance(context).setSelectedDir(direction);
        Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.action_linesFragment_to_boardFragment);
        }
}
