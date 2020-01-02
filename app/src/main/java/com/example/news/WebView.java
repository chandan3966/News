package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

public class WebView extends AppCompatActivity {
    private android.webkit.WebView wb;
    private String s = "";
    private Notification notification;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        notification = Notification.getInstance();
        s = getIntent().getStringExtra("url");
        wb = findViewById(R.id.webview);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.setWebViewClient(new HelloWebViewClient());
        wb.loadUrl(s);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        preferences.edit().clear().commit();
        startActivity(new Intent(WebView.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.edit().clear().commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().clear().commit();
    }

    private class HelloWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
