package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimi.maledettatreest.controller.CommunicationController;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_BASE = "MALEDETTATREEST_";

    private final String APP_PREFS = TAG_BASE + "prefs";
    private final int DOESNT_EXIST = -1;
    private final String PREF_VERSION_CODE_KEY = "version";
    private final String SID_PREF_KEY = "sid";
    private final String TAG = TAG_BASE + "MainActivity";
    private final String UID_PREF_KEY = "uid";
    private final String UNEXPECTED_ERROR_MESSAGE = "Something went wrong. Please RETRY to setup the application or EXIT";

    private int currentVersionCode;
    private CommunicationController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRunSetUp();
        Log.d(TAG,getSharedPreferences(APP_PREFS,0).getString(SID_PREF_KEY,UNEXPECTED_ERROR_MESSAGE));
    }

    private void firstRunSetUp() {
        currentVersionCode = BuildConfig.VERSION_CODE;
        int savedVersionCode = getSharedPreferences(APP_PREFS,0)
                                .getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        cc = CommunicationController.getInstance(this);
        if (savedVersionCode == DOESNT_EXIST) {
            Log.d(TAG, "Setting up application");
            cc.register(this::registrationResponse,
                    this::registrationError);
            }
        getSharedPreferences(APP_PREFS,0)
                .edit()
                .putInt(PREF_VERSION_CODE_KEY, currentVersionCode)
                .apply();
    }

    private void registrationError(VolleyError error) {
        cc.handleVolleyError(error,this,TAG);
        currentVersionCode = DOESNT_EXIST;
    }

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");
        try {
            getSharedPreferences(APP_PREFS,0)
                    .edit()
                    .putString(SID_PREF_KEY, response.get("sid").toString())
                    .apply();
            Log.d(TAG, "Successfully set up");
            /*cc.getProfile(response.get("sid").toString(), this::getProfileResponse,
                    error -> cc.handleVolleyError(error,this,TAG));*/
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO dialog retry or exit
        }
    }
}