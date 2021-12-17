package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.User;

public class LinesFragmentController {

    private final static String RETRY = "RETRY";
    private final static String TAG = MainActivity.TAG_BASE + "LinesFragmentController";
    private final static String UNEXPECTED_ERROR_MESSAGE = "Something went wrong. Please RETRY";
    private final static String UNEXPECTED_ERROR_TITLE = "OPS";

    private CommunicationController cc;
    private final Context context;
    private final SharedPreferences prefs;
    private final Activity activity;
    private Response.Listener<JSONObject> rL;

    public LinesFragmentController(Fragment owner, Activity activity){
        context = owner.requireContext();
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        this.activity = activity;
    }

    public void applicationSetUp(Response.Listener<JSONObject> rL) {
        Log.d(TAG,"Check first run");

        this.rL = rL;
        cc = CommunicationController.getInstance(context);

        if(!prefs.contains(User.SID)){
            Log.d(TAG,"First Run: Setting up application");
            cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG));
        }else {
            Log.d(TAG, "Normal Run");
            if (prefs.contains(Line.DID)) {
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_linesFragment_to_boardFragment);
            }
            else if (LinesModel.getInstance().getSize() == 0) {
                getLines();
            }
        }
    }

    public void getLines(){
        cc.getLines(prefs.getString(User.SID,MainActivity.DOESNT_EXIST), rL,
                error -> cc.handleVolleyError(error,context,TAG));
    }

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");
        try {
            prefs.edit().putString(User.SID, response.get(User.SID).toString()).apply();
            Log.d(TAG, "Successfully set up");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Missing Sid. Server Fucked Up.");
            new AlertDialog.Builder(context).setMessage(UNEXPECTED_ERROR_MESSAGE)
                    .setTitle(UNEXPECTED_ERROR_TITLE)
                    .setPositiveButton(RETRY,
                            (dialog, id) -> cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG)))
                    .create().show();
        }
        getLines();
    }
}