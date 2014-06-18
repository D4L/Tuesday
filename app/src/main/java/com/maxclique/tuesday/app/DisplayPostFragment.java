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
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Austin on 6/17/2014.
 */
public class DisplayPostFragment extends Fragment {

    private TextView titleView;
    private TextView detailsView;
    private TextView dateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_post_fragment, container, false);
        titleView = (TextView) resultantView.findViewById(R.id.title);
        detailsView = (TextView) resultantView.findViewById(R.id.details);
        dateView = (TextView) resultantView.findViewById(R.id.date);

        refresh();
        return resultantView;
    }

    public void refresh() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        titleView.setText(getString(R.string.fetching));
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
                                JSONObject object = new JSONParser<JSONObject>(resultOfTask,
                                        new JSONParser.JSONObjectFactory()).getJSON();
                                titleView.setText(object.getString(getString(R.string.subject)));
                                dateView.setText(TimeAgoParser.timeAgoSince(getActivity(),
                                    object.getLong(getString(R.string.created_at))));
                                detailsView.setText(object.optString(getString(R.string.details),
                                        ""));
                            } catch (Exception e) {
                                titleView.setText("Failure!");
                            }
                        }
                    }
            ).execute();
        } else {
            // display
            titleView.setText(getString(R.string.no_network));
        }
    }
}
