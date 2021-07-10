package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = "PostDetailActivity";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    // UI components
    ImageView ivProfilePic;
    TextView tvUsername;
    ImageView ivPicture;
    TextView tvDescription;
    TextView tvDate;
    ImageView ivLike;
    TextView tvNumLikes;

    // Current post
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Unwrap current post from intent
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        // Retrieve UI components
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUsername = findViewById(R.id.tvUsername);
        ivPicture = findViewById(R.id.ivPicture);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        ivLike = findViewById(R.id.ivLike);
        tvNumLikes = findViewById(R.id.tvNumLikes);

        // Update UI
        updateUI();
    }

    public void updateUI() {
        ParseFile profilePic = post.getProfilePic();
        // Display the user's profile picture or the empty profile picture
        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivProfilePic);
        } else {
            Glide.with(this)
                    .load(getResources().getDrawable(R.drawable.emptyprofilepic))
                    .circleCrop()
                    .into(ivProfilePic);
        }
        tvUsername.setText("@" + post.getUser().getUsername());
        ParseFile postPic = post.getImage();
        if (postPic != null) {
            Glide.with(this)
                    .load(postPic.getUrl())
                    .into(ivPicture);
        }
        Spanned formattedDescription = Html.fromHtml("<b>" + post.getUser().getUsername() + "</b> " + post.getDescription());
        tvDescription.setText(formattedDescription);
        tvDate.setText(getRelativeTimeAgo(post.getTimeStamp()));
        Glide.with(this)
                .load(getResources().getDrawable(R.drawable.ufi_heart))
                .into(ivLike);
        formatLikes();
        ivPicture.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                Glide.with(getApplicationContext())
                        .load(getResources().getDrawable(R.drawable.ufi_heart_active))
                        .into(ivLike);
                post.like();
                formatLikes();
            }
        });
    }

    public void formatLikes() {
        // Formatting likes (don't display if 0 likes, otherwise display the number of likes)
        Spanned formattedlikes;
        int numLikes = post.getLikes();
        if (numLikes == 0) {
            formattedlikes = Html.fromHtml("");
        } else if (numLikes == 1) {
            formattedlikes = Html.fromHtml("<b>1</b> like");
        } else {
            formattedlikes = Html.fromHtml("<b>" + numLikes + "</b> likes");
        }
        tvNumLikes.setText(formattedlikes);
    }

    // Provided by CodePath
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }
}