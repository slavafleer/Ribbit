package com.slava.ribbit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Slava on 06/04/2015.
 */
public class InboxFragment extends ListFragment {
    public List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The first parameter is the layout ID that's used for this fragment.
        // The second parameter is the container where the fragment will be displayed.
        // This will be the view pager from main activity.
        // The last parameter should be false whenever we're adding a fragment
        // to an activity in code which we will do in a moment.
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser.getCurrentUser().getObjectId());
        // KEY_CREATED_AT = "createdAt";
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e == null) {
                    // Messages were found.
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    // for each ParseUser named user in mUsers
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);
                    } else {
                        // refill adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        String messageText = message.getString(ParseConstants.KEY_MESSAGE);
        Uri fileUri = Uri.parse(file.getUrl());

        switch (messageType) {
            case ParseConstants.TYPE_TEXT:
                // View Text Message
                //Toast.makeText(getActivity(), messageText, Toast.LENGTH_LONG).show();
                break;
            case ParseConstants.TYPE_IMAGE:
                // View Image
                Intent imageIntent = new Intent(getActivity(), ViewImageActivity.class);
                imageIntent.setData(fileUri);
                startActivity(imageIntent);
                break;
            case ParseConstants.TYPE_VIDEO:
                // View Video
                Intent videoIntent = new Intent(Intent.ACTION_VIEW, fileUri);
                videoIntent.setDataAndType(fileUri, "video/*");
                startActivity(videoIntent);
                break;
        }
    }
}
