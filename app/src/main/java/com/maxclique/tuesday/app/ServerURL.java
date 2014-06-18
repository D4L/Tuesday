package com.maxclique.tuesday.app;

/**
 * Created by Austin on 6/16/2014.
 */
public class ServerURL {
    private static final String BASE_URL = "http://maxclique-monday-v2.meteor.com/server/";
    private static final String NO_PIC = "?no_pic=true";

    public static String getAllPosts() {
        return BASE_URL + "posts" + NO_PIC;
    }

    public static String getPost(String id) {
        return BASE_URL + "posts/" + id + NO_PIC;
    }
}
