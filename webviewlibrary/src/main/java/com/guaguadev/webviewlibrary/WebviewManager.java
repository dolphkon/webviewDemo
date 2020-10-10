package com.guaguadev.webviewlibrary;

import android.content.Context;
import android.content.Intent;

import com.guaguadev.webviewlibrary.activity.WebviewActivity;

/**
 * ****************************************************
 * Project: webviewDemo
 * PackageName: com.guaguadev.webviewlibrary.utils
 * ClassName：WebviewManager
 * Author: kongdexi
 * Date: 2020/10/9 17:40
 * Description:TODO
 * *****************************************************
 */
public class WebviewManager {
    private Context context;
    private String url;
    private String title;

    private WebviewManager(Build build) {
        this.context = build.context;
        this.url = build.url;
        this.title = build.title;
    }

    public static Build with(Context context) {
        return new Build(context);
    }

    public static class Build {
        private Context context;
        private String url;
        private String title;

        private Build(Context context) {
            this.context = context.getApplicationContext();
        }


        /**
         * webview加载地址
         *
         * @param url
         */
        public Build setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * @param :
         * @return :  标题
         * @describe :
         */
        public Build setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 加载 webview
         */
        public WebviewManager load() {
            Intent intent= WebviewActivity.newInstance(context, url, title);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intent);
            return new WebviewManager(this);
        }

    }
}
