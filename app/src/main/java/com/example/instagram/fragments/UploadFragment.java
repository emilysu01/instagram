package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.Post;
import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {

    // Constants
    public static final String TAG = "UploadFragment";
    public static final int RESULT_LOAD_IMAGE = 1;

    // UI components
    EditText etDescription;
    Button btnUploadPic;
    ImageView ivPicture;
    Button btnSubmit;

    // Photo information
    ParseFile photoFile;

    // Required empty public constructor
    public UploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_compose, container, false);
    }

    // Set up all the views
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve UI components
        etDescription = view.findViewById(R.id.etDescription);
        btnUploadPic = view.findViewById(R.id.btnUploadPic);
        ivPicture = view.findViewById(R.id.ivPicture);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Set up progress bar
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        pb.setVisibility(ProgressBar.INVISIBLE);

        // Set up onClickListeners
        btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for errors
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (photoFile == null || ivPicture.getDrawable() == null) {
                    Toast.makeText(getContext(), "Photo can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                // Make post to backend
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });
    }

    private void savePost(String description, ParseUser currentUser, ParseFile photoFile) {
        // Set up new post
        Post post = new Post();
        post.setDescription(description);
        post.setImage(photoFile);
        post.setUser(currentUser);

        // Save new post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "Post save was successful");
                // Reset UI after saving
                etDescription.setText("");
                ivPicture.setImageResource(0);
            }
        });
    }

    // Will be invoked when the child application returns to the parent application (returning from the camera app to Instagram app)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            // Set up file
            Uri photoUri = data.getData();
            String photoPath = photoUri.getPath();
            String realPath = getRealPathFromUri(getContext(),photoUri);
            File localFile = new File(realPath);
            boolean canRead = localFile.canRead();
            String uriToString = photoUri.toString();
            File urllocalFile = new File(uriToString);
            canRead = urllocalFile.canRead();
            Bitmap selectedImage = loadFromUri(photoUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photoFile = new ParseFile(byteArray);

            // Update UI with photo
            ivPicture.setImageBitmap(selectedImage);
        }
    }

    // Provided by CodePath
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Provided by CodePath
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}