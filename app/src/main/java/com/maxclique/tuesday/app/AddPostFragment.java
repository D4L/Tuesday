package com.maxclique.tuesday.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Austin on 6/19/2014.
 */
public class AddPostFragment extends Fragment {

    EditText subjectText;
    EditText detailsText;
    ImageView mImageView;
    String mImageType;
    Bitmap mImageBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.add_post_fragment, container, false);

        subjectText = (EditText) resultantView.findViewById(R.id.subject);
        detailsText = (EditText) resultantView.findViewById(R.id.details);
        mImageView = (ImageView) resultantView.findViewById(R.id.image);
        mImageBitmap = null;

        Button button = (Button) resultantView.findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addPost(subjectText.getText().toString(), detailsText.getText().toString());
            }
        });
        button = (Button) resultantView.findViewById(R.id.button_add_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PictureHelper.getLargePicIntent(getActivity());
                startActivityForResult(intent, PictureHelper.CAPTURE_IMAGE_REQUEST_CODE);
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
        if (mImageBitmap != null) {
            object.put("image", encodePicture(mImageBitmap));
        }

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

    private String encodePicture(Bitmap image) {
        return PictureHelper.encodePicture(image, mImageType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PictureHelper.CAPTURE_IMAGE_REQUEST_CODE ||
                resultCode != Activity.RESULT_OK) {
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = PictureHelper.getSampleSizeOfFile(getActivity(), options,
                mImageView.getMaxHeight(), mImageView.getMeasuredWidth());
        mImageType = options.outMimeType;
        mImageBitmap = PictureHelper.getBitmapOfFile(getActivity(), options);
        mImageView.setImageBitmap(mImageBitmap);
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
