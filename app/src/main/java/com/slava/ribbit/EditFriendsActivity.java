package com.slava.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null) {
                    // Success
                    mUsers = parseUsers;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    // for each ParseUser named user in mUsers
                    for(ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this)
                            .setMessage(e.getMessage())
                            .setTitle(R.string.title_activity_edit_friends)
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
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
