package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.util.Log;

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

import org.json.JSONObject;

public class CommunicationController {
    private final String URL_BASE = "https://ewserver.di.unimi.it/mobicomp/treest/";
    private final String TAG = "MALEDETTATREEST_CommunicationController";

    private static CommunicationController instance;
    private final RequestQueue requestQueue;

    private CommunicationController(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized CommunicationController getInstance(Context context) {
        if(instance == null) instance = new CommunicationController(context);
        return instance;
    }

    public void handleVolleyError(VolleyError error, Context context, String tag) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Log.e(tag, error.toString());
        if(message != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setTitle("ERROR")
                    .setNegativeButton("Ok", (dialog, id) -> {})
                    .create()
                    .show();
        }
    }

    public void register(Response.Listener<JSONObject> responseListener,
                         Response.ErrorListener errorListener) {
        final String url = URL_BASE + "register.php";
        requestQueue.add(new JsonObjectRequest(Request.Method.GET,
                                                url,
                                                new JSONObject(),
                                                responseListener,
                                                errorListener));
    }
}
