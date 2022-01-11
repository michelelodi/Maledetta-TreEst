package it.unimi.maledettatreest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.services.ImageManager;

public class UserFragment extends Fragment implements ActivityResultCallback<Uri> {

    private static final String TAG = MainActivity.TAG_BASE + "UserFragment";

    private CommunicationController cc;
    private Context context;
    private SharedPreferences prefs;
    private View view;
    private ActivityResultLauncher<String> getProfilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        getProfilePicture = registerForActivityResult(new ActivityResultContracts.GetContent(), this);
        cc = CommunicationController.getInstance(context);
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        if(!prefs.contains(User.NAME))
            cc.getProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST),
                    this::handleGetProfileResponse, error -> cc.handleVolleyError(error, context, TAG));
        else setupView();
    }

    private void handleGetProfileResponse(JSONObject response) {
        try {
            Iterator<String> keys = response.keys();
            while(keys.hasNext()){
                String key = keys.next();
                prefs.edit().putString(key, response.get(key).toString()).apply();
            }
        } catch (JSONException e) { e.printStackTrace(); }

        setupView();
    }

    private void handleSetProfileResponse(JSONObject jsonObject) {
        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            String picture = ImageManager.bitmapToBase64(bitmap);
            if(picture.length()>137000)
                new AlertDialog.Builder(context).setMessage("Selected image is too large")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, (dialog, id) -> {}).create().show();
            else{
                ((ImageView)view.findViewById(R.id.profilePictureIV)).setImageBitmap(bitmap);

                prefs.edit().putString(User.PICTURE,picture).putString(User.PVERSION,
                        String.valueOf(Integer.parseInt(prefs.getString
                                (User.PVERSION,MainActivity.DOESNT_EXIST))+1)).apply();

                cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST), null, picture,
                        this::handleSetProfileResponse, e->cc.handleVolleyError(e,context,TAG));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus()
                                                                .getWindowToken(), 0);
    }

    private void setupView(){
        ImageView profilePicture = view.findViewById(R.id.profilePictureIV);
        EditText profileName = view.findViewById(R.id.profileNameET);
        Button editName = view.findViewById(R.id.editNameB);
        Button editPicture = view.findViewById(R.id.editPictureB);
        String picture = prefs.getString(User.PICTURE, MainActivity.DOESNT_EXIST);

        profileName.setText(prefs.getString(User.NAME, MainActivity.DOESNT_EXIST));

        if(picture.equals("null"))
            profilePicture.setImageResource(R.drawable.blank_profile_picture);
        else
            profilePicture.setImageBitmap(ImageManager.base64ToBitmap(picture));

        editName.setOnClickListener(v -> {
            String name = profileName.getText().toString();

            if(name.isEmpty())
                new AlertDialog.Builder(context).setMessage("Name cannot be empty")
                            .setTitle(MainActivity.ERROR)
                            .setPositiveButton(MainActivity.OK, null).create().show();

            else if(name.length() > 20)
                new AlertDialog.Builder(context).setMessage("Name cannot exceed 20 characters")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, null).create().show();
            else {
                prefs.edit().putString(User.NAME,name).apply();
                hideKeyboard();
                cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST), name, null,
                        this::handleSetProfileResponse, e-> cc.handleVolleyError(e, context, TAG));
            } });

        editPicture.setOnClickListener(v -> {
            hideKeyboard();
            getProfilePicture.launch("image/*");
        });
    }
}