package it.unimi.maledettatreest.controller;

import android.util.Log;
import android.view.View;

import it.unimi.maledettatreest.MainActivity;

public class EditLineDirectionClickListener implements View.OnClickListener {
    private final String TAG = MainActivity.TAG_BASE + "EditLineDirectionClickListener";

    private final String did;

    public EditLineDirectionClickListener(String did) {
        this.did = did;
    }

    @Override
    public void onClick(View view) {
        //OpenBoardFragment if did > 0, else OpenLinesFragment
        Log.d(TAG,"Opening board for did " + did);
    }
}
