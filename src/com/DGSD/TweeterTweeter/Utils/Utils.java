package com.DGSD.TweeterTweeter.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Daniel Grech
 * Date: 14/11/11 2:26 PM
 * Description :
 */
public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    public static boolean isTablet(Context context) {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) && (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String join(AbstractCollection<String> s, String delimiter) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        Iterator<String> iter = s.iterator();
        StringBuffer buffer = new StringBuffer(iter.next());

        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }

        return buffer.toString();
    }

    public static List<String> unjoin(String string, String joiner) {
        if(string == null || string.length() == 0) {
            return null;
        }

        System.err.println("UNJOINING STRING: " + string);
        return Arrays.asList(string.split(joiner));
    }

    public static int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }

        int numDigits= 0;

        for (int i = 0, size = inString.length(); i < size; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    public static boolean isEmpty(String inString) {
        return inString == null || inString.length() == 0;
    }

    public static boolean isInLandscape(Context c) {
        Display d = ((WindowManager) c.getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
        return d.getWidth() > d.getHeight();
    }

    public static Uri getTempPhotoUri(Context c) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "imageUpload");
        values.put(MediaStore.Images.Media.DESCRIPTION, "An image uploaded using TweeterTweeter");
        Uri tempPhotoUri = null;
        try {
            tempPhotoUri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch(UnsupportedOperationException e){
            try{
                tempPhotoUri =
                        c.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
            }catch(UnsupportedOperationException e2){
                e2.printStackTrace();
            }
        }
        return tempPhotoUri ;
    }

    public static File getTempFile(Context context){
        //it will return /sdcard/com.DGSD.TweeterTweeter/.image.tmp
        final File path = new File( Environment.getExternalStorageDirectory(),context.getPackageName() );

        if(!path.exists()) {
            path.mkdir();
        }

        return new File(path, ".image.tmp");
    }

    public static String getPath(Activity activity, Uri uri) {
        Cursor cursor = activity.managedQuery(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);

        if( cursor.moveToFirst() ) {
            return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        } else {
            Log.w(TAG, "Returning null from getPath()");
            return null;
        }
    }
}
