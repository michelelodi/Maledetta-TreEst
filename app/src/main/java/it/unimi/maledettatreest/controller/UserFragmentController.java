package it.unimi.maledettatreest.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.services.ImageManager;

public class UserFragmentController{

    private final String TAG = MainActivity.TAG_BASE + "UserFragmentController";

    private final Context context;
    private final CommunicationController cc;
    private final SharedPreferences prefs;
    private final View view;
    private final Activity activity;
    ActivityResultLauncher<String> getProfilePicture;

    public UserFragmentController(Context c, Activity a, View v,ActivityResultLauncher<String> arl){
        context = c;
        activity = a;
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        cc = CommunicationController.getInstance(context);
        view = v;
        getProfilePicture = arl;
    }

    public void getProfileResponse(JSONObject response) {
        Log.d(TAG,"Handling GetProfile Response");

        try {
            Iterator<String> keys = response.keys();
            while(keys.hasNext()){
                String key = keys.next();
                prefs.edit().putString(key, response.get(key).toString()).apply();
            }} catch (JSONException e) { e.printStackTrace(); }
        setupView();
    }

    public void setupView(){
        Log.d(TAG,"Setting Up View");

        ImageView profilePicture = view.findViewById(R.id.profilePictureIV);
        EditText profileName = view.findViewById(R.id.profileNameET);
        Button editName = view.findViewById(R.id.editNameB);
        Button editPicture = view.findViewById(R.id.editPictureB);
        String name = prefs.getString(User.NAME, MainActivity.DOESNT_EXIST);
        String picture = prefs.getString(User.PICTURE, MainActivity.DOESNT_EXIST);

       profileName.setText(name);

      if(picture.equals("null")) {
            Log.d(TAG, "Using default picture");
            profilePicture.setImageResource(R.drawable.blank_profile_picture);
        } else {
            Log.d(TAG,"Getting profile picture");
            profilePicture.setImageBitmap(ImageManager.base64ToBitmap(picture));
        }

        if(!editName.hasOnClickListeners())
            editName.setOnClickListener(view -> {
                Log.d(TAG,"Editing name");
                String newName = profileName.getText().toString();
                if(newName.isEmpty()){
                    Log.d(TAG,"User entered en empty name");
                    new AlertDialog.Builder(context).setMessage("Name cannot be empty")
                            .setTitle(MainActivity.ERROR).setPositiveButton(MainActivity.OK, (dialog, id) -> {})
                            .create().show();
                }else if(newName.length() > 20){
                    Log.d(TAG,"User entered a name of length " + newName.length());
                    new AlertDialog.Builder(context).setMessage("Name cannot exceed 20 characters")
                            .setTitle(MainActivity.ERROR).setPositiveButton(MainActivity.OK, (dialog, id) -> {})
                            .create().show();
                } else if(newName.equals(name)){
                    Log.d(TAG,"User did not change his name");
                } else {
                    prefs.edit().putString(User.NAME,newName).apply();
                    hideSoftKeyboard(activity);
                    cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST),
                            newName,
                            null,
                            this::handleSetProfileResponse,
                            e-> cc.handleVolleyError(e, context, TAG));
                } });

        if(!editPicture.hasOnClickListeners())
            editPicture.setOnClickListener(view -> {
                Log.d(TAG,"Editing picture");
                hideSoftKeyboard(activity);
                getProfilePicture.launch("image/*");
            });

        Log.d(TAG,"View Set Up Done");
    }

    public void handleSetProfileResponse(JSONObject jsonObject) {
        Log.d(TAG,"Profile successfully updated");
        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG).show();
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            Log.d(TAG,"Hiding keyboard");
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}