package it.unimi.maledettatreest;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.services.ImageManager;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = MainActivity.TAG_BASE + "PostViewHolder";

    private final TextView postAuthorTV, postAuthorNameTV, postDatetimeTV, postPversionTV,
            postFollowingAuthorTV, postCommentTV, postStatusTV, postDelayTV;
    private final ImageView userPictureIV;
    private final LinearLayout singlePostLinearLayout;
    private final Button followB;
    private final CommunicationController cc;
    private final Context context;
    private boolean followingAuthor;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;
        cc = CommunicationController.getInstance(context);

        postAuthorTV = itemView.findViewById(R.id.postAuthorTV);
        postAuthorNameTV = itemView.findViewById(R.id.postAuthorNameTV);
        postDatetimeTV = itemView.findViewById(R.id.postDatetimeTV);
        postPversionTV = itemView.findViewById(R.id.postPversionTV);
        postFollowingAuthorTV = itemView.findViewById(R.id.postFollowingAuthorTV);
        postCommentTV = itemView.findViewById(R.id.postCommentTV);
        postStatusTV = itemView.findViewById(R.id.postStatusTV);
        postDelayTV = itemView.findViewById(R.id.postDelayTV);
        userPictureIV = itemView.findViewById(R.id.userPictureIV);
        followB = itemView.findViewById(R.id.followB);

        singlePostLinearLayout = itemView.findViewById(R.id.singlePostLinearLayout);

    }

    public void updateContent(Post post){
            String author = post.getAuthor();
            String pversion = post.getPversion();
            followingAuthor = Boolean.parseBoolean(post.getFollowingAuthor());

            postAuthorTV.setText(author);
            postAuthorNameTV.setText(post.getAuthorName());
            postDatetimeTV.setText(post.getDatetime());
            postPversionTV.setText(pversion);
            postFollowingAuthorTV.setText(post.getFollowingAuthor());

            if(post.getComment() != null)postCommentTV.setText(post.getComment());
            else singlePostLinearLayout.removeView(postCommentTV);

            if(post.getStatus() != null &&
                Integer.parseInt(post.getStatus()) >= 0 &&
                Integer.parseInt(post.getStatus()) < 3)
                    postStatusTV.setText(post.getStatus());
            else singlePostLinearLayout.removeView(postStatusTV);

            if(post.getDelay() != null &&
                Integer.parseInt(post.getDelay()) >= 0 &&
                Integer.parseInt(post.getDelay()) < 4)
                    postDelayTV.setText(post.getDelay());
            else singlePostLinearLayout.removeView(postDelayTV);

            if(post.getPicture() != null) userPictureIV.setImageBitmap(ImageManager.base64ToBitmap(post.getPicture()));
            else userPictureIV.setImageResource(R.drawable.blank_profile_picture);

            if(followingAuthor) followB.setText(R.string.unfollow);
            else followB.setText(R.string.follow);

            followB.setOnClickListener(view -> {
                if(followingAuthor) {
                    cc.unfollow(post.getAuthor(),
                            this::handleUnfollowResponse, error -> cc.handleVolleyError(error, context, TAG));
                }
                else
                    cc.follow(post.getAuthor(),
                            this::handleFollowResponse, error -> cc.handleVolleyError(error, context, TAG));
            });
    }

    private void handleFollowResponse(JSONObject jsonObject) {
        followB.setText(R.string.unfollow);
        followingAuthor = !followingAuthor;
    }

    private void handleUnfollowResponse(JSONObject jsonObject) {
        followB.setText(R.string.follow);
        followingAuthor = !followingAuthor;
    }
}
