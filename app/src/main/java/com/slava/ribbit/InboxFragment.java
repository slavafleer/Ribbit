package com.slava.ribbit;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Slava on 06/04/2015.
 */
public class InboxFragment extends ListFragment {
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
}
