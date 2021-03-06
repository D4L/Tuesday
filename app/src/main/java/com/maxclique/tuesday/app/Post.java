package com.maxclique.tuesday.app;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Austin on 6/29/2014.
 */
public class Post implements Parcelable {

    final String CREATED_AT_FIELD = "created_at";
    final String SUBJECT_FIELD = "subject";
    final String ID_FIELD = "_id";
    final String DETAILS_FIELD = "details";

    private long mCreatedAt;
    private String mSubject;
    private String mId;
    private String mDetails;

    static public Post[] extractFromJson(String jsonString) throws JSONException {
        JSONArray extractJson = new JSONParser<JSONArray>(jsonString,
                new JSONParser.JSONArrayFactory()).getJSON();
        Post[] result = new Post[extractJson.length()];
        for (int i = 0; i < extractJson.length(); ++i) {
            JSONObject temp = extractJson.getJSONObject(i);
            result[i] = new Post(temp);
        }
        sortByCreatedAt(result);
        return result;
    }

    static private void sortByCreatedAt(Post[] posts) {
        Arrays.sort(posts, new CreatedAtComparator());
    }


    public Post(JSONObject object) throws JSONException {
        mCreatedAt = object.getLong(CREATED_AT_FIELD);
        mSubject = object.getString(SUBJECT_FIELD);
        mDetails = object.optString(DETAILS_FIELD, "");
        mId = object.getString(ID_FIELD);
    }

    public Post(Parcel source) {
        mSubject = source.readString();
        mCreatedAt = source.readLong();
        mId = source.readString();
        mDetails = source.readString();
    }

    public long getCreatedAt() {
        return mCreatedAt;
    }

    public String getSubject() {
        return mSubject;
    }

    public String getId() {
        return mId;
    }

    public String getDetails() {
        return mDetails;
    }

    public static class CreatedAtComparator implements Comparator<Post> {
        @Override
        public int compare(Post lhs, Post rhs) {
            return lhs.getCreatedAt() < rhs.getCreatedAt() ? 1 : -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSubject);
        dest.writeLong(mCreatedAt);
        dest.writeString(mId);
        dest.writeString(mDetails);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
