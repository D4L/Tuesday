package com.maxclique.tuesday.app;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

/**
 * Created by Austin on 6/17/2014.
 */
public class DisplayPostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_post_fragment, container, false);
        refresh();
        return resultantView;
    }

    public void refresh() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // writeListView(getString(R.string.fetching));
        if (networkInfo != null && networkInfo.isConnected()) {
            String postId = getArguments().getString("id", null);
            if (postId == null) {
                return;
            }
            new DownloadWebpageTask(ServerURL.getPost(postId),
                    new DownloadWebpageTask.DownloadWebpageTaskCallback() {
                        @Override
                        public void run(String resultOfTask) {
                            Log.d("Tuesday", resultOfTask);
                            try {
                                /*writeListView(JSONParser.convertJSONObjects(
                                        new JSONParser<JSONArray>(resultOfTask,
                                                new JSONParser.JSONArrayFactory()).getJSON()
                                ));*/
                            } catch (Exception e) {
                                //writeListView("Failure!");
                            }
                        }
                    }
            ).execute();
        } else {
            // display
            // writeListView(getString(R.string.no_network));
        }
    }
}
