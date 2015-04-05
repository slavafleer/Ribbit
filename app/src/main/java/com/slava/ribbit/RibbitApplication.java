package com.slava.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Slava on 05/04/2015.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "AsmLSbTlxqzs2rSLJ6L3CLfj1c7fdvhOzI25jlV5", "TTrioJSv7ai6v6ZZN73hwtpzoGXe4uVHsLH2TD8B");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
