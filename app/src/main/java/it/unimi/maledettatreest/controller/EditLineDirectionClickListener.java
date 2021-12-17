package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import androidx.navigation.Navigation;
import java.util.HashMap;
import it.unimi.maledettatreest.BoardFragmentDirections;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Line;

public class EditLineDirectionClickListener implements View.OnClickListener {
    private static final String TAG = MainActivity.TAG_BASE + "EditLineDirectionClickListener";

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
            Log.d(TAG, "Opening board for did " + terminus.get(Line.DID));
            prefs.edit().putString(Line.DID,terminus.get(Line.DID)).apply();
            prefs.edit().putString(Line.SNAME,terminus.get(Line.SNAME)).apply();
            prefs.edit().putString(Line.LNAME,terminus.get(Line.LNAME)).apply();
            prefs.edit().putString(Line.REVERSE_DID,terminus.get(Line.REVERSE_DID)).apply();
            prefs.edit().putString(Line.REVERSE_SNAME,terminus.get(Line.REVERSE_SNAME)).apply();

            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_linesFragment_to_boardFragment);
        }else {
            Log.d(TAG, "Opening lines");
            prefs.edit().remove(Line.DID).apply();
            prefs.edit().remove(Line.SNAME).apply();
            prefs.edit().remove(Line.LNAME).apply();
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(BoardFragmentDirections.actionBoardFragmentToLinesFragment());
        }
    }
}
