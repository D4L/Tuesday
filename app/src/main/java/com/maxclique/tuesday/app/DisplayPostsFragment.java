package com.maxclique.tuesday.app;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_posts_fragment, container, false);

        listView = (ListView) resultantView.findViewById(R.id.posts_list_view);
        refresh();

        return resultantView;
    }

    public void refresh() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        writeListView(getString(R.string.fetching));
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute();
        } else {
            // display
            writeListView(getString(R.string.no_network));
        }
    }

    protected void writeListView(String string) {
        writeListView(JSONParser.convertJSONObjects(getString(R.string.subject), string));
    }

    protected void writeListView(JSONObject[] objects) {
        PostListAdapter postAdapter = new PostListAdapter(getActivity(),
                R.layout.display_post_row, objects);
        listView.setAdapter(postAdapter);
    }

    private class PostListAdapter extends ArrayAdapter<JSONObject> {
        Context context;
        int layoutResourceId;
        JSONObject[] objects;

        public PostListAdapter(Context context, int resource, JSONObject[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.layoutResourceId = resource;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = ((Activity)context).getLayoutInflater().inflate(
                        layoutResourceId, parent, false);

            }
            JSONObject post = objects[position];
            TextView titleView = (TextView) row.findViewById(R.id.title);
            TextView dateView = (TextView) row.findViewById(R.id.date);
            try {
                titleView.setText(post.getString(getString(R.string.subject)));

            } catch (JSONException e) {
                titleView.setText(getString(R.string.json_convert_error));
            }
            try {
                dateView.setText(post.getString(getString(R.string.created_at)));
            } catch (JSONException e) {}
            return row;
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
                writeListView(JSONParser.convertJSONObjects(new JSONParser<JSONArray>(result,
                        new JSONParser.JSONArrayFactory()).getJSON()));
            } catch (Exception e) {
                writeListView("Failure!");
            }
        }
    }
}
