package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.User;

public class LinesFragmentController {

    private final String RIPROVA = "RIPROVA";
    private final String TAG = MainActivity.TAG_BASE + "LinesFragmentController";
    private final String UNEXPECTED_ERROR_MESSAGE = "Qualcosa è andato storto. Clicca su RIPROVA per rieseguire il setup";
    private final String UNEXPECTED_ERROR_TITLE = "OPS";

    private CommunicationController cc;
    private Context context;
    private final SharedPreferences prefs;
    private LinesModel linesModel;
    private LinesAdapter adapter;

    public LinesFragmentController(Fragment owner, LinesAdapter adapter){
        context = owner.requireContext();
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        linesModel = LinesModel.getInstance();
        this.adapter = adapter;
    }

    public void applicationSetUp() {
        Log.d(TAG,"Check first run");

        cc = CommunicationController.getInstance(context);

        if(prefs.getString(User.SID,MainActivity.DOESNT_EXIST).equals(MainActivity.DOESNT_EXIST)){
            Log.d(TAG,"First Run: Setting up application");
            cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG));
        }else{
            Log.d(TAG,"Normal Run");
            getLines();
        }
    }

    public void getLines(){
        cc.getLines(prefs.getString(User.SID,MainActivity.DOESNT_EXIST),this::handleGetLinesResponse,
                error -> cc.handleVolleyError(error,context,TAG));
    }

    private void handleGetLinesResponse(JSONObject response) {
        Log.d(TAG,"Handling getLines Response");

        try {
            for(int i = 0; i < response.getJSONArray("lines").length(); i++) {
                linesModel.addLine(new Line(((JSONObject) response.getJSONArray("lines").get(i))));
                adapter.notifyItemChanged(i);
            }
        } catch (JSONException e) { e.printStackTrace(); }
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
                            (dialog, id) -> cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG)))
                    .create().show();
        }
        getLines();
    }
}