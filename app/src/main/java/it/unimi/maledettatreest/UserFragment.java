package it.unimi.maledettatreest;

import android.content.Context;
import android.os.Bundle;

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

    public UserFragment() {
    }

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        ufc = new UserFragmentController(requireContext(), requireContext().getSharedPreferences(MainActivity.APP_PREFS,0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ufc.firstRunSetUp((ViewSetupManager) () -> ufc.setupView(view));
    }
}