package it.unimi.maledettatreest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import it.unimi.maledettatreest.services.KeyboardManager;

public class UserFragment extends Fragment implements ActivityResultCallback<Uri> {

    private static final String TAG = MainActivity.TAG_BASE + "UserFragment";

    private CommunicationController cc;
    private Context context;
    private UsersModel um;
    private ActivityResultLauncher<String> getProfilePicture;
    private KeyboardManager km;
    private ImageView profilePicture;
    private EditText profileName;
    private Button editName, editPicture;
    private String picture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        getProfilePicture = registerForActivityResult(new ActivityResultContracts.GetContent(), this);
        cc = CommunicationController.getInstance(context);
        um = UsersModel.getInstance(context);
        km = new KeyboardManager(requireActivity());
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePicture = view.findViewById(R.id.profilePictureIV);
        profileName = view.findViewById(R.id.profileNameET);
        editName = view.findViewById(R.id.editNameB);
        editPicture = view.findViewById(R.id.editPictureB);

        if(um.getSessionUser().getUid() == null)
            cc.getProfile(this::handleGetProfileResponse, error -> cc.handleVolleyError(error, context, TAG));
        else
            setupView();
    }

    private void handleGetProfileResponse(JSONObject response) {
        try {
            SessionUser sessionUser = um.getSessionUser();
            sessionUser.setPversion(response.get(User.PVERSION).toString());
            sessionUser.setUid(response.get(User.UID).toString());
            sessionUser.setPicture(response.get(User.PICTURE).toString());
            sessionUser.setName(response.get(User.NAME).toString());

            um.setSessionUser(sessionUser);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        setupView();
    }

    private void handleSetProfileNameResponse(JSONObject jsonObject) {
        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG).show();

        SessionUser sessionUser = um.getSessionUser();
        sessionUser.setName(profileName.getText().toString());
        um.setSessionUser(sessionUser);
    }

    private void handleSetProfilePictureResponse(JSONObject jsonObject) {
        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG).show();

        SessionUser sessionUser = um.getSessionUser();
        sessionUser.setPicture(picture);
        sessionUser.setPversion(String.valueOf(Integer.parseInt(um.getSessionUser().getPversion())+1));
        um.setSessionUser(sessionUser);
    }

    @Override
    public void onActivityResult(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            picture = ImageManager.bitmapToBase64(bitmap);
            km.hideKeyboard();
            if(picture.length()>137000)
                Toast.makeText(context, "Selected image is too large", Toast.LENGTH_LONG).show();
            else{
                profilePicture.setImageBitmap(bitmap);

                cc.setProfile(null, picture, this::handleSetProfilePictureResponse,
                        error ->cc.handleVolleyError(error, context, TAG));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void setupView(){

        String picture = um.getSessionUser().getPicture();

        profileName.setText(um.getSessionUser().getName());

        if(picture == null)
            profilePicture.setImageResource(R.drawable.blank_profile_picture);
        else
            profilePicture.setImageBitmap(ImageManager.base64ToBitmap(picture));

        editName.setOnClickListener(v -> {
            String name = profileName.getText().toString();
            km.hideKeyboard();

            if(name.isEmpty())
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_LONG).show();

            else if(name.length() > 20)
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_LONG).show();

            else {
                cc.setProfile(name, null, this::handleSetProfileNameResponse,
                                error -> cc.handleVolleyError(error, context, TAG));
            } });

        editPicture.setOnClickListener(v -> {
            km.hideKeyboard();
            getProfilePicture.launch("image/*");
        });
    }
}