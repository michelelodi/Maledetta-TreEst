package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.model.User;

public class CommunicationController {
    private final String URL_BASE = "https://ewserver.di.unimi.it/mobicomp/treest/";
    private final String TAG = MainActivity.TAG_BASE + "CommunicationController";

    private static CommunicationController instance;
    private final RequestQueue requestQueue;

    private CommunicationController(Context c) {
        this.requestQueue = Volley.newRequestQueue(c);
    }

    public static synchronized CommunicationController getInstance(Context c) {
        if(instance == null) instance = new CommunicationController(c);
        return instance;
    }

    public void getLines(String sid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        Log.d(TAG,"Handling GetLines Request");

        String url = URL_BASE + "getLines.php";
        try {
            requestQueue.add(new JsonObjectRequest(Request.Method.POST, url, new JSONObject().put(User.SID,sid), rL, eL));
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getProfile(String sid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        Log.d(TAG,"Handling GetProfile Request");

        String url = URL_BASE + "getProfile.php";
        try {
            requestQueue.add(new JsonObjectRequest(Request.Method.POST, url, new JSONObject().put(User.SID,sid), rL, eL));
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void handleVolleyError(VolleyError e, Context c, String t) {
        Log.d(TAG,"Handling Volley Error");

        String message = null;

        if(e.networkResponse.statusCode  ==  400) {
            Log.d(TAG,"Invalid parameters");
            message = "Errore inaspettato: riprova";
        }
        else if(e.networkResponse.statusCode  ==  401) {
            Log.d(TAG,"Invalid sid");
            message = "Errore inaspettato: riprova";
        }
        else if(e.networkResponse.statusCode  ==  413) {
            Log.d(TAG,"Invalid data length");
            message = "Errore inaspettato: riprova";
        }
        else if (e instanceof NetworkError) message = "Cannot connect to Internet...Please check your connection!";
        else if (e instanceof ServerError) message = "The server could not be found. Please try again after some time!!";
        else if (e instanceof ParseError) message = "Parsing error! Please try again after some time!!";
        else if (e instanceof TimeoutError) message = "Connection TimeOut! Please check your internet connection.";

        Log.e(t, e.toString());

        if(message != null) new AlertDialog.Builder(c).setMessage(message).setTitle("ERRORE")
                                .setPositiveButton("Ok", (dialog, id) -> {}).create().show();
    }

    public void register(Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        Log.d(TAG, "Handling Register Request");

        String url = URL_BASE + "register.php";
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null, rL, eL));
    }

    public void setProfile(String sid, @Nullable String name, @Nullable String picture, Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        Log.d(TAG,"Handling SetProfile Request");

        String url = URL_BASE + "setProfile.php";
        JSONObject body = new JSONObject();

        try {
            body.put(User.SID, sid);
            if (name != null || picture != null) {
                if (name != null) body.put(User.NAME, name);
                if (picture != null) body.put(User.PICTURE, picture);
            }
        } catch (JSONException e) {e.printStackTrace();}
        requestQueue.add(new JsonObjectRequest(Request.Method.POST, url, body, rL, eL));
    }
}
