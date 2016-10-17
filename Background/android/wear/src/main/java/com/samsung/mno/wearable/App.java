package com.samsung.mno.wearable;

import android.app.Application;
import android.content.Context;

/**
 * Created by vgandhi on 10/13/16.
 */

public class App extends Application {
    static Context mApplicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }
}
