package com.example.instagram;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

@ParseClassName("Post")
@Parcel(analyze=Post.class)
public class Post extends ParseObject {

    // Keys in DB
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_LIKES = "likes";

    // Getter and setter methods
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getKeyCreatedAt() {
        return getString(KEY_CREATED_AT);
    }

    public String getTimeStamp() {
        return String.valueOf(getCreatedAt());
    }

    public ParseFile getProfilePic() {
        return getUser().getParseFile(KEY_PROFILE_PIC);
    }

    public int getLikes() {
        return getInt(KEY_LIKES);
    }

    public void like() {
        put(KEY_LIKES, getLikes() + 1);
    }
}
