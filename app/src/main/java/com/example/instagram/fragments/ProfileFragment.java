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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Post;
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

    // Constants
    public static final String TAG = "ProfileFragment";

    // UI components
    ImageView ivProfilePic;
    TextView tvUsername;
    RecyclerView rvPosts;

    // Adapter and data model
    ProfilePostsAdapter adapter;
    List<Post> allPosts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve UI components
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        rvPosts = view.findViewById(R.id.rvPosts);

        // Set UI components
        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
        // Display the user's profile picture or the empty profile picture
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

        // Set up adapter
        adapter = new ProfilePostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        // Populate profile
        queryPosts();
    }

    protected void queryPosts() {
        // Initialize query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Get all the posts the current user made
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);

        // getInBackground is used to retrieve a single item from the backend
        // findInBackground is used to retrieve all items from the backend
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Add all retrieved posts to data model and notify adapter
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
