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

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public static final String TAG = "PostsAdapter";

    public ProfilePostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPicture;

        Post post;

        final FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPicture = itemView.findViewById(R.id.ivPicture);
        }

        // Bind the post data into the view elements
        public void bind(Post post) {
            this.post = post;

            ParseFile profilePic = post.getImage();
            if (profilePic != null) {
                Glide.with(context)
                        .load(profilePic.getUrl())
                        .into(ivPicture);
            }
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

}
