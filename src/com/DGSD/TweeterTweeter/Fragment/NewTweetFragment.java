package com.DGSD.TweeterTweeter.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.FollowingProvider;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.TTApplication;
import com.DGSD.TweeterTweeter.Utils.MediaUploadTask;
import com.DGSD.TweeterTweeter.Utils.MentionsTokenizer;
import com.DGSD.TweeterTweeter.Utils.UrlShortenTask;
import com.DGSD.TweeterTweeter.Utils.Utils;
import com.github.droidfu.widgets.WebImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 * Author: Daniel Grech
 * Date: 26/11/11 6:03 PM
 * Description :
 */
public class NewTweetFragment extends DialogFragment  implements LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorAdapter.ViewBinder,
        UrlShortenTask.OnShortenUrlListener, MediaUploadTask.OnMediaUploadListener {
    private static final String TAG = NewTweetFragment.class.getSimpleName();

    protected static final String[] FROM = { Database.Field.SCREEN_NAME, Database.Field.IMG };

    protected static final int[] TO = { R.id.name, R.id.image };

    protected static String[] ROWS_TO_RETURN = {
            Database.Field.SCREEN_NAME,
            Database.Field.IMG,
            Database.Field.ID
    };

    protected static class CursorCols {
        public static int img = -1;
    }

    public static final int MAX_TWEET_SIZE = 140;

    private static final String TWEET_TEXT = "_tweet_text";

    private static final int GET_GALLERY_IMAGE = 0;

    private static final int GET_CAMERA_IMAGE = 1;

    private TextView mCharacterCount;

    private MenuItem mSubmitAction;

    private MultiAutoCompleteTextView mTweetText;

    private SimpleCursorAdapter mAdapter;

    private String mCurrentFilter;

    private ProgressDialog mProgressDialog;

    private String mLastDialogMessage;

    private boolean mProgressBarShowing = false;

    private UrlShortenTask mUrlShortenerTask;

    private MediaUploadTask mMediaUploadTask;

    private Uri mLastImageUri;

    public static NewTweetFragment newInstance() {
        final NewTweetFragment f = new NewTweetFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_tweet, container, false);

        if(getDialog() != null) {
            getDialog().setTitle("New Tweet");
        }

        mTweetText =
                (MultiAutoCompleteTextView) root.findViewById(R.id.text);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCharacterCount = new TextView(getActivity());
        mCharacterCount.setText( String.valueOf(MAX_TWEET_SIZE) );

        if(savedInstanceState != null) {
            addToTweet(mTweetText, savedInstanceState.getString(TWEET_TEXT, ""));
        } else {
            if(getArguments() != null) {
                addToTweet(mTweetText, getArguments().getString(Extra.TEXT));
            }
        }

        if(mProgressBarShowing) {
            mProgressDialog = ProgressDialog.show(getActivity(), "", mLastDialogMessage == null ? "Please Wait.." : mLastDialogMessage, true);
        }

        if(mUrlShortenerTask != null) {
            mUrlShortenerTask.setOnShortenUrlListener(this);
        }

        if(mMediaUploadTask != null) {
            mMediaUploadTask.setOnMediaUploadListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setupCharacterCounter();

        setupMentionCompletion();

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mTweetText != null) {
            outState.putString(TWEET_TEXT, mTweetText.getText().toString());
        }

        if(mProgressDialog != null) {
            mProgressBarShowing = mProgressDialog.isShowing();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.v(TAG, "onDetach()");

        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if(mUrlShortenerTask != null) {
            mUrlShortenerTask.setOnShortenUrlListener(null);
        }

        if(mMediaUploadTask != null) {
            mMediaUploadTask.setOnMediaUploadListener(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(Menu.NONE, MenuRes.CHAR_COUNT, Menu.NONE, "Character Count")
                .setActionView(mCharacterCount)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, MenuRes.IMG, Menu.NONE, "Images")
                .setIcon(R.drawable.ic_menu_camera)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.LOCATION, Menu.NONE, "Location")
                .setIcon(R.drawable.ic_menu_mylocation)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.CONTACTS, Menu.NONE, "Add Contact")
                .setIcon(R.drawable.ic_menu_add_contact)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.SHORTEN_URL, Menu.NONE, "Shorten Urls")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        mSubmitAction = menu.add(Menu.NONE, MenuRes.SUBMIT, Menu.NONE, "Send")
                .setIcon(R.drawable.ic_menu_send);

        if(mCharacterCount != null && mCharacterCount.getText().length() > 0) {
            try {
                if(Integer.parseInt(mCharacterCount.getText().toString()) < 0) {
                    mSubmitAction.setEnabled(false);
                }
            } catch(NumberFormatException e) {

            }
        }

        mSubmitAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MenuRes.SHORTEN_URL:
                mUrlShortenerTask = new UrlShortenTask(getActivity(), this);
                mUrlShortenerTask.execute();
                return true;
            case MenuRes.IMG:
                final CharSequence[] choices = {"Camera Picture", "Gallery image"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Attach an image");

                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item) {
                            case 0:
                                //Camera Picture
                                takePhoto();
                                break;
                            case 1:
                                //Gallery Picture
                                getGalleryPhoto();
                                break;
                        }
                    }
                });

                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupCharacterCounter() {
        mCharacterCount.setText( Integer.toString( MAX_TWEET_SIZE -
                mTweetText.getText().length() ) );

        if(mTweetText.getText().length()  > MAX_TWEET_SIZE){
            mCharacterCount.setTextColor(Color.RED);
            mSubmitAction.setEnabled(false);
        }
        else{
            mCharacterCount.setTextColor(Color.BLACK);
            mSubmitAction.setEnabled(true);
        }

        //Listen for changes in tweet text..
        mTweetText.addTextChangedListener(new TweetLengthWatcher());
    }

    private void getGalleryPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                GET_GALLERY_IMAGE);
    }

    private void takePhoto(){
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mLastImageUri = Utils.getTempPhotoUri(getActivity());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mLastImageUri);

        startActivityForResult(intent, GET_CAMERA_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode) {
            case GET_CAMERA_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    final File file = Utils.getTempFile(getActivity());

                    try{
                        file.deleteOnExit();
                    } catch(SecurityException e) {
                        Log.e(TAG, "onActivityResult()", e);
                    }

                    mMediaUploadTask = new MediaUploadTask(getActivity(),
                            ((TTApplication)getActivity().getApplication()).getSession().getAccessToken(),
                            Utils.getPath(getActivity(), mLastImageUri));

                    mMediaUploadTask.setOnMediaUploadListener(this);
                    mMediaUploadTask.execute();
                }
                else {
                    Log.i(TAG, "Picture not taken!");
                }
                break;
            case GET_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = intent.getData();

                    mMediaUploadTask = new MediaUploadTask(getActivity(),
                            ((TTApplication)getActivity().getApplication()).getSession().getAccessToken(),
                            Utils.getPath(getActivity(), imageUri));

                    mMediaUploadTask.setOnMediaUploadListener(this);
                    mMediaUploadTask.execute();
                }
                else {
                    Log.i(TAG, "Picture not chosen!");
                }
                break;
        }
    }

    public static void addToTweet(TextView tv, String text) {
        if(text == null) {
            return;
        }

        String currentText = tv.getText().toString();

        if(currentText.length() == 0) {
            tv.append(text);
        }
        else if( currentText.charAt(currentText.length()-1) == ' ' ) {
            tv.append(text);
        }
        else {
            tv.append(" " + text);
        }
    }

    /**
     * Setup for completion of '@' mentions
     */
    private void setupMentionCompletion() {
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_people, null, FROM, TO, 0);

        mAdapter.setViewBinder(this);

        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return new StringBuilder().append('@')
                        .append(cursor.getString(cursor.getColumnIndex(Database.Field.SCREEN_NAME)))
                        .toString();
            }
        });

        //Filter as we type
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                if(constraint != null) {
                    if(constraint.toString().startsWith("@")) {
                        return null;
                    }

                    mCurrentFilter = new StringBuilder().append("\"@\" ||")
                            .append(Database.Ordering.SCREEN_NAME)
                            .append(" LIKE \"")
                            .append(constraint)
                            .append("%\"")
                            .toString();
                    getLoaderManager().restartLoader(0, null, NewTweetFragment.this);
                    mCurrentFilter = null;
                }

                return mAdapter.getCursor();
            }
        });


        mTweetText.setAdapter(mAdapter);

        mTweetText.setThreshold(1);

        mTweetText.setTokenizer(new MentionsTokenizer());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), FollowingProvider.CONTENT_URI,
                ROWS_TO_RETURN, mCurrentFilter, null,
                Database.Ordering.SCREEN_NAME + Database.Ordering.ASC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in. (Old cursor is automatically closed)
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int col) {
        if(CursorCols.img < 0) {
            //We need to get the column numbers
            CursorCols.img = cursor.getColumnIndex(Database.Field.IMG);
        }

        if(col == CursorCols.img) {
            //Only load the image if we are not currently flinging the listview
            WebImageView img = (WebImageView) view;
            //img.reset();
            img.setImageUrl(cursor.getString(CursorCols.img));
            img.loadImage();

            return true;
        }

        return false;
    }

    @Override
    public void onStartUrlShorten() {
        mLastDialogMessage = "Shortening urls";
        mProgressDialog = ProgressDialog.show(getActivity(), "", mLastDialogMessage, true);
    }

    @Override
    public void onFinishUrlShorten(Vector<UrlShortenTask.Hyperlink> links) {
        String text = mTweetText.getText().toString();
        for(UrlShortenTask.Hyperlink link : links) {
            if(link.newUrl == null) {
                continue;
            }

            Log.i(TAG, "Got link: " + link.foundUrl + " " + link.newUrl);

            text = text.replace(link.foundUrl, link.newUrl);
        }

        mTweetText.setText("");

        mTweetText.append(text);

        mProgressDialog.dismiss();
    }

    @Override
    public String onGetText() {
        return mTweetText == null ? null : mTweetText.getText().toString();
    }

    @Override
    public void onShortenUrlError() {
        mProgressDialog.dismiss();

        Toast.makeText(getActivity(),
                "Error shortening url", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartMediaUpload() {
        mLastDialogMessage = "Uploading images";
        mProgressDialog = ProgressDialog.show(getActivity(), "", mLastDialogMessage, true);
    }

    @Override
    public void onFinishMediaUpload(String url) {
        addToTweet(mTweetText, url);

        mProgressDialog.dismiss();
    }

    @Override
    public void onMediaUploadError() {
        mProgressDialog.dismiss();

        Toast.makeText(getActivity(),
                "Error uploading image", Toast.LENGTH_SHORT).show();
    }

    private class TweetLengthWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            int size = s.length();
            mCharacterCount.setText(Integer.toString( MAX_TWEET_SIZE - size ) );

            if(size > MAX_TWEET_SIZE){
                mCharacterCount.setTextColor(Color.RED);
                mSubmitAction.setEnabled(false);
            }
            else{
                mCharacterCount.setTextColor(Color.BLACK);
                mSubmitAction.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

    private static class MenuRes {
        public static final int SUBMIT = 0;

        public static final int LOCATION = 1;

        public static final int IMG = 2;

        public static final int SHORTEN_URL = 3;

        public static final int CONTACTS = 4;

        public static final int CHAR_COUNT = 5;

    }

    public static class Extra {
        public static final String TEXT = "_text";
    }
}
