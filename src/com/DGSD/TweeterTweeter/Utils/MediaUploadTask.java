package com.DGSD.TweeterTweeter.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.TTApplication;
import com.github.droidfu.concurrent.BetterAsyncTask;
import com.rosaloves.bitlyj.BitlyException;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.PropertyConfiguration;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

/**
 * Author: Daniel Grech
 * Date: 3/12/11 3:48 PM
 * Description :
 */

public class MediaUploadTask extends BetterAsyncTask<Void, Void, String> {
    private static final String TAG = MediaUploadTask.class.getSimpleName();

    private static final int REQUIRED_SIZE=70;

    private OnMediaUploadListener mListener;

    private AccessToken mToken;

    private String mFilePath;

    public MediaUploadTask(Context c, AccessToken token, String filepath) {
        super(c);


        mToken = token;

        mFilePath = filepath;
    }

    public void setOnMediaUploadListener(OnMediaUploadListener listener) {
        mListener = listener;
    }

    @Override
    protected void before(Context context) {
        if(mListener != null) {
            mListener.onStartMediaUpload();
        }
    }

    @Override
    protected String doCheckedInBackground(Context context, Void... params) throws Exception {
        Properties props = new Properties();

        props.put(PropertyConfiguration.MEDIA_PROVIDER, MediaProvider.TWITPIC.getName());
        props.put(PropertyConfiguration.OAUTH_ACCESS_TOKEN, mToken.getToken());
        props.put(PropertyConfiguration.OAUTH_ACCESS_TOKEN_SECRET, mToken.getTokenSecret());
        props.put(PropertyConfiguration.OAUTH_CONSUMER_KEY,TTApplication.CONSUMER_KEY);
        props.put(PropertyConfiguration.OAUTH_CONSUMER_SECRET,TTApplication.CONSUMER_SECRET);
        props.put(PropertyConfiguration.MEDIA_PROVIDER_API_KEY, context.getResources().getString(R.string.twitpic_key));

        ImageUpload imageUpload = new ImageUploadFactory(new PropertyConfiguration(props)).getInstance(MediaProvider.TWITPIC);

        Bitmap b = decodeFile( new File(mFilePath) );
        File file = File.createTempFile(Integer.toString(new Random().nextInt()), ".jpg");
        writeBitmapToFile(b, file);

        try {
            return imageUpload.upload(file);
        } finally {
            if (!file.delete()) {
                Log.e(TAG, "Failed to delete " + file.getAbsolutePath());
            }
        }
    }

    @Override
    protected void after(Context context, String url) {
        if(mListener != null) {
            mListener.onFinishMediaUpload(url);
        }
    }

    @Override
    protected void handleError(Context context, Exception e) {
        Log.e(TAG, "Error in mediaUploadTask()", e);

        if(mListener != null) {
            mListener.onMediaUploadError();
        }
    }

    private void writeBitmapToFile(Bitmap b, File f) throws IOException {
        final FileOutputStream out = new FileOutputStream(f);

        b.compress(Bitmap.CompressFormat.JPEG, 90, out);

        out.close();
    }

    private Bitmap decodeFile(File f) throws IOException{
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = getImageScale(REQUIRED_SIZE, o.outWidth, o.outHeight);
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    }

    /**
     * Find the correct image scale. This will always return a power of 2
     * @param size The scaling percentage of the image we want to produce
     * @param width Width of the original image
     * @param height Height of the original image
     * @return A scaling factor for use with BitmapFactory.Options.inSampleSize
     */
    private static int getImageScale(int size, int width, int height) {
        int scale=1;
        while(true){
            if( (width / 2) < REQUIRED_SIZE || (height / 2) < REQUIRED_SIZE)  {
                break;
            }
            width /= 2;
            height /= 2;
            scale++;
        }

        return scale;
    }

    public interface OnMediaUploadListener {
        public void onStartMediaUpload();
        public void onFinishMediaUpload(String url);
        public void onMediaUploadError();
    }
}