package com.guaguadev.webviewlibrary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.guaguadev.webviewlibrary.R;
import com.guaguadev.webviewlibrary.dialog.CustomDialog;

import java.io.File;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.jlpay.webview.utils
 * ClassName: PermissionUtil
 * Author: kongdexi
 * Date: 2020/7/9 19:55
 * Description:TODO
 * *****************************************************
 */
public class PermissionUtil {
    public static void showCameraAndStorageNoti(final Context context){
        CustomDialog dialog = new CustomDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.dialog_tip))
                .setMessage(context.getResources().getString(R.string.you_need_those_permissions_above)+getAppName(context)+context.getResources().getString(R.string.permission_manage))
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                }).create(context, ContextCompat.getColor(context, R.color.common_tv_gold),ContextCompat.getColor(context, R.color.common_tv_gold));
        dialog.show();
    }

    /**
     * 获取应用名称
     */
    public static String getAppName(Context context) {
        String appName;
        PackageManager packManager = context.getPackageManager();
        ApplicationInfo appInfo = context.getApplicationInfo();
        appName = (String) packManager.getApplicationLabel(appInfo);
        return appName;
    }

    /**
     * 获取Uri
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriFromFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName()+ ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
