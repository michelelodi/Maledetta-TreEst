package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimi.maledettatreest.controller.CommunicationController;

public class MainActivity extends AppCompatActivity {

    private final String APP_PREFS = "maledetta_treest_prefs";
    private final String PREF_VERSION_CODE_KEY = "version";
    private final int DOESNT_EXIST = -1;
    private final String CURRENT_USER = "current_user";
    private final String TAG = "MALEDETTATREEST_MainActivity";
    private final String SETUP = "Setting up application";
    private final String SETUP_SUCCESSFULL = "Successfully set up";

    private int currentVersionCode;
    private CommunicationController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRunSetUp();
        Log.d(TAG,getSharedPreferences(APP_PREFS,0).getString(CURRENT_USER,"Something went wrong"));
    }

    private void firstRunSetUp() {
        currentVersionCode = BuildConfig.VERSION_CODE;
        int savedVersionCode = getSharedPreferences(APP_PREFS,0)
                                .getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        cc = CommunicationController.getInstance(this);
        if (savedVersionCode == DOESNT_EXIST) {
            Log.d(TAG,SETUP);
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
        try {
            getSharedPreferences(APP_PREFS,0)
                    .edit()
                    .putString(CURRENT_USER, response.get("sid").toString())
                    .apply();
            Log.d(TAG,SETUP_SUCCESSFULL);
            /*cc.getProfile(response.get("sid").toString(), this::getProfileResponse,
                    error -> cc.handleVolleyError(error,this,TAG));*/
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO dialog retry or exit
        }
    }
}