package com.guaguadev.webviewdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.guaguadev.webviewlibrary.WebviewManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_01)
    Button btn01;
    @BindView(R.id.btn_02)
    Button btn02;
    @BindView(R.id.btn_03)
    Button btn03;
    @BindView(R.id.btn_04)
    Button btn04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_01, R.id.btn_02, R.id.btn_03, R.id.btn_04})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_01:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/"));
                startActivity(intent);
                break;
            case R.id.btn_02:
                WebviewManager.with(this).setTitle("加载网页").setUrl("http://jqweui.com/dist/demos/form.html").load();
                break;
            case R.id.btn_03:
                WebviewManager.with(this).setTitle("加载网页").setUrl("https://loan-dev.jlpay.com/jlcard/businessLoan").load();
                break;
            case R.id.btn_04:
                break;
        }
    }
}
