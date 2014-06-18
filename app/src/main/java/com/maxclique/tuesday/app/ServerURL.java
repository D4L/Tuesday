package com.maxclique.tuesday.app;

/**
 * Created by Austin on 6/16/2014.
 */
public class ServerURL {
    private static final String BASE_URL = "http://maxclique-monday-v1.meteor.com/server/";

    public static String getAllPosts() {
        return BASE_URL + "posts?no_pic=true";
    }
    public static String getPost(String id) {return BASE_URL + "posts/" + id;}
}
