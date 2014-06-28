package com.maxclique.tuesday.app;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Austin on 6/17/2014.
 */
public class DisplayPostFragment extends Fragment {

    private TextView titleView;
    private TextView detailsView;
    private TextView dateView;
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_post_fragment, container, false);
        titleView = (TextView) resultantView.findViewById(R.id.title);
        detailsView = (TextView) resultantView.findViewById(R.id.details);
        dateView = (TextView) resultantView.findViewById(R.id.date);
        mImageView = (ImageView) resultantView.findViewById(R.id.image);

        // set up the app icon as an UP button
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        JSONObject object = grabObjectFromArgs();
        if (object != null) {
            writeViews(object);
        }

        try {
            grabImage(object.getString("_id"));
        } catch (Exception e) {
        }
        return resultantView;
    }

    private JSONObject grabObjectFromArgs() {
        String stringObject = getArguments().getString("object", null);
        if (stringObject == null) {
            return null;
        }
        JSONObject object;
        try {
            object = new JSONParser<JSONObject>(stringObject,
                    new JSONParser.JSONObjectFactory()).getJSON();
        } catch (Exception e) {
            titleView.setText("Failure!");
            return null;
        }
        return object;
    }

    public void grabImage(String id) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask(ServerURL.getImage(id),
                    new DownloadWebpageTask.DownloadWebpageTaskCallback() {
                        @Override
                        public void run(String resultOfTask) {

                            if (resultOfTask.equals("")) {
                                return;
                            }
                            String imageDataBytes =
                                    resultOfTask.substring(resultOfTask.indexOf(",") + 1);

                            byte[] is = Base64.decode(imageDataBytes, Base64.NO_PADDING);
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(
                                    is, 0, is.length);
                            mImageView.setImageBitmap(imageBitmap);
                        }
                    }
            ).execute();
        }
    }

    private void writeViews(JSONObject object) {
        try {
            titleView.setText(object.getString(getString(R.string.subject)));
            dateView.setText(TimeAgoParser.timeAgoSince(getActivity(),
                    object.getLong(getString(R.string.created_at))));
            detailsView.setText(object.optString(getString(R.string.details),
                    ""));
        } catch (Exception e) {
            titleView.setText("Failure!");
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
