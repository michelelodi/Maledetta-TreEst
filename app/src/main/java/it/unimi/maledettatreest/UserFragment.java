package it.unimi.maledettatreest;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.IOException;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.UserFragmentController;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.services.ImageManager;

public class UserFragment extends Fragment implements ActivityResultCallback<Uri> {

    private final String TAG = MainActivity.TAG_BASE + "UserFragment";

    private UserFragmentController ufc;
    private CommunicationController cc;
    private Context context;
    private SharedPreferences prefs;
    View view;
    ActivityResultLauncher<String> getProfilePicture;

    public UserFragment() {
    }

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        context = requireContext();
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        getProfilePicture = registerForActivityResult(new ActivityResultContracts.GetContent(), this);
        cc = CommunicationController.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ufc = new UserFragmentController(context, requireActivity(),view, getProfilePicture);
        this.view = view;

        if(prefs.getString(User.NAME, MainActivity.DOESNT_EXIST).equals(MainActivity.DOESNT_EXIST)) {
            cc.getProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST),
                    response -> ufc.getProfileResponse(response),
                    error -> cc.handleVolleyError(error, context, TAG));
        }
        else {
            ufc.setupView();
        }
    }

    @Override
    public void onActivityResult(Uri uri) {
        Log.d(TAG,"Extracting Bitmap from URI");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            String picture = ImageManager.bitmapToBase64(bitmap);
            if(picture.length()>137000){
                new AlertDialog.Builder(context).setMessage("Immagine troppo grande")
                        .setTitle(MainActivity.ERROR).setPositiveButton(MainActivity.OK, (dialog, id) -> {})
                        .create().show();
            }else{
                Log.d(TAG,"Setting new image and updating shared preferences");
                ((ImageView)view.findViewById(R.id.profilePictureIV)).setImageBitmap(bitmap);
                prefs.edit().putString(User.PICTURE,picture).putString(User.PVERSION,
                        String.valueOf(Integer.parseInt(prefs.getString
                                (User.PVERSION,MainActivity.DOESNT_EXIST))+1)).apply();
                cc.setProfile(prefs.getString(User.SID, MainActivity.DOESNT_EXIST), null, picture,
                        response->ufc.handleSetProfileResponse(response), e->cc.handleVolleyError(e,context,TAG));
            }} catch (IOException e) { e.printStackTrace(); }
    }
}