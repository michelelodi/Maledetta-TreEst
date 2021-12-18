package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import androidx.navigation.Navigation;
import java.util.HashMap;
import it.unimi.maledettatreest.BoardFragmentDirections;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Line;

public class EditLineDirectionClickListener implements View.OnClickListener {

    private final HashMap<String,String> terminus;
    private final Activity activity;
    private final SharedPreferences prefs;

    public EditLineDirectionClickListener(Activity activity, Context context, HashMap<String,String> terminus) {
        this.terminus = terminus;
        this.activity = activity;
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
    }

    @Override
    public void onClick(View view) {
        if(terminus != null){
            prefs.edit().putString(Line.DID,terminus.get(Line.DID))
                    .putString(Line.SNAME,terminus.get(Line.SNAME))
                    .putString(Line.LNAME,terminus.get(Line.LNAME))
                    .putString(Line.REVERSE_DID,terminus.get(Line.REVERSE_DID))
                    .putString(Line.REVERSE_SNAME,terminus.get(Line.REVERSE_SNAME)).apply();

            Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(R.id.action_linesFragment_to_boardFragment);
        }else {
            prefs.edit().remove(Line.DID)
                    .remove(Line.SNAME)
                    .remove(Line.LNAME)
                    .remove(Line.REVERSE_DID)
                    .remove(Line.REVERSE_SNAME).apply();

            Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(BoardFragmentDirections.actionBoardFragmentToLinesFragment());
        }
    }
}
