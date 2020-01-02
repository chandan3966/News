package com.example.news;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class Notification extends Application {

    private static Notification myApplication;
    SharedPreferences.Editor preEditor;

    public Notification(){
        myApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        preEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationHandler(this))
                .init();

    }

    class NotificationHandler implements OneSignal.NotificationOpenedHandler{

        private Context context;
        public NotificationHandler(Context context) {
            this.context = context;
        }

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            JSONObject data = result.notification.payload.additionalData;
            Toast.makeText(context,data.optString("url"),Toast.LENGTH_SHORT).show();
            if (data != null && data.has("url")){
                preEditor.putString("url",data.optString("url"));
                preEditor.apply();
            }
        }
    }

    public static synchronized Notification getInstance(){
        return myApplication;
    }
}
