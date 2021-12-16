package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.User;


public class BoardFragmentController {
    private final String TAG = MainActivity.TAG_BASE + "BoardFragmentController";

    private CommunicationController cc;
    private Context context;
    private String sid, did;

    public BoardFragmentController(Context context, String sid, String did){
        this.context = context;
        this.sid = sid;
        this.did=did;
        cc = CommunicationController.getInstance(context);
    }

    public void getBoardPosts(Response.Listener<JSONObject> rL){
        cc.getPosts(sid, did, rL, error->cc.handleVolleyError(error,context,TAG));
    }
}
