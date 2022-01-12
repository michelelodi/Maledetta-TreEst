package it.unimi.maledettatreest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import it.unimi.maledettatreest.model.SessionUser;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.model.UsersModel;

public class LinesFragment extends Fragment {

    private final static String RETRY = "RETRY";
    private final static String UNEXPECTED_ERROR_MESSAGE = "Something went wrong. Please RETRY";
    private final static String UNEXPECTED_ERROR_TITLE = "OPS";
    private final static String TAG = MainActivity.TAG_BASE + "LinesFragment";

    private Context context;
    private LinesAdapter adapter;
    private CommunicationController cc;
    private LinesModel lm;
    private UsersModel um;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        adapter = new LinesAdapter(requireActivity(), context);
        cc = CommunicationController.getInstance(context);
        lm = LinesModel.getInstance(context);
        um = UsersModel.getInstance(context);

        return inflater.inflate(R.layout.fragment_lines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(um.getSessionUser().getSid() == null){
            cc.register(this::registrationResponse, error -> cc.handleVolleyError(error, context, TAG));
        }else if (lm.getSize() == 0)
            cc.getLines(this::handleGetLinesResponse, error -> cc.handleVolleyError(error, context, TAG));

        RecyclerView listView = view.findViewById(R.id.postsRecyclerView);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleGetLinesResponse(JSONObject response) {
        try {
            for(int i = 0; i < response.getJSONArray("lines").length(); i++)
                lm.addLine(new Line(((JSONObject) response.getJSONArray("lines").get(i))));

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void registrationResponse(JSONObject response) {
        try {
            um.setSessionUser(new SessionUser(response.get(User.SID).toString(), null,
                                                    "0", null, null));
            cc.getLines(this::handleGetLinesResponse, error -> cc.handleVolleyError(error, context, TAG));
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            new AlertDialog.Builder(context).setMessage(UNEXPECTED_ERROR_MESSAGE)
                .setTitle(UNEXPECTED_ERROR_TITLE).setPositiveButton(RETRY, (dialog, id) ->
                    cc.register(this::registrationResponse, error ->
                                        cc.handleVolleyError(error, context, TAG))).create().show();
        }
    }
}