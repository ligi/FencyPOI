package org.battlehack.fencypoi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

/**
 * Created by sodoku on 09.06.13.
 */
public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        this.setContentView(R.layout.activity_about);
        String POSTText = null;
        WebView webview = (WebView) findViewById(R.id.donateWebView);
        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 100);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        //this code can be taken from GET request in previous solution
        POSTText = "cmd=_donations&business=philip.d.stewart+paypal@googlemail.com&lc=US&item_name=Doation&currency_code=USD&bn=PP-DonationsBF:btn_donateCC_LG.gif:NonHosted";
        byte[] post = null;
        post = EncodingUtils.getBytes(POSTText, "BASE64");
        webview.postUrl("https://www.paypal.com/cgi-bin/webscr", post);
    }


}