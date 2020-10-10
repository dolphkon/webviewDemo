package com.guaguadev.webviewlibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.guaguadev.webviewlibrary.R;

/**
 * ****************************************************
 * Project: webviewDemo
 * PackageName: com.guaguadev.webviewlibrary.dialog
 * ClassName：LoadingDialog
 * Author: kongdexi
 * Date: 2020/10/9 17:47
 * Description:TODO
 * *****************************************************
 */
public class LoadingDialog extends Dialog {

    TextView mTvMessage;

    public LoadingDialog(Context context) {
        super(context, R.style.ProgressDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置

        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading);
        mTvMessage = findViewById(R.id.tv_message);
    }

    public void setMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            mTvMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(msg);
        } else {
            mTvMessage.setVisibility(View.GONE);
        }

    }

}
