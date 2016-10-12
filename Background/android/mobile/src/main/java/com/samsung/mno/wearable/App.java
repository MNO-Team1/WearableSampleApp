package com.samsung.mno.wearable;

import android.content.Context;

/**
 * Created by vgandhi on 10/10/16.
 */

public class App extends android.app.Application {
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
