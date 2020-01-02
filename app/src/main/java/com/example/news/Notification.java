package com.example.news;

import android.app.Application;

import com.onesignal.OneSignal;

public class Notification extends Application {

    private static Notification myApplication;

    public Notification(){
        myApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

    public static synchronized Notification getInstance(){
        return myApplication;
    }
}
