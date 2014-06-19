package com.maxclique.tuesday.app;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Austin on 6/19/2014.
 */
public class PostWebpageTask extends AsyncTask<Void, Void, String> {

    String url;
    PostWebpageTaskCallback callback;

    public PostWebpageTask(String url, PostWebpageTaskCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... Params) {
        try {
            return postUrl();
        } catch (IOException e) {
            return Resources.getSystem().getString(R.string.url_error);
        }
    }

    private String postUrl() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("charset", "utf-8");

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
        writer.write("hi");
        writer.flush();
        os.close();

        // start the post
        conn.connect();
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        this.callback.run(result);
    }

    public interface PostWebpageTaskCallback {
        public void run(String resultOfTask);
    }
}
