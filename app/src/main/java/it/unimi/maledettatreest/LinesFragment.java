package it.unimi.maledettatreest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.LinesAdapter;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.User;

public class LinesFragment extends Fragment {

    private final static String RETRY = "RETRY";
    private final static String UNEXPECTED_ERROR_MESSAGE = "Something went wrong. Please RETRY";
    private final static String UNEXPECTED_ERROR_TITLE = "OPS";
    private final String TAG = MainActivity.TAG_BASE + "LinesFragment";
    private Context context;
    private LinesAdapter adapter;
    private CommunicationController cc;
    private SharedPreferences prefs;

    public LinesFragment() {}

    public static LinesFragment newInstance() { return new LinesFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
        adapter = new LinesAdapter(requireActivity(), context);
        cc = CommunicationController.getInstance(context);
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_lines, container, false); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"Check first run");
        super.onViewCreated(view, savedInstanceState);
        if(!prefs.contains(User.SID)){
            Log.d(TAG,"First Run: Setting up application");
            cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG));
        }else {
            Log.d(TAG, "Normal Run");
            if (prefs.contains(Line.DID))
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_linesFragment_to_boardFragment);
            else if (LinesModel.getInstance().getSize() == 0)
                cc.getLines(prefs.getString(User.SID,MainActivity.DOESNT_EXIST), this::handleGetLinesResponse,
                        error -> cc.handleVolleyError(error,context,TAG));
        }
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

    private void registrationResponse(JSONObject response) {
        Log.d(TAG,"Handling Register Response");
        try {
            prefs.edit().putString(User.SID, response.get(User.SID).toString()).apply();
            Log.d(TAG, "Successfully set up");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Missing Sid. Server Fucked Up.");
            new AlertDialog.Builder(context).setMessage(UNEXPECTED_ERROR_MESSAGE)
                    .setTitle(UNEXPECTED_ERROR_TITLE)
                    .setPositiveButton(RETRY,
                            (dialog, id) -> cc.register(this::registrationResponse, error -> cc.handleVolleyError(error,context,TAG)))
                    .create().show();
        }
        cc.getLines(prefs.getString(User.SID,MainActivity.DOESNT_EXIST), this::handleGetLinesResponse,
                error -> cc.handleVolleyError(error,context,TAG));
    }
}