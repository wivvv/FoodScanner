package com.example.john.foodscanner;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by john on 10/6/17.
 */

public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}