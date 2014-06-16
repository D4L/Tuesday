package com.maxclique.tuesday.app;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Austin on 6/15/2014.
 */
public class DisplayPostsFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_posts_fragment, container, false);

        textView = (TextView) resultantView.findViewById(R.id.my_text_view);
        refresh();

        return resultantView;
    }

    public void refresh() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        textView.setText(R.string.fetching);
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute();
        } else {
            // display
            textView.setText(R.string.no_network);
        }
    }

    private class DownloadWebpageTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                return downloadUrl();
            } catch (IOException e) {
                return getString(R.string.url_error);
            } catch (JSONException e) {
                return getString(R.string.json_convert_error);
            }
        }

        private String downloadUrl() throws IOException, JSONException {
            InputStream is = null;
            try {
                int len = 1000;
                URL url = new URL("http://maxclique-monday-v1.meteor.com/server/posts?no_pic=true");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("Tuesday", "The response is: " + response);
                // TODO(austin) handle cases when content length == -1
                int responseLength = conn.getContentLength();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[responseLength];
                reader.read(buffer);

                return new String(buffer);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                textView.setText(new JSONParser<JSONArray>(result,
                        new JSONParser.JSONArrayFactory()).getJSON().toString());
            } catch (Exception e) {
                textView.setText("Failure!");
            }
        }
    }
}
