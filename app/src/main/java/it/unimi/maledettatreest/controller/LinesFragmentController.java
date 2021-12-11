package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.model.User;

public class LinesFragmentController {

    private final String RIPROVA = "RIPROVA";
    private final String TAG = MainActivity.TAG_BASE + "LinesFragmentController";
    private final String UNEXPECTED_ERROR_MESSAGE = "Qualcosa è andato storto. Clicca su RIPROVA per rieseguire il setup";
    private final String UNEXPECTED_ERROR_TITLE = "OPS";

    private CommunicationController cc;
    private Context context;
    private final SharedPreferences prefs;

    public LinesFragmentController(Context c){
        context =  c;
        prefs = c.getSharedPreferences(MainActivity.APP_PREFS,0);
    }

    public void firstRunSetUp() {
        Log.d(TAG,"Check first run");

        cc = CommunicationController.getInstance(context);
        if(prefs.getString(User.SID,MainActivity.DOESNT_EXIST).equals(MainActivity.DOESNT_EXIST)){
            //TODO first run
            Log.d(TAG,"First Run: Setting up application");
            cc.register(this::registrationResponse, this::registrationError);
        }else{
            //TODO normal run
            Log.d(TAG,"Normal Run");
        }
    }

    private void registrationError(VolleyError error) {
        cc.handleVolleyError(error,context,TAG);
    }

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");

        try {
            prefs.edit().putString(User.SID, response.get(User.SID).toString()).apply();

            Log.d(TAG, "Successfully set up");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Missing Sid. The World Is Ending");

            new AlertDialog.Builder(context).setMessage(UNEXPECTED_ERROR_MESSAGE)
                    .setTitle(UNEXPECTED_ERROR_TITLE)
                    .setPositiveButton(RIPROVA,
                            (dialog, id) -> cc.register(this::registrationResponse, this::registrationError))
                    .create().show();
        }
    }
}