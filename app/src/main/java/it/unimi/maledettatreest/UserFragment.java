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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

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
    private TextInputLayout profileNameWrapper;
    private Button editName, editPicture;
    private String updatedPicture = null;
    private String updatedName = null;
    private boolean nameUpdated = false;

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
        profileNameWrapper = view.findViewById(R.id.outlinedTextField);

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

    private void handleSetProfileResponse(JSONObject jsonObject) {
        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG).show();
        SessionUser sessionUser = um.getSessionUser();
        if (updatedName != null) {
            sessionUser.setName(updatedName);
            updatedName = null;
        }
        if(updatedPicture != null){
            sessionUser.setPicture(updatedPicture);
            sessionUser.setPversion(String.valueOf(Integer.parseInt(um.getSessionUser().getPversion())+1));
        }
        um.setSessionUser(sessionUser);
        nameUpdated = false;
    }

    @Override
    public void onActivityResult(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            updatedPicture = ImageManager.bitmapToBase64(bitmap);
            km.hideKeyboard();
            if(updatedPicture.length()>137000) {
                Toast.makeText(context, "Selected image is too large", Toast.LENGTH_LONG).show();
                updatedPicture = null;
            }
            else{
                profilePicture.setImageBitmap(ImageManager.getRoundedCornerBitmap(bitmap,8));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void setupView(){

        String picture = um.getSessionUser().getPicture();

        profileNameWrapper.setHint(um.getSessionUser().getName());

        profileName.setOnFocusChangeListener((view, b) -> {
            if(b) {
                profileNameWrapper.setHint(R.string.profile_name_placeholder);
            }
            else {
               if(updatedName == null) profileNameWrapper.setHint(um.getSessionUser().getName());
               else profileNameWrapper.setHint(updatedName);
            }
        });

        Bitmap bitmap = ImageManager.base64ToBitmap(picture);

        if(picture == null || bitmap == null)
            profilePicture.setImageResource(R.drawable.blank_profile_picture);
        else
            profilePicture.setImageBitmap(ImageManager.getRoundedCornerBitmap(bitmap, 8) );

        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                nameUpdated = true;
            }
        });

        editName.setOnClickListener(v -> {
            km.hideKeyboard();
            if(nameUpdated) {
                String name = profileName.getText().toString();
                profileName.setText(null);
                if (name.isEmpty())
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_LONG).show();
                else if (name.length() > 20)
                    Toast.makeText(context, "Name cannot be 20 character longer", Toast.LENGTH_LONG).show();
                else
                    updatedName = name;
            }
            profileName.clearFocus();
            if(updatedPicture != null || updatedName != null)
                cc.setProfile(updatedName, updatedPicture, this::handleSetProfileResponse,
                    error -> cc.handleVolleyError(error, context, TAG));
            else
                Toast.makeText(context, "No changes to save", Toast.LENGTH_LONG).show();
        });

        editPicture.setOnClickListener(v -> {
            km.hideKeyboard();
            getProfilePicture.launch("image/*");
        });
    }
}