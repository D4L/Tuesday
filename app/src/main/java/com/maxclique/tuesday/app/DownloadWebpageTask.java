package com.maxclique.tuesday.app;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Austin on 6/16/2014.
 */
public class DownloadWebpageTask extends AsyncTask<Void, Void, String> {

    String url;
    DownloadWebpageTaskCallback callback;

    public DownloadWebpageTask(String url, DownloadWebpageTaskCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return downloadUrl();
        } catch (IOException e) {
            return Resources.getSystem().getString(R.string.url_error);
        } catch (UnfoundResourceException e) {
            return "";
        }
    }

    private String downloadUrl() throws IOException, UnfoundResourceException {
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Tuesday", "The response is: " + response);
            if (response == 404) {
                throw new UnfoundResourceException();
            }
            // TODO(austin) handle cases when content length == -1
            int responseLength = conn.getContentLength();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }

            return result;
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

    public interface DownloadWebpageTaskCallback {
        public void run(String resultOfTask);
    }

    public class UnfoundResourceException extends Exception {
    }
}
