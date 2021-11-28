package it.unimi.maledettatreest.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import it.unimi.maledettatreest.BuildConfig;
import it.unimi.maledettatreest.MainActivity;
import it.unimi.maledettatreest.R;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.services.ImageManager;

public class UserFragmentController implements ActivityResultCallback<Uri>{

    private final String PREF_VERSION_CODE_KEY = "version";
    private final String RIPROVA = "RIPROVA";
    private final String TAG = MainActivity.TAG_BASE + "UserFragmentController";
    private final String UNEXPECTED_ERROR_MESSAGE = "Qualcosa è andato storto. Clicca su RIPROVA per rieseguire il setup oppure ESCI";
    private final String UNEXPECTED_ERROR_TITLE = "OPS";

    private int currentVersionCode;
    private final Context context;
    private CommunicationController cc;
    private final SharedPreferences prefs;
    private ViewSetupManager ufvsm;
    private View view;
    ImageView profilePicture;

    public UserFragmentController(Context c, SharedPreferences sp){
        context = c;
        prefs = sp;
    }

    public void firstRunSetUp(ViewSetupManager vsm) {
        Log.d(TAG,"Check first run");

        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, Integer.parseInt(MainActivity.DOESNT_EXIST));

        ufvsm = vsm;
        currentVersionCode = BuildConfig.VERSION_CODE;
        cc = CommunicationController.getInstance(context);

        if (savedVersionCode == Integer.parseInt(MainActivity.DOESNT_EXIST)) {
            Log.d(TAG, "First Run: Setting up application");
            cc.register(this::registrationResponse,
                    this::registrationError);
        }else{
            Log.d(TAG,"Normal Run");
            ufvsm.setupView();
        }

        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void registrationError(VolleyError error) {
        cc.handleVolleyError(error,context,TAG);
        currentVersionCode = Integer.parseInt(MainActivity.DOESNT_EXIST);
    }

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");
        try {
            prefs.edit().putString(User.SID, response.get(User.SID).toString()).apply();
            Log.d(TAG, "Successfully set up");
            cc.getProfile(response.get("sid").toString(), this::getProfileResponse,
                    error -> cc.handleVolleyError(error,context,TAG));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Missing Sid. The World Is Ending");

            new AlertDialog.Builder(context)
                    .setMessage(UNEXPECTED_ERROR_MESSAGE)
                    .setTitle(UNEXPECTED_ERROR_TITLE)
                    .setPositiveButton(RIPROVA, (dialog, id) -> cc.register(this::registrationResponse,
                                                                                    this::registrationError))
                    .create()
                    .show();
        }
    }

    private void getProfileResponse(JSONObject response) {
        Log.d(TAG,"Handling GetProfile Response");

        Iterator<String> keys = response.keys();
        while(keys.hasNext()){
            String key = keys.next();
            try {
                prefs.edit().putString(key, response.get(key).toString()).apply();
            } catch (JSONException e) { e.printStackTrace(); }
        }
        ufvsm.setupView();
    }

    public void setupView(View v, ActivityResultLauncher<String> getProfilePicture){
        Log.d(TAG,"Setting Up View");

        view = v;

        EditText profileName = view.findViewById(R.id.profileNameET);
        profilePicture = view.findViewById(R.id.profilePictureIV);
        Button editName = view.findViewById(R.id.editNameB);
        Button editPicture = view.findViewById(R.id.editPictureB);

        profileName.setText(prefs.getString(User.NAME, MainActivity.DOESNT_EXIST));

        if(prefs.getString(User.PICTURE, MainActivity.DOESNT_EXIST).equals("null")) {
            Log.d(TAG, "Using default picture");
            profilePicture.setImageResource(R.drawable.blank_profile_picture);
        } else {
            Log.d(TAG,"Getting profile picture");
            profilePicture.setImageBitmap(ImageManager.base64ToBitmap(prefs.getString(User.PICTURE, MainActivity.DOESNT_EXIST)));
        }
        if(!editName.hasOnClickListeners())
            editName.setOnClickListener(view -> {
                Log.d(TAG,"Editing name");
                String newName = profileName.getText().toString();
                if(!newName.equals(prefs.getString(User.NAME, MainActivity.DOESNT_EXIST)) &&
                        newName.length() < 21 && !newName.isEmpty())
                            cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST),
                                        newName,
                                    null,
                                        this::handleSetProfileResponse,
                                        e->cc.handleVolleyError(e,context,TAG));
            });

        if(!editPicture.hasOnClickListeners())
            editPicture.setOnClickListener(view -> {
                Log.d(TAG,"Editing picture");
                getProfilePicture.launch("image/*");
            });

        Log.d(TAG,"Done");
    }

    private void handleSetProfileResponse(JSONObject jsonObject) {
        Log.d(TAG,"Handling SetProfile Response");
        prefs.edit().putString(User.NAME,((EditText)view.findViewById(R.id.profileNameET)).getText().toString()).apply();
    }


    @Override
    public void onActivityResult(Uri uri) {
        try {
            Log.d(TAG,"Extracting Bitmap from URI");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            String picture = ImageManager.bitmapToBase64(bitmap);
            if(picture.length()>137000){
                new AlertDialog.Builder(context)
                        .setMessage("Immagine troppo grande")
                        .setTitle("ERRORE")
                        .setNegativeButton("OK", (dialog, id) -> {})
                        .create()
                        .show();
            }else{
                Log.d(TAG,"Setting new image and updating shared preferences");
                profilePicture.setImageBitmap(bitmap);
                prefs.edit()
                        .putString(User.PICTURE,picture)
                        .putString(User.PVERSION,
                                String.valueOf(Integer.parseInt(prefs.getString
                                        (User.PVERSION,MainActivity.DOESNT_EXIST))+1))
                        .apply();
                cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST),
                        null,
                        picture,
                        this::handleSetProfileResponse,
                        e->cc.handleVolleyError(e,context,TAG));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
