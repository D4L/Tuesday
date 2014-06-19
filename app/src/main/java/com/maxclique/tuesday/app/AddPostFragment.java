package com.maxclique.tuesday.app;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Austin on 6/19/2014.
 */
public class AddPostFragment extends Fragment {

    EditText subjectText;
    EditText detailsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.add_post_fragment, container, false);

        subjectText = (EditText) resultantView.findViewById(R.id.subject);
        detailsText = (EditText) resultantView.findViewById(R.id.details);

        Button button = (Button) resultantView.findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addPost(subjectText.getText().toString(), detailsText.getText().toString());
            }
        });
        // set up the app icon as an UP button
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.add_post_title));
        setHasOptionsMenu(true);

        return resultantView;
    }

    private void addPost(String subject, String details) {
        HashMap<String, String> object = new HashMap<String, String>();
        object.put(getString(R.string.subject), subject);
        object.put(getString(R.string.details), details);

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //writeListView(getString(R.string.fetching));
        if (networkInfo != null && networkInfo.isConnected()) {
            new PostWebpageTask(ServerURL.addPost(),
                    new PostWebpageTask.PostWebpageTaskCallback() {

                        @Override
                        public void run(String resultOfTask) {
                            getFragmentManager().popBackStack();
                        }

                    }
            ).execute(object);
        } else {
            // display
            //writeListView(getString(R.string.no_network));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
