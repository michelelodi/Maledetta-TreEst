package it.unimi.maledettatreest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.model.UsersModel;
import it.unimi.maledettatreest.services.ImageManager;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = MainActivity.TAG_BASE + "PostViewHolder";
    private static final String UNNAMED = "unnamed";
    private static final String GUEST = MainActivity.TAG_BASE + "@guest";

    private final TextView postAuthorNameTV, postDateTV, postTimeTV, postCommentTV, statusTV, delayTV;
    private final ImageView userPictureIV, delayIV, statusIV;
    private final Button followB;
    private final CommunicationController cc;
    private final Context context;
    private boolean followingAuthor;
    private final UsersModel um;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;
        cc = CommunicationController.getInstance(context);
        um = UsersModel.getInstance(context);

        postAuthorNameTV = itemView.findViewById(R.id.postAuthorNameTV);
        postDateTV = itemView.findViewById(R.id.dateTV);
        postTimeTV = itemView.findViewById(R.id.timeTV);
        postCommentTV = itemView.findViewById(R.id.postCommentTV);
        statusTV = itemView.findViewById(R.id.statusTV);
        statusIV = itemView.findViewById(R.id.statusIV);
        userPictureIV = itemView.findViewById(R.id.userPictureIV);
        followB = itemView.findViewById(R.id.followB);
        delayIV = itemView.findViewById(R.id.delayIV);
        delayTV = itemView.findViewById(R.id.delayTV);
    }

    public void updateContent(Post post){
            followingAuthor = Boolean.parseBoolean(post.getFollowingAuthor());

            if(!post.getAuthorName().equals(UNNAMED)) {
                String name = "@" + post.getAuthorName();
                postAuthorNameTV.setText(name);
            }
            else
                postAuthorNameTV.setText(GUEST);
            if(followingAuthor) postAuthorNameTV.setTextColor(context.getColor(R.color.primary_blue));
            else postAuthorNameTV.setTextColor(context.getColor(R.color.yellow));
            postDateTV.setText(post.getDatetime().substring(0,11));
            postTimeTV.setText(post.getDatetime().substring(12,17));

            postCommentTV.setText(post.getComment());

            if(post.getStatus() != null && Integer.parseInt(post.getStatus()) >= 0 &&
                    Integer.parseInt(post.getStatus()) < 3){

                switch (Integer.parseInt(post.getStatus())) {
                    case 0 -> {
                        statusTV.setText(AddPostFragment.IDEAL);
                        statusIV.setImageResource(R.drawable.ideal);
                    }
                    case 1 -> {
                        statusTV.setText(AddPostFragment.ACCEPTABLE);
                        statusIV.setImageResource(R.drawable.meh);
                    }
                    case 2 -> {
                        statusTV.setText(AddPostFragment.UNACCEPTABLE);
                        statusIV.setImageResource(R.drawable.frown);
                    }
                }
            }

            if(post.getDelay() != null &&
                Integer.parseInt(post.getDelay()) >= 0 &&
                Integer.parseInt(post.getDelay()) < 4) {
                switch (Integer.parseInt(post.getDelay())) {
                    case 0 -> {
                        delayTV.setText(AddPostFragment.NO_DELAY);
                        delayIV.setImageResource(R.drawable.no_delay);
                    }
                    case 1 -> {
                        delayTV.setText(AddPostFragment.SHORT_DELAY);
                        delayIV.setImageResource(R.drawable.short_delay);
                    }
                    case 2 -> {
                        delayTV.setText(AddPostFragment.LONG_DELAY);
                        delayIV.setImageResource(R.drawable.long_delay);
                    }
                    case 3 -> {
                        delayTV.setText(AddPostFragment.CANCELLED);
                        delayIV.setImageResource(R.drawable.cancelled);
                    }
                }
            }

            if(post.getPicture() != null) {
                Bitmap bitmap = ImageManager.base64ToBitmap(post.getPicture());
                if(bitmap != null)
                    userPictureIV.setImageBitmap(ImageManager.getRoundedCornerBitmap(bitmap, 8));
                else userPictureIV.setImageResource(R.drawable.blank_profile_picture);
            }

            else userPictureIV.setImageResource(R.drawable.blank_profile_picture);

            if(!post.getAuthor().equals(um.getSessionUser().getUid())) {
                followB.setVisibility(View.VISIBLE);

                if (followingAuthor) followB.setText(R.string.unfollow);
                else followB.setText(R.string.follow);

                followB.setOnClickListener(view -> {
                    if (followingAuthor) {
                        cc.unfollow(post.getAuthor(),
                                this::handleUnfollowResponse, error -> cc.handleVolleyError(error, context, TAG));
                    } else
                        cc.follow(post.getAuthor(),
                                this::handleFollowResponse, error -> cc.handleVolleyError(error, context, TAG));
                });
            }
            else{
                followB.setVisibility(View.GONE);
            }
    }

    private void handleFollowResponse(JSONObject jsonObject) {
        followB.setText(R.string.unfollow);
        postAuthorNameTV.setTextColor(context.getColor(R.color.primary_blue));
        followingAuthor = !followingAuthor;
    }

    private void handleUnfollowResponse(JSONObject jsonObject) {
        postAuthorNameTV.setTextColor(context.getColor(R.color.yellow));
        followB.setText(R.string.follow);
        followingAuthor = !followingAuthor;
    }
}
