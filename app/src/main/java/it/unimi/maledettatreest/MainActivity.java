package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public static final String DOESNT_EXIST = "-1";
    public static final String ERRORE = "ERRORE";
    public static final String OK = "Ok";
    public static final String TAG_BASE = "MALEDETTATREEST_";
    public static final String APP_PREFS = "prefs";
    private final String TAG = TAG_BASE + "MainActivity";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate");

        ((NavigationBarView)findViewById(R.id.bottom_navigation)).setOnItemSelectedListener(item ->
        {
            switch (item.getItemId()) {
                case R.id.linesBottomNav:
                    Log.d(TAG, "Navigating to Lines");
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(UserFragmentDirections.actionUserFragmentToLinesFragment());
                    return true;
                case R.id.userBottomNav:
                    Log.d(TAG, "Navigating to User");
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(LinesFragmentDirections.actionLinesFragmentToUserFragment());
                    return true;
                default:
                    Log.d(TAG, "Unknown menu item " + item);
                    return false;
            }
        });
    }
}