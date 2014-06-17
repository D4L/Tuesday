package com.maxclique.tuesday.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            new DownloadWebpageTask(ServerURL.getAllPosts(),
                    new DownloadWebpageTask.DownloadWebpageTaskCallback() {
                        @Override
                        public void run(String resultOfTask) {
                            try {
                                writeListView(JSONParser.convertJSONObjects(
                                        new JSONParser<JSONArray>(resultOfTask,
                                                new JSONParser.JSONArrayFactory()).getJSON()
                                ));
                            } catch (Exception e) {
                                writeListView("Failure!");
                            }
                        }
                    }
            ).execute();
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
                row = ((Activity) context).getLayoutInflater().inflate(
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
            } catch (JSONException e) {
            }
            return row;
        }
    }

}
