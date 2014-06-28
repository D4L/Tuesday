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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
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
                takePicture();
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // TODO: think about this a bit more :P, compress rate small
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        StringBuilder sb = new StringBuilder();
        sb.append("data:");
        sb.append(mImageType);
        sb.append(";base64,");
        sb.append(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        return sb.toString();
    }

    private void takePicture() {
        File file = new File(getActivity().getExternalFilesDir(null), "imageFile.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null && file != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, 1435);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1435 || resultCode != Activity.RESULT_OK) {
            return;
        }
        File file = new File(getActivity().getExternalFilesDir(null), "imageFile.jpg");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int requiredHeight = mImageView.getMaxHeight();
        int requiredWidth = mImageView.getMeasuredWidth();
        String imageType = options.outMimeType;
        int inSampleSize = 1;
        while ((imageHeight / inSampleSize) > requiredHeight ||
                (imageWidth / inSampleSize) > requiredWidth) {
            inSampleSize *= 2;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        mImageType = imageType;
        mImageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        mImageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath(), options));
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
