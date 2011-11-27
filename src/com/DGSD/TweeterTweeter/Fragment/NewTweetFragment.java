package com.DGSD.TweeterTweeter.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.Utils.Utils;

/**
 * Author: Daniel Grech
 * Date: 26/11/11 6:03 PM
 * Description :
 */
public class NewTweetFragment extends DialogFragment {
    private static final String TAG = NewTweetFragment.class.getSimpleName();

    public static final int MAX_TWEET_SIZE = 140;

    private static final String TWEET_TEXT = "_tweet_text";

    private static final int GET_GALLERY_IMAGE = 0;

    private static final int GET_CAMERA_IMAGE = 1;

    private TextView mCharacterCount;

    private MenuItem mSubmitAction;

    private MultiAutoCompleteTextView mTweetText;

    public static NewTweetFragment newInstance() {
        final NewTweetFragment f = new NewTweetFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mCharacterCount.setText("140");

        if(savedInstanceState != null) {
            addToTweet(mTweetText, savedInstanceState.getString(TWEET_TEXT, ""));
        } else {
            if(getArguments() != null) {
                addToTweet(mTweetText, getArguments().getString(Extra.TEXT));
            }
        }

        //setupMentionCompletion();
    }

    @Override
    public void onResume() {
        super.onResume();

        setupCharacterCounter();

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mTweetText != null) {
            outState.putString(TWEET_TEXT, mTweetText.getText().toString());
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

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.getTempFile(getActivity())) );

        startActivityForResult(intent, GET_CAMERA_IMAGE);
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
