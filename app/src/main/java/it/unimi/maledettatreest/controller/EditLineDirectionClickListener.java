package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.Navigation;

import java.util.HashMap;
import java.util.Objects;

import it.unimi.maledettatreest.BoardFragmentDirections;
import it.unimi.maledettatreest.LinesFragmentDirections;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.UserFragmentDirections;
import it.unimi.maledettatreest.model.Line;

public class EditLineDirectionClickListener implements View.OnClickListener {
    private final String TAG = MainActivity.TAG_BASE + "EditLineDirectionClickListener";

    private final HashMap<String,String> terminus;
    private Activity activity;

    public EditLineDirectionClickListener(Activity activity, HashMap<String,String> terminus) {
        this.terminus = terminus;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        if(Integer.parseInt(Objects.requireNonNull(terminus.get(Line.DID)))>0) {
            Log.d(TAG, "Opening board for did " + terminus.get(Line.DID));
            Bundle bundle = new Bundle();
            bundle.putString(Line.DID,terminus.get(Line.DID));
            bundle.putString(Line.SNAME,terminus.get(Line.SNAME));
            bundle.putString(Line.LNAME,terminus.get(Line.LNAME));
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_linesFragment_to_boardFragment, bundle);
        }else {
            Log.d(TAG, "Opening lines");
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(BoardFragmentDirections.actionBoardFragmentToLinesFragment());
        }
    }
}
