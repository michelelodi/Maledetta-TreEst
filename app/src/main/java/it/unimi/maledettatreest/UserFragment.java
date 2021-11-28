package it.unimi.maledettatreest;

import android.content.Context;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimi.maledettatreest.controller.UserFragmentController;
import it.unimi.maledettatreest.controller.ViewSetupManager;

public class UserFragment extends Fragment {

    private final String TAG = MainActivity.TAG_BASE + "UserFragment";

    private UserFragmentController ufc;
    private Context c;
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

        c = requireContext();
        ufc = new UserFragmentController(c, c.getSharedPreferences(MainActivity.APP_PREFS,0));
        getProfilePicture = registerForActivityResult(new ActivityResultContracts.GetContent(), ufc);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ufc.firstRunSetUp((ViewSetupManager) () -> ufc.setupView(view, getProfilePicture));
    }
}