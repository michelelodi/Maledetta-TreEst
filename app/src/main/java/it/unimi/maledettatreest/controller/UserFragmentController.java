package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import it.unimi.maledettatreest.BuildConfig;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.User;

public class UserFragmentController {

    private final String PREF_VERSION_CODE_KEY = "version";
    private final String RIPROVA = "RIPROVA";
    private final String TAG = MainActivity.TAG_BASE + "UserFragmentController";
    private final String UNEXPECTED_ERROR_MESSAGE = "Qualcosa è andato storto. Clicca su RIPROVA per rieseguire il setup oppure ESCI";
    private final String UNEXPECTED_ERROR_TITLE = "OPS";

    private int currentVersionCode;
    private Context context;
    private CommunicationController cc;
    private SharedPreferences prefs;
    private ViewSetupManager ufvsm;

    public UserFragmentController(Context c, SharedPreferences sp){
        context = c;
        prefs = sp;
    }

    public void firstRunSetUp(ViewSetupManager vsm) {
        Log.d(TAG,"Check first run");

        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, Integer.parseInt(MainActivity.DOESNT_EXIST));

        ufvsm = vsm;
        currentVersionCode = BuildConfig.VERSION_CODE;
        cc = CommunicationController.getInstance(context);

        if (savedVersionCode == Integer.parseInt(MainActivity.DOESNT_EXIST)) {
            Log.d(TAG, "First Run: Setting up application");
            cc.register(this::registrationResponse,
                    this::registrationError);
        }else{
            Log.d(TAG,"Normal Run");
            ufvsm.setupView();
        }

        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void registrationError(VolleyError error) {
        cc.handleVolleyError(error,context,TAG);
        currentVersionCode = Integer.parseInt(MainActivity.DOESNT_EXIST);
    }

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");
        try {
            prefs.edit().putString(User.SID, response.get(User.SID).toString()).apply();
            Log.d(TAG, "Successfully set up");
            cc.getProfile(response.get("sid").toString(), this::getProfileResponse,
                    error -> cc.handleVolleyError(error,context,TAG));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Missing Sid. The World Is Ending");

            new AlertDialog.Builder(context)
                    .setMessage(UNEXPECTED_ERROR_MESSAGE)
                    .setTitle(UNEXPECTED_ERROR_TITLE)
                    .setPositiveButton(RIPROVA, (dialog, id) -> cc.register(this::registrationResponse,
                                                                                    this::registrationError))
                    .create()
                    .show();
        }
    }

    private void getProfileResponse(JSONObject response) {
        Log.d(TAG,"Handling GetProfile Response");

        Iterator<String> keys = response.keys();
        while(keys.hasNext()){
            String key = keys.next();
            try {
                prefs.edit().putString(key, response.get(key).toString()).apply();
            } catch (JSONException e) { e.printStackTrace(); }
        }
        ufvsm.setupView();
    }

    public void setupView(View v){
        Log.d(TAG,"Setting Up View");

        ((TextView)v.findViewById(R.id.uidTV)).setText(context
                .getSharedPreferences(MainActivity.APP_PREFS,0)
                .getString(User.UID, MainActivity.DOESNT_EXIST));
        ((TextView)v.findViewById(R.id.nameTV)).setText(context
                .getSharedPreferences(MainActivity.APP_PREFS,0)
                .getString(User.NAME, MainActivity.DOESNT_EXIST));
        ((TextView)v.findViewById(R.id.pictureTV)).setText(context
                .getSharedPreferences(MainActivity.APP_PREFS,0)
                .getString(User.PICTURE, MainActivity.DOESNT_EXIST));
        ((TextView)v.findViewById(R.id.pversionTV)).setText(context
                .getSharedPreferences(MainActivity.APP_PREFS,0)
                .getString(User.PVERSION, MainActivity.DOESNT_EXIST));

        Log.d(TAG,"Done");
    }
}
