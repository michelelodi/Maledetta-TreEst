package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import androidx.navigation.Navigation;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Direction;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;

public class EditLineDirectionClickListener implements View.OnClickListener {

    private Direction direction;
    private final Activity activity;
    private final SharedPreferences prefs;
    private Context context;

    public EditLineDirectionClickListener(Activity activity, Context context, Direction direction) {
        this.direction = direction;
        this.activity = activity;
        this.context = context;
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
    }

    @Override
    public void onClick(View view) {
        LinesModel.getInstance(context).setSelectedDir(direction);

            prefs.edit().putString(Direction.DID,direction.getDid())
                    .putString(Direction.SNAME,direction.getSname())
                    .putString(Line.LNAME,direction.getLname())
                    .putString(Direction.REVERSE_DID,direction.getReverseDid())
                    .putString(Direction.REVERSE_SNAME,direction.getReverseSname()).apply();

            Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(R.id.action_linesFragment_to_boardFragment);
        }
}
