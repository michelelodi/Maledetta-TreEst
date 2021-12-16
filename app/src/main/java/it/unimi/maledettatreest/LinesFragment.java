package it.unimi.maledettatreest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimi.maledettatreest.controller.LinesAdapter;
import it.unimi.maledettatreest.controller.LinesFragmentController;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;

public class LinesFragment extends Fragment {

    private final String TAG = MainActivity.TAG_BASE + "LinesFragment";

    private LinesFragmentController lfc;
    private Context context;
    private LinesAdapter adapter;

    public LinesFragment() {
    }

    public static LinesFragment newInstance() {
        return new LinesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        context = requireContext();
        adapter = new LinesAdapter(requireActivity(), context);
        lfc = new LinesFragmentController(this);
        lfc.applicationSetUp(this::handleGetLinesResponse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_lines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        RecyclerView listView = view.findViewById(R.id.postsRecyclerView);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleGetLinesResponse(JSONObject response) {
        Log.d(TAG,"Handling getLines Response");

        try {
            for(int i = 0; i < response.getJSONArray("lines").length(); i++)
                LinesModel.getInstance().addLine(new Line(((JSONObject) response.getJSONArray("lines").get(i))));
            adapter.notifyDataSetChanged();
        } catch (JSONException e) { e.printStackTrace(); }
    }
}