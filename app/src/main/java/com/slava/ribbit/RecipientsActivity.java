package com.slava.ribbit;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ActionBarActivity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected ListView mRecipientsList;
    protected TextView mRecipientsEmpty;
    protected MenuItem sendMenuItem;

    protected Uri mMediaUri;
    protected String mFileType;
    protected String mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        mRecipientsList = (ListView) findViewById(R.id.recipientsListView);
        mRecipientsEmpty = (TextView) findViewById(R.id.recipientsEmpty);
        mRecipientsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        mTextMessage = getIntent().getExtras().getString("message");

        mRecipientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRecipientsList.getCheckedItemCount() > 0) {
                    sendMenuItem.setVisible(true);
                } else {
                    sendMenuItem.setVisible(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    // for each ParseUser named user in mUsers
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            mRecipientsList.getContext(),
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );

                    mRecipientsList.setAdapter(adapter);
                    mRecipientsList.setEmptyView(mRecipientsEmpty);

                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this)
                            .setMessage(e.getMessage())
                            .setTitle(R.string.title_section2)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        // Send button is first item in menu.
        sendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send ) {
            ParseObject message = createMessage();

            if(message == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.message_error_title))
                        .setMessage(getString(R.string.message_error_text))
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                send(message);
                // Closing this activity
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(RecipientsActivity.this,
                            getString(R.string.message_sent_text), Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this)
                            .setTitle(getString(R.string.message_error_title))
                            .setMessage(getString(R.string.message_error_text))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
        message.put(ParseConstants.TYPE_TEXT, mTextMessage);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if(fileBytes == null) {
            return null;
        } else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }

    }


    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i < mRecipientsList.getCount(); i++) {
            if(mRecipientsList.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }

        return recipientIds;
    }
}
