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
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.SessionUser;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.model.UsersModel;
import it.unimi.maledettatreest.services.ImageManager;

public class UserFragment extends Fragment implements ActivityResultCallback<Uri> {

    private static final String TAG = MainActivity.TAG_BASE + "UserFragment";

    private CommunicationController cc;
    private Context context;
    private SharedPreferences prefs;
    private View view;
    private UsersModel um;
    private ActivityResultLauncher<String> getProfilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        getProfilePicture = registerForActivityResult(new ActivityResultContracts.GetContent(), this);
        cc = CommunicationController.getInstance(context);
        um = UsersModel.getInstance(context);
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        if(!prefs.contains(User.NAME))
            cc.getProfile(um.getSessionUser().getSid(), this::handleGetProfileResponse,
                            error -> cc.handleVolleyError(error, context, TAG));
        else {
            SessionUser sessionUser = um.getSessionUser();
            sessionUser.setUid(um.getSessionUser().getUid());
            sessionUser.setName(um.getSessionUser().getName());
            sessionUser.setPicture(um.getSessionUser().getPicture());
            sessionUser.setPversion(um.getSessionUser().getPversion());

            um.setSessionUser(sessionUser);
            setupView();
        }
    }

    private void handleGetProfileResponse(JSONObject response) {
        try {
            prefs.edit().putString(User.UID, response.get(User.UID).toString()).apply();
            prefs.edit().putString(User.NAME, response.get(User.NAME).toString()).apply();
            prefs.edit().putString(User.PICTURE, response.get(User.PICTURE).toString()).apply();
            prefs.edit().putString(User.PVERSION, response.get(User.PVERSION).toString()).apply();

            SessionUser sessionUser = um.getSessionUser();
            sessionUser.setPversion(response.get(User.PVERSION).toString());
            sessionUser.setUid(response.get(User.UID).toString());
            sessionUser.setPicture(response.get(User.PICTURE).toString());
            sessionUser.setName(response.get(User.NAME).toString());

            um.setSessionUser(sessionUser);
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
                        String.valueOf(Integer.parseInt(um.getSessionUser().getPversion())+1)).apply();

                SessionUser sessionUser = um.getSessionUser();
                sessionUser.setPicture(picture);
                um.setSessionUser(sessionUser);

                cc.setProfile(um.getSessionUser().getSid(), null, picture,
                        this::handleSetProfileResponse, e->cc.handleVolleyError(e,context,TAG));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity()
                                                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus()
                                                                .getWindowToken(), 0);
    }

    private void setupView(){
        ImageView profilePicture = view.findViewById(R.id.profilePictureIV);
        EditText profileName = view.findViewById(R.id.profileNameET);
        Button editName = view.findViewById(R.id.editNameB);
        Button editPicture = view.findViewById(R.id.editPictureB);
        String picture = um.getSessionUser().getPicture();

        profileName.setText(um.getSessionUser().getName());

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
                SessionUser sessionUser = um.getSessionUser();
                sessionUser.setName(name);
                um.setSessionUser(sessionUser);
                hideKeyboard();
                cc.setProfile(um.getSessionUser().getSid(), name, null,
                        this::handleSetProfileResponse, e-> cc.handleVolleyError(e, context, TAG));
            } });

        editPicture.setOnClickListener(v -> {
            hideKeyboard();
            getProfilePicture.launch("image/*");
        });
    }
}