package it.unimi.maledettatreest.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;

import androidx.navigation.Navigation;

import it.unimi.maledettatreest.LinesFragmentDirections;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.UserFragmentDirections;

public class MainActivityController {

    private final static String TAG = MainActivity.TAG_BASE + "MainActivityController";

    @SuppressLint("NonConstantResourceId")
    public static boolean setupListeners(MenuItem item, Activity activity){
        switch(item.getItemId()){
            case R.id.linesBottomNav:
                Log.d(TAG,"Navigating to Lines");
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(UserFragmentDirections.actionUserFragmentToLinesFragment());
                return true;
            case R.id.userBottomNav:
                Log.d(TAG,"Navigating to User");
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(LinesFragmentDirections.actionLinesFragmentToUserFragment());
                return true;
            default:
                Log.d(TAG,"Unknown menu item " + item);
                return false;
        }
    }

}
