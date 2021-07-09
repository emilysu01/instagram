package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.ProfilePostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    RecyclerView rvPosts;
    ImageView ivProfilePic;
    TextView tvUsername;

    public static final String TAG = "ProfileFragment";

    protected ProfilePostsAdapter adapter;
    protected List<Post> allPosts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        rvPosts = view.findViewById(R.id.rvPosts);

        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
        Log.i(TAG, "username " + ParseUser.getCurrentUser().getParseFile("profilePic"));
        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivProfilePic);
            Log.i(TAG, "url " + profilePic.getUrl());
        } else {
            Glide.with(this)
                    .load(getResources().getDrawable(R.drawable.emptyprofilepic))
                    .circleCrop()
                    .into(ivProfilePic);
        }

        tvUsername.setText("@" + ParseUser.getCurrentUser().getUsername());

        adapter = new ProfilePostsAdapter(getContext(), allPosts);

        rvPosts.setAdapter(adapter);

        rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        queryPosts();
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Get the full details of the user who made the post
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        // getInBackground is used to retrieve a single item from the backend
        // findInBackground is used to retrieve all items from the backend
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
