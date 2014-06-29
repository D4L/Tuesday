package com.maxclique.tuesday.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Austin on 6/28/2014.
 */
public class PictureHelper {

    static final int CAPTURE_IMAGE_REQUEST_CODE = 1435;

    static public File getImageFile(Activity activity) {
        return new File(activity.getExternalFilesDir(null), "imageFile.jpg");
    }

    static public String encodePicture(Bitmap image, String imageType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // TODO: think about this a bit more :P, compress rate small
        image.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        StringBuilder sb = new StringBuilder();
        sb.append("data:");
        sb.append(imageType);
        sb.append(";base64,");
        sb.append(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        return sb.toString();
    }

    static public Intent getLargePicIntent(Activity activity) {
        File file = getImageFile(activity);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (activity.getPackageManager() != null &&
                intent.resolveActivity(activity.getPackageManager()) != null && file != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            return intent;
        }
        return null;
    }

    static public int getSampleSize(BitmapFactory.Options options,
                                    int scaledHeight, int scaledWidth) {
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int inSampleSize = 1;
        while ((imageHeight / inSampleSize) > scaledHeight ||
                (imageWidth / inSampleSize) > scaledWidth) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    static public int getSampleSizeOfFile(Activity activity, BitmapFactory.Options options,
                                          int scaledHeight, int scaledWidth) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getImageFile(activity).getAbsolutePath(), options);
        options.inJustDecodeBounds = false;
        return getSampleSize(options, scaledHeight, scaledWidth);
    }

    static public int getSampleSizeOfByteArray(byte[] image, BitmapFactory.Options options,
                                               int scaledHeight, int scaledWidth) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);
        options.inJustDecodeBounds = false;
        return getSampleSize(options, scaledHeight, scaledWidth);
    }

    static public Bitmap getBitmapOfFile(Activity activity, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(getImageFile(activity).getAbsolutePath(), options);
    }

    static public Bitmap getBitmapOfByteArray(byte[] image, BitmapFactory.Options options) {
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }
}
