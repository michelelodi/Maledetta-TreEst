package it.unimi.maledettatreest;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.EditLineDirectionClickListener;
import it.unimi.maledettatreest.controller.PostsAdapter;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.User;

public class BoardFragment extends Fragment {
    private static final String TAG = MainActivity.TAG_BASE + "BoardFragment";

    private CommunicationController cc;
    private RecyclerView listView;
    private Context context;
    private SharedPreferences prefs;
    View view;

    public BoardFragment() {}

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        context = requireContext();
        cc = CommunicationController.getInstance(context);
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        listView = view.findViewById(R.id.postsRecyclerView);
        String sid = prefs.getString(User.SID, MainActivity.DOESNT_EXIST);

        listView.setLayoutManager(new LinearLayoutManager(context));

        ((TextView)view.findViewById(R.id.lnameBoardFragmentTV)).setText(prefs.getString(Line.LNAME, MainActivity.DOESNT_EXIST));
        ((TextView)view.findViewById(R.id.snameBoardFragmentTV)).setText(prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST));
        ((TextView)view.findViewById(R.id.didBoardFragmentTV)).setText(prefs.getString(Line.DID, MainActivity.DOESNT_EXIST));

        view.findViewById(R.id.backToHomeB).setOnClickListener(new EditLineDirectionClickListener(requireActivity(), context, null));
        view.findViewById(R.id.revertB).setOnClickListener(v-> {
            String did = prefs.getString(Line.DID, MainActivity.DOESNT_EXIST);
            String sname = prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST);

            prefs.edit().putString(Line.DID,prefs.getString(Line.REVERSE_DID,MainActivity.DOESNT_EXIST)).apply();
            prefs.edit().putString(Line.SNAME,prefs.getString(Line.REVERSE_SNAME,MainActivity.DOESNT_EXIST)).apply();
            prefs.edit().putString(Line.REVERSE_SNAME,sname).apply();
            prefs.edit().putString(Line.REVERSE_DID, did).apply();
            Log.d(TAG,prefs.getString(Line.DID,MainActivity.DOESNT_EXIST));
            cc.getPosts(sid, prefs.getString(Line.DID,MainActivity.DOESNT_EXIST),
                    this::handleGetPostsResponse, error -> cc.handleVolleyError(error, context, TAG));
        });

        cc.getPosts(sid, prefs.getString(Line.DID,MainActivity.DOESNT_EXIST), this::handleGetPostsResponse, error->cc.handleVolleyError(error,context,TAG));
    }

    private void handleGetPostsResponse(JSONObject response){
        Log.d(TAG,"Handling GetPosts Reponse");
        try {
            ((TextView)view.findViewById(R.id.snameBoardFragmentTV)).setText(prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST));
            ((TextView)view.findViewById(R.id.didBoardFragmentTV)).setText(prefs.getString(Line.DID, MainActivity.DOESNT_EXIST));
            String POSTS = "posts";
            listView.swapAdapter(new PostsAdapter(context,response.getJSONArray(POSTS)),true);
        } catch (JSONException e) { e.printStackTrace(); }
    }
}