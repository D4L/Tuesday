package com.maxclique.tuesday.app;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.utils.URIUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Austin on 6/19/2014.
 */
public class PostWebpageTask extends AsyncTask<HashMap<String, String>, Void, String> {

    String url;
    PostWebpageTaskCallback callback;

    public PostWebpageTask(String url, PostWebpageTaskCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String postParams = getPostParams(params[0]);
        try {
            return postUrl(postParams);
        } catch (IOException e) {
            return Resources.getSystem().getString(R.string.url_error);
        }
    }

    private String getPostParams(HashMap<String, String> object) {
        String result = "";
        Iterator it = object.entrySet().iterator();
        while (true) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            try {
                result += pair.getKey() + "=" + URLEncoder.encode(pair.getValue(), "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            if (it.hasNext()) {
                result += "&";
            } else {
                break;
            }
        }
        return result;
    }

    private String postUrl(String params) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("charset", "utf-8");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
            writer.write(params);
            writer.flush();
            os.close();

            // start the post
            conn.connect();
            Log.d("Tuesday", "The response is " + conn.getResponseCode());

            int responseLength = conn.getContentLength();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            Reader reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[responseLength];
            reader.read(buffer);

            return new String(buffer);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        this.callback.run(result);
    }

    public interface PostWebpageTaskCallback {
        public void run(String resultOfTask);
    }
}
