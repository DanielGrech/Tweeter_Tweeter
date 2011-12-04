package com.DGSD.TweeterTweeter.Utils;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;
import com.DGSD.TweeterTweeter.R;
import com.github.droidfu.concurrent.BetterAsyncTask;
import com.rosaloves.bitlyj.BitlyException;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Daniel Grech
 * Date: 3/12/11 3:48 PM
 * Description :
 */

public class UrlShortenTask extends BetterAsyncTask<Void, Void, Vector<UrlShortenTask.Hyperlink>> {
    private static final String TAG = UrlShortenTask.class.getSimpleName();

    private String mUserName;

    private String mKey;

    private OnShortenUrlListener mListener;

    Pattern hyperLinksPattern =
            Pattern.compile("\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public UrlShortenTask(Context c, OnShortenUrlListener listener) {
        super(c);

        mListener = listener;
    }

    public void setOnShortenUrlListener(OnShortenUrlListener listener) {
        mListener = listener;
    }

    @Override
    protected void before(Context context) {
        mUserName = context.getResources().getString(R.string.bitlyName);

        mKey = context.getResources().getString(R.string.bitlyKey);

        if(mListener != null) {
            mListener.onStartUrlShorten();
        }
    }

    @Override
    protected Vector<Hyperlink> doCheckedInBackground(Context context, Void... params) throws Exception {
        if(mListener == null) {
            return new Vector<Hyperlink>();
        }

        SpannableString linkableText = new SpannableString(mListener.onGetText());

        Vector<Hyperlink> links = gatherLinks(linkableText, hyperLinksPattern);

        for(Hyperlink link : links) {
            String url = link.foundUrl.toString();
            if(!url.startsWith("http://")) {
                url = "http://".concat(url);
            }

            try {
                link.newUrl = as(mUserName, mKey)
                        .call(shorten(url)).getShortUrl();
            } catch(BitlyException e) {
                Log.e(TAG, "Error shortening url", e);
            }
        }

        return links;
    }

    @Override
    protected void after(Context context, Vector<Hyperlink> linkList) {
        if(mListener != null) {
            mListener.onFinishUrlShorten(linkList);
        }
    }

    @Override
    protected void handleError(Context context, Exception e) {
        Log.e(TAG, "Error in UrlShortenTask()", e);

        if(mListener != null) {
            mListener.onShortenUrlError();
        }
    }

    private final Vector<Hyperlink> gatherLinks(Spannable s, Pattern pattern){
        // Matcher matching the pattern
        Matcher m = pattern.matcher(s);

        Vector<Hyperlink> links = new Vector<Hyperlink>();

        while (m.find()){
            int start = m.start();
            int end = m.end();

            Hyperlink spec = new Hyperlink();

            spec.foundUrl = s.subSequence(start, end);

            links.add(spec);
        }

        return links;
    }

    public interface OnShortenUrlListener {
        public void onStartUrlShorten();
        public void onFinishUrlShorten(Vector<Hyperlink> links);
        public void onShortenUrlError();
        public String onGetText();
    }


    public static class Hyperlink{
        public CharSequence foundUrl;
        public String newUrl;
    }
}