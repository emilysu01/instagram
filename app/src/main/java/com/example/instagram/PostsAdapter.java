package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public static final String TAG = "PostsAdapter";

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePic;
        TextView tvUsername;
        ImageView ivPicture;
        TextView tvDescription;
        TextView tvDate;
        ImageView ivLike;
        TextView tvNumLikes;

        Post post;

        final FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
        }

        // Bind the post data into the view elements
        public void bind(Post post) {
            this.post = post;

            ParseFile profilePic = post.getProfilePic();
            if (profilePic != null) {
                Glide.with(context)
                        .load(profilePic.getUrl())
                        .circleCrop()
                        .into(ivProfilePic);
                Log.i(TAG, "url " + profilePic.getUrl());
            } else {
                Glide.with(context)
                        .load(context.getResources().getDrawable(R.drawable.emptyprofilepic))
                        .circleCrop()
                        .into(ivProfilePic);
            }
            tvUsername.setText("@" + post.getUser().getUsername());

            ParseFile postPic = post.getImage();
            if (postPic != null) {
                Glide.with(context)
                        .load(postPic.getUrl())
                        .into(ivPicture);
            }
            Spanned formattedDescription = Html.fromHtml("<b>" + post.getUser().getUsername() + "</b> " + post.getDescription());
            tvDescription.setText(formattedDescription);
            tvDate.setText(getRelativeTimeAgo(post.getTimeStamp()));
            Glide.with(context)
                    .load(context.getResources().getDrawable(R.drawable.ufi_heart))
                    .into(ivLike);

            formatLikes();
            ivPicture.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick() {
                    Glide.with(context)
                                .load(context.getResources().getDrawable(R.drawable.ufi_heart_active))
                                .into(ivLike);
                    post.like();
                    formatLikes();
                }
            });

            ivPicture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openPostDetail();
                    return false;
                }
            });
            tvDescription.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openPostDetail();
                    return false;
                }
            });
        }

        private void openPostDetail() {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
            context.startActivity(intent);
        }

        public void formatLikes() {
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
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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
