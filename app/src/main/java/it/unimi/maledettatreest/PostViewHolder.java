package it.unimi.maledettatreest;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.unimi.maledettatreest.model.Post;
import it.unimi.maledettatreest.services.ImageManager;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private final TextView postAuthorTV, postAuthorNameTV, postDatetimeTV, postPversionTV,
            postFollowingAuthorTV, postCommentTV, postStatusTV, postDelayTV;
    private final ImageView userPictureIV;
    private final LinearLayout singlePostLinearLayout;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        postAuthorTV = itemView.findViewById(R.id.postAuthorTV);
        postAuthorNameTV = itemView.findViewById(R.id.postAuthorNameTV);
        postDatetimeTV = itemView.findViewById(R.id.postDatetimeTV);
        postPversionTV = itemView.findViewById(R.id.postPversionTV);
        postFollowingAuthorTV = itemView.findViewById(R.id.postFollowingAuthorTV);
        postCommentTV = itemView.findViewById(R.id.postCommentTV);
        postStatusTV = itemView.findViewById(R.id.postStatusTV);
        postDelayTV = itemView.findViewById(R.id.postDelayTV);
        userPictureIV = itemView.findViewById(R.id.userPictureIV);

        singlePostLinearLayout = itemView.findViewById(R.id.singlePostLinearLayout);

    }

    public void updateContent(Post post){
            String author = post.getAuthor();
            String pversion = post.getPversion();

            postAuthorTV.setText(author);
            postAuthorNameTV.setText(post.getAuthorName());
            postDatetimeTV.setText(post.getDatetime());
            postPversionTV.setText(pversion);
            postFollowingAuthorTV.setText(post.getFollowingAuthor());

            if(post.getComment() != null)postCommentTV.setText(post.getComment());
            else singlePostLinearLayout.removeView(postCommentTV);
            if(post.getStatus() != null)postStatusTV.setText(post.getStatus());
            else singlePostLinearLayout.removeView(postStatusTV);
            if(post.getDelay() != null)postDelayTV.setText(post.getDelay());
            else singlePostLinearLayout.removeView(postDelayTV);

            if(post.getPicture() != null) userPictureIV.setImageBitmap(ImageManager.base64ToBitmap(post.getPicture()));
            else userPictureIV.setImageResource(R.drawable.blank_profile_picture);
    }
}
