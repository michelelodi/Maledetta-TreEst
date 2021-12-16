package it.unimi.maledettatreest;

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
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.PostsAdapter;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.User;

public class BoardFragment extends Fragment {
    private final String POSTS = "posts";
    private final String TAG = MainActivity.TAG_BASE + "BoardFragment";

    private CommunicationController cc;
    private RecyclerView listView;
    private Context context;

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

        listView = view.findViewById(R.id.postsRecyclerView);
        listView.setLayoutManager(new LinearLayoutManager(context));
        ((TextView)view.findViewById(R.id.lnameBoardFragmentTV)).setText(getArguments().getString(Line.LNAME));
        ((TextView)view.findViewById(R.id.snameBoardFragmentTV)).setText(getArguments().getString(Line.SNAME));
        ((TextView)view.findViewById(R.id.didBoardFragmentTV)).setText(getArguments().getString(Line.DID));
        cc.getPosts(context.getSharedPreferences(MainActivity.APP_PREFS,0).getString(User.SID,MainActivity.DOESNT_EXIST),
                getArguments().getString(Line.DID), this::handleGetPostsResponse, error->cc.handleVolleyError(error,context,TAG));
    }

    private void handleGetPostsResponse(JSONObject response){
        Log.d(TAG,"Handling GetPosts Reponse");
        try {
            listView.setAdapter(new PostsAdapter(context,response.getJSONArray(POSTS)));
        } catch (JSONException e) { e.printStackTrace(); }
    }
}