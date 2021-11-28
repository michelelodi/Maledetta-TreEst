package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import it.unimi.maledettatreest.controller.MainActivityController;

public class MainActivity extends AppCompatActivity {
    public static final String DOESNT_EXIST = "-1";
    public static final String TAG_BASE = "MALEDETTATREEST_";
    public static final String APP_PREFS = "prefs";
    private final String TAG = TAG_BASE + "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate");
    }
}