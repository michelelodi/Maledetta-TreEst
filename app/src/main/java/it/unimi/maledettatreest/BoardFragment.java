package it.unimi.maledettatreest;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.controller.PostsAdapter;
import it.unimi.maledettatreest.model.Direction;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.MaledettaTreEstDB;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.model.User;
import it.unimi.maledettatreest.model.UsersModel;

public class BoardFragment extends Fragment {

    private static final String TAG = MainActivity.TAG_BASE + "BoardFragment";

    private CommunicationController cc;
    private RecyclerView listView;
    private Context context;
    private View view;
    private PostsAdapter adapter;
    private LinkedList<Post> posts;
    private Looper secondaryThreadLooper;
    private MaledettaTreEstDB db;
    private UsersModel um;
    private LinesModel lm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        cc = CommunicationController.getInstance(context);
        um = UsersModel.getInstance(context);
        lm = LinesModel.getInstance(context);

        HandlerThread handlerThread = new HandlerThread("BoardHandlerThread");
        handlerThread.start();
        secondaryThreadLooper = handlerThread.getLooper();
        db = MaledettaTreEstDB.getDatabase(context);

        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        listView = view.findViewById(R.id.postsRecyclerView);
        listView.setLayoutManager(new LinearLayoutManager(context));

        cc.getPosts(lm.getSelectedDir().getDid(),
                this::handleGetPostsResponse, error->cc.handleVolleyError(error,context,TAG));

        setupView();
    }

    private void handleGetPostsResponse(JSONObject response){

        HashMap<String, String> missingUids = new HashMap<>();
        HashMap<String, String> foundUids = new HashMap<>();

        try {
            String departure = lm.getSelectedDir().getSname().substring(0,lm.getSelectedDir().getSname().indexOf(" ")) +
                    "\n" + lm.getSelectedDir().getSname().substring(lm.getSelectedDir().getSname().indexOf(" ")+1);
            String arrival = lm.getSelectedDir().getReverseSname().substring(0,lm.getSelectedDir().getReverseSname().indexOf(" ")) +
                    "\n" + lm.getSelectedDir().getReverseSname().substring(lm.getSelectedDir().getReverseSname().indexOf(" ")+1);

            ((TextView)view.findViewById(R.id.terminus1ArrivalBoard))
                    .setText(departure);
            ((TextView)view.findViewById(R.id.terminus1DepartureBoard))
                    .setText(arrival);

            posts = Post.getPostsFromJSONArray(response.getJSONArray("posts"));
            adapter =  new PostsAdapter(context,posts);
            listView.setAdapter(adapter);

            for (Post p : posts)
                if(Integer.parseInt(p.getPversion()) > 0)
                    missingUids.put(p.getAuthor(), p.getPversion());

            for (String uid : new HashMap<>(missingUids).keySet()) {
                User user = um.getUserByUid(uid);
                if(user != null && user.getPversion().equals(missingUids.get(uid))) {
                   foundUids.put(uid,user.getPicture());
                   missingUids.remove(uid);
                }
            }

            for(int i = 0; i < posts.size(); i++)
                if(foundUids.containsKey(posts.get(i).getAuthor())){
                    Post updatedPost = posts.get(i);
                    updatedPost.setPicture(foundUids.get(posts.get(i).getAuthor()));
                    posts.set(i, updatedPost);
                    adapter.notifyItemChanged(i);
                }

            for (String uid : missingUids.keySet())
                new Handler(secondaryThreadLooper).post(() ->
                        MaledettaTreEstDB.databaseWriteExecutor.execute(() -> {
                            String base64Pic = db.userPicturesDao().getPicture(uid);
                            String storedPversion = db.userPicturesDao().getVersion(uid);
                            if (base64Pic != null && storedPversion.equals(missingUids.get(uid))) {
                                um.addUser(new User(uid, storedPversion, base64Pic));
                                requireActivity().runOnUiThread(() -> {
                                    for(int i = 0; i < posts.size(); i++) {
                                        Post updatedPost = posts.get(i);
                                        if(updatedPost.getAuthor().equals(uid)) {
                                            updatedPost.setPicture(base64Pic);
                                            posts.set(i, updatedPost);
                                            adapter.notifyItemChanged(i);
                                        }
                                    }
                                });
                            } else{
                                cc.getUserPicture(uid, this::handleGetUserPictureResponse,
                                                error -> cc.handleVolleyError(error, context, TAG));
                            }
                        }));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    public void handleGetUserPictureResponse(JSONObject response) {
        try {
            for(int i = 0; i < posts.size(); i++){
                Post post = posts.get(i);
                if(post.getAuthor().equals(response.getString(User.UID))){
                    post.setPicture(response.getString(User.PICTURE));
                    posts.set(i,post);
                    adapter.notifyItemChanged(i);
                }
            }

            um.addUser(new User(response));

            new Handler(secondaryThreadLooper).post(() -> db.storePicture(response));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    private void setupView(){

        view.findViewById(R.id.revertB).setOnClickListener(v-> {

            lm.setSelectedDir(new Direction(lm.getSelectedDir().getLname(),
                                            lm.getSelectedDir().getReverseDid(),
                                            lm.getSelectedDir().getReverseSname(),
                                            lm.getSelectedDir().getDid(),
                                            lm.getSelectedDir().getSname() ));

            cc.getPosts(lm.getSelectedDir().getDid(), this::handleGetPostsResponse,
                        error -> cc.handleVolleyError(error, context, TAG));
        });

        view.findViewById(R.id.addPostB).setOnClickListener(v->
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(BoardFragmentDirections.actionBoardFragmentToAddPostFragment()));

        view.findViewById(R.id.detailsB).setOnClickListener(v->
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(BoardFragmentDirections.actionBoardFragmentToMapsFragment()));
    }
}