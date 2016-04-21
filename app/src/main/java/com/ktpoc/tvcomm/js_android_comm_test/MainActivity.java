package com.ktpoc.tvcomm.js_android_comm_test;

import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private final String _url = "https://tvcomm.dlinkddns.com:3000/homepage/amuzlab";
    //private final String _url = "http://175.209.53.205/songsong.html";

    private final String _TAG = "[ SONG_TEST ]";
    private final Handler handler = new Handler();
    private final String _ANDROID_APP_KEY = "android2JS"; //JSInterface add할때 어느 안드로이드 앱인 지 구분해줘야 되서 넣는 거
    public WebView mWebView;

    /* 맨처음에 홈페이지 웹뷰 띄운 상태에서 버튼 누르면 안드로이드에서 TV UI 웹앱으로 USER EVENT SENDING 이라는 메세지 보내고,
       웹앱의 자바스크립트 함수에서 메세지 받으면 그 메세지 그대로 안드로이드로 보냄
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView  = (WebView)findViewById(R.id.web_view);
        //1. 메인 페이지 일단 먼저 띄우고
        loadWebView(_url);
        setWebViewSettings();
    }
    public void handleButtonEvent(View view){
        //3. javascript 소스 내에 안드로이드로부터 메세지 수신하는 함수가 onReceiveFromAndroid 인데 이 함수를 안드로이드에서 호출 그 떄 보내고 싶은 메세지를 함께 파라메터로 전송
        // 만약 함수 여러개 부르고 싶으면 자바스크립트에도 함수 여러개 등록 해서 그 함수 호출
        mWebView.loadUrl("javascript:onReceiveFromAndroid('USER EVENT SENDING...')");
    }

    private void loadWebView(String url){
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.loadUrl(url);
        Log.d(_TAG, "CHROME VERSION : " + mWebView.getSettings().getUserAgentString());
    }

    private void setWebViewSettings(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        //2. JS 인터페이스 뚫고
        mWebView.addJavascriptInterface(new AndroidBridge(), _ANDROID_APP_KEY);
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void sendMessageFromJS(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d(_TAG, "JS SEND A MSG THAT --> " + arg);
                    // 자바스트립트 에서 sendMessageFromJS에 파라미터로 전송시키고 싶은 메세지 태워서 보내면 됨
                }
            });
        }
        //자바스크립트에서 안드로이드 소스 부르고 싶을 땐 여기다 안드로이드 함수 등록하고 자바스크립트에서 호출하면 됨
    }

}
