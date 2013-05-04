package ru.thegoncharov.authwatch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_PROGRESS);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadUrl("file:///android_asset/3rd_party.html");
    }
}
