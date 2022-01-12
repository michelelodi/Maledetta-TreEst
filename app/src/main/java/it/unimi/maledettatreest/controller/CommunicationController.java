package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.model.Direction;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.model.UsersModel;

public class CommunicationController {

    private static final String URL_BASE = "https://ewserver.di.unimi.it/mobicomp/treest/";
    private static final String TAG = MainActivity.TAG_BASE + "CommunicationController";

    private static CommunicationController instance;
    private final RequestQueue requestQueue;
    private final UsersModel um;


    private CommunicationController(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        um = UsersModel.getInstance(context);
    }

    public static synchronized CommunicationController getInstance(Context context) {
        if(instance == null) instance = new CommunicationController(context);
        return instance;
    }

    public void addPost(String did, @Nullable String delay, @Nullable String status,
                        @Nullable String comment, Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        try {
            baseRequest("addPost", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                        .put(Direction.DID, did)
                                                        .put(Post.DELAY, delay)
                                                        .put(Post.STATUS, status)
                                                        .put(Post.COMMENT, comment), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    private void baseRequest(String url, JSONObject body, Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        String requestUrl = URL_BASE + url + ".php";
        requestQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl, body, rL, eL));
    }

    public void follow(String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("follow", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                        .put(User.UID, uid), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void getLines(Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getLines", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                                                            , rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void getPosts(String did, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getPosts", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                        .put(Direction.DID,did), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void getProfile(Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getProfile", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                                                        , rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void getStations(String did, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getStations", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                            .put(Direction.DID,did), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void getUserPicture(String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("getUserPicture", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                                .put(User.UID,uid), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void handleVolleyError(VolleyError e, Context c, String TAG) {
        String message = "A network error occurred. Please check your connection or try again later.";

        if (e instanceof ServerError) message = "The server could not be found. Please try again after some time!!";

        Log.e(TAG, e.toString());
        new AlertDialog.Builder(c).setMessage(message).setTitle(MainActivity.ERROR)
                                .setPositiveButton("Ok", (dialog, id) -> {}).create().show();
    }

    public void register(Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        baseRequest("register", new JSONObject(), rL, eL);
    }

    public void setProfile(@Nullable String name, @Nullable String picture,
                           Response.Listener<JSONObject> rL, Response.ErrorListener eL){
        try {
            baseRequest("setProfile", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                    .put(User.NAME, name).put(User.PICTURE, picture), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void unfollow(String uid, Response.Listener<JSONObject> rL, Response.ErrorListener eL) {
        try {
            baseRequest("unfollow", new JSONObject().put(User.SID, um.getSessionUser().getSid())
                                                        .put(User.UID,uid), rL, eL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }
}
