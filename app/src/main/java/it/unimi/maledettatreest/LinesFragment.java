package it.unimi.maledettatreest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimi.maledettatreest.controller.LinesFragmentController;


public class LinesFragment extends Fragment {

    private LinesFragmentController lfc;
    private Context context;
    private SharedPreferences prefs;

    public LinesFragment() {
    }

    public static LinesFragment newInstance() {
        return new LinesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        prefs=context.getSharedPreferences(MainActivity.APP_PREFS,0);
        lfc = new LinesFragmentController(context);

        lfc.firstRunSetUp();

        return inflater.inflate(R.layout.fragment_lines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}