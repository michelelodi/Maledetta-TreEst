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
import it.unimi.maledettatreest.PostViewHolder;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.User;

public class CommunicationController {
    private static final String URL_BASE = "https://ewserver.di.unimi.it/mobicomp/treest/";
    private static final String TAG = MainActivity.TAG_BASE + "CommunicationController";

    private static CommunicationController instance;
    private final RequestQueue requestQueue;

    private CommunicationController(Context c) {
        this.requestQueue = Volley.newRequestQueue(c);
    }

    public static synchronized CommunicationController getInstance(Context c) {
        if(instance == null) instance = new CommunicationController(c);
        return instance;
    }

    public void addPost(String sid, String did, @Nullable String delay, @Nullable String status,
                        @Nullable String comment, Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        try {
            baseRequest("addPost", new JSONObject().put(User.SID, sid).put(Line.DID, did)
                    .put(PostViewHolder.POST_DELAY, delay)
                    .put(PostViewHolder.POST_STATUS, status)
                    .put(PostViewHolder.POST_COMMENT, comment), rL, eL);
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void baseRequest(String url, JSONObject body, Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        Log.d(TAG,"Handling " + url + " Request");

        String requestUrl = URL_BASE + url + ".php";
        requestQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl, body, rL, eL));
    }

    public void follow(String sid, String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("follow", new JSONObject().put(User.SID,sid).put(User.UID,uid),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getLines(String sid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getLines", new JSONObject().put(User.SID,sid),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getPosts(String sid, String did, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getPosts", new JSONObject().put(User.SID,sid).put(Line.DID,did),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getProfile(String sid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getProfile", new JSONObject().put(User.SID,sid),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getStations(String sid, String did, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getStations", new JSONObject().put(User.SID,sid).put(Line.DID,did),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getUserPicture(String sid, String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getUserPicture", new JSONObject().put(User.SID,sid).put(User.UID,uid),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void handleVolleyError(VolleyError e, Context c, String t) {
        Log.d(TAG,"Handling Volley Error");

        String message = "Unexpected error: please retry.";

        switch(e.networkResponse.statusCode){
            case 400:
                Log.d(TAG,"Invalid parameters");
            case 401:
                Log.d(TAG,"Invalid sid");
            case 413:
                Log.d(TAG,"Invalid data length");
            default:
                if (e instanceof NetworkError) message = "Cannot connect to Internet...Please check your connection!";
                else if (e instanceof ServerError) message = "The server could not be found. Please try again after some time!!";
                else if (e instanceof ParseError) message = "Parsing error! Please try again after some time!!";
                else if (e instanceof TimeoutError) message = "Connection TimeOut! Please check your internet connection.";
        }
        Log.e(t, e.toString());
        new AlertDialog.Builder(c).setMessage(message).setTitle(MainActivity.ERROR)
                                .setPositiveButton("Ok", (dialog, id) -> {}).create().show();
    }

    public void register(Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        baseRequest("register", new JSONObject(), rL, eL);
    }

    public void setProfile(String sid, @Nullable String name, @Nullable String picture,
                           Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        try {
            baseRequest("setProfile", new JSONObject().put(User.SID, sid)
                    .put(User.NAME, name).put(User.PICTURE, picture), rL, eL);
        } catch (JSONException e) {e.printStackTrace();}
    }

    public void unfollow(String sid, String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("unfollow", new JSONObject().put(User.SID,sid).put(User.UID,uid),rL,eL);
        } catch (JSONException e) { e.printStackTrace(); }
    }
}
