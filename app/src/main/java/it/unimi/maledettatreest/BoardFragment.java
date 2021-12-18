package it.unimi.maledettatreest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.EditLineDirectionClickListener;
import it.unimi.maledettatreest.controller.PostsAdapter;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.model.UsersModel;

public class BoardFragment extends Fragment {

    private static final String TAG = MainActivity.TAG_BASE + "BoardFragment";

    private CommunicationController cc;
    private RecyclerView listView;
    private Context context;
    private SharedPreferences prefs;
    private String sid;
    private View view;
    private PostsAdapter adapter;
    private ArrayList<Post> posts;

    public BoardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = requireContext();
        cc = CommunicationController.getInstance(context);
        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        sid = prefs.getString(User.SID, MainActivity.DOESNT_EXIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        listView = view.findViewById(R.id.postsRecyclerView);
        listView.setLayoutManager(new LinearLayoutManager(context));
        cc.getPosts(sid, prefs.getString(Line.DID,MainActivity.DOESNT_EXIST),
                this::handleGetPostsResponse, error->cc.handleVolleyError(error,context,TAG));
        setupView();
    }

    private void handleGetPostsResponse(JSONObject response){
        try {
            ((TextView)view.findViewById(R.id.snameBoardFragmentTV))
                    .setText(prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST));
            ((TextView)view.findViewById(R.id.didBoardFragmentTV))
                    .setText(prefs.getString(Line.DID, MainActivity.DOESNT_EXIST));

            posts = Post.getPostsFromJSONArray(response.getJSONArray("posts"));
            HashSet<String> missingUids = new HashSet<>();

            for(Post post : posts)
                if(Integer.parseInt(post.getPversion()) > 0) {
                    User user = UsersModel.getInstance().getUserByUid(post.getAuthor());
                    if(user != null && user.getPversion().equals(post.getPversion()))
                        post.setPicture(user.getPicture());
                    else if(false) ;//TODO check in db
                    else missingUids.add(post.getAuthor());
                }

            adapter =  new PostsAdapter(context,posts);
            listView.swapAdapter(adapter,true);

            for(String uid : missingUids)
                cc.getUserPicture(sid, uid, this::handleGetUserPictureResponse,
                        error -> cc.handleVolleyError(error, context, TAG));

        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void handleGetUserPictureResponse(JSONObject response) {
        for(int i = 0; i < posts.size(); i++){
            try {
                Post post = posts.get(i);
                if(post.getAuthor().equals(response.getString(User.UID))){
                    post.setPicture(response.getString(User.PICTURE));
                    posts.set(i,post);
                    adapter.notifyItemChanged(i);
                }
            } catch (JSONException e) { e.printStackTrace(); }
        }
    }

    private void setupView(){
        ((TextView)view.findViewById(R.id.lnameBoardFragmentTV))
                .setText(prefs.getString(Line.LNAME, MainActivity.DOESNT_EXIST));
        ((TextView)view.findViewById(R.id.snameBoardFragmentTV))
                .setText(prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST));
        ((TextView)view.findViewById(R.id.didBoardFragmentTV))
                .setText(prefs.getString(Line.DID, MainActivity.DOESNT_EXIST));

        view.findViewById(R.id.backToHomeB)
                .setOnClickListener(new EditLineDirectionClickListener(requireActivity(),
                                                                        context, null));

        view.findViewById(R.id.revertB).setOnClickListener(v-> {
            String did = prefs.getString(Line.DID, MainActivity.DOESNT_EXIST);
            String sname = prefs.getString(Line.SNAME, MainActivity.DOESNT_EXIST);

            prefs.edit().putString(Line.DID,prefs.getString(Line.REVERSE_DID,MainActivity.DOESNT_EXIST))
                    .putString(Line.SNAME,prefs.getString(Line.REVERSE_SNAME,MainActivity.DOESNT_EXIST))
                    .putString(Line.REVERSE_SNAME,sname)
                    .putString(Line.REVERSE_DID, did).apply();

            cc.getPosts(sid, prefs.getString(Line.DID,MainActivity.DOESNT_EXIST),
                    this::handleGetPostsResponse, error -> cc.handleVolleyError(error, context, TAG));
        });
    }
}