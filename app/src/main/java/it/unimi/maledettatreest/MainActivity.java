package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationBarView;

import it.unimi.maledettatreest.controller.MainActivityController;

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
            MainActivityController.setupListeners(item,this));
    }
}