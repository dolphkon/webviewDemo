package com.guaguadev.webviewlibrary.activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.guaguadev.webviewlibrary.R;
import com.guaguadev.webviewlibrary.utils.PaxWebChromeClient;
import com.guaguadev.webviewlibrary.utils.WebviewHelper;

/**
 * ****************************************************
 * Project: webviewDemo
 * PackageName: com.guaguadev.webviewlibrary.activity
 * ClassName：WebviewActivity
 * Author: kongdexi
 * Date: 2020/10/9 17:41
 * Description:TODO
 * *****************************************************
 */
public class WebviewActivity extends FragmentActivity implements View.OnClickListener {
    private String url;
    private String title;
    private PaxWebChromeClient chromeClient;
    private WebView mWebview;
    private TextView mTitle;
    private ImageView mImgBack;
    private ImageView ivClose;
    private static boolean reStrict = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_web_view);
        initView();
    }

    public static Intent newInstance(Context context, String url, String title) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        return intent;
    }

    public void initWebView() {
        WebviewHelper.settings(this, mWebview);
        chromeClient = new PaxWebChromeClient(this);
        mWebview.setWebChromeClient(chromeClient);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    reStrict = true;
                    checkShowBack();
                }
                if (url.startsWith("tel")) {
                    String phoneNumber = url.substring(url.lastIndexOf("/") + 1);
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(dialIntent);
                    return true;
                }
                try {
                    if (url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("mailto://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                view.loadUrl(url);
                return true;
            }
        });
        mWebview.loadUrl(url);
    }

    public void initView() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        mWebview = findViewById(R.id.web_view);
        mTitle = findViewById(R.id.title);
        mImgBack = findViewById(R.id.im_back);
        ivClose = findViewById(R.id.iv_close);
        mImgBack.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        if (mImgBack != null) {
            mImgBack.setVisibility(showLeftImg() ? View.VISIBLE : View.GONE);
        }
        mTitle.setText(title);
        initWebView();
    }


    @Override
    protected void onPause() {
        WebviewHelper.onPause(mWebview);
        super.onPause();
    }

    @Override
    protected void onResume() {
        WebviewHelper.onResume(mWebview);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        WebviewHelper.onDestory(mWebview);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chromeClient.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean showLeftImg() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.im_back) {
            checkBack();
        } else if (v.getId() == R.id.iv_close) {
            finish();
        }
    }



/**
 *  检测是否能返回
 * */
    private boolean checkBack() {
        //兼容webview返回
        WebBackForwardList mWebBackForwardList = mWebview.copyBackForwardList();
        if (mWebBackForwardList != null && mWebBackForwardList.getSize() == 2 && mWebBackForwardList.getItemAtIndex(0).getUrl().contains("redirect=1")) {
            finish();
            return false;
        }

        if (mWebview.canGoBack()) {
            mWebview.goBack();//返回上一页面
            return true;
        } else {
            finish();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return checkBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *  是否显示返回键
     * */
    private void checkShowBack() {
        if (reStrict) {
            ivClose.setVisibility(View.VISIBLE);
            mTitle.setMaxEms(8);//标题可能会被遮挡
        } else {
            ivClose.setVisibility(View.GONE);
        }
    }
}
