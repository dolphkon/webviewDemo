package com.guaguadev.webviewlibrary.utils;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;


import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * ****************************************************
 * Project: webviewProj
 * PackageName: com.jlpay.webview
 * ClassName: PaxWebChromeClient
 * Author: kongdexi
 * Date: 2020/7/9 19:34
 * Description:TODO
 * *****************************************************
 */
public class PaxWebChromeClient extends WebChromeClient {
    private final static int VIDEO_REQUEST = 0x11;
    private FragmentActivity mActivity;
    private Uri imageUri;
    private ValueCallback<Uri> mUploadMessage;//定义接受返回值
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private static final int PHOTO_REQUEST = 0x9001;
    public PaxWebChromeClient(@NonNull FragmentActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

    }


    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        mUploadMessage = uploadMsg;
//            take();
        checkPermition(acceptType);
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mUploadMessage = uploadMsg;
        checkPermition(acceptType);
    }

    // For Android 5.0+

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mUploadCallbackAboveL = filePathCallback;
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        for (int i = 0; i < acceptTypes.length; i++) {
            checkPermition(acceptTypes[i]);
        }
        return true;
    }

    private void checkPermition(final String accept) {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions
                .request(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE})
                .subscribe(granted -> {
                    if (granted) {
                        openFile(accept);
                    } else {
                        PermissionUtil.showCameraAndStorageNoti(mActivity);
                    }

                });
    }


    private void openFile(String accept) {
        if (accept.contains("image")) {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");        // Create the storage directory if it does not exist
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(mActivity,  mActivity.getPackageName()+".fileprovider", file);
            } else {
                imageUri = Uri.fromFile(file);
            }
            final List<Intent> cameraIntents = new ArrayList();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = mActivity.getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent i = new Intent(captureIntent);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                i.setPackage(packageName);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntents.add(i);

            }
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
            mActivity.startActivityForResult(chooserIntent, PHOTO_REQUEST);
        }else if (accept.contains("video")) {//     video/*
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // set the video file name
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
            //限制时长
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            //开启摄像机
            mActivity.startActivityForResult(intent, VIDEO_REQUEST);
        }
    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != PHOTO_REQUEST
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
            return;

        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }
        return;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VIDEO_REQUEST:
                    Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                    if (mUploadCallbackAboveL != null) {
                        if (resultCode == RESULT_OK) {
                            mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
                            mUploadCallbackAboveL = null;
                        } else {
                            mUploadCallbackAboveL.onReceiveValue(new Uri[]{});
                            mUploadCallbackAboveL = null;
                        }

                    } else if (mUploadMessage != null) {
                        if (resultCode == RESULT_OK) {
                            mUploadMessage.onReceiveValue(result);
                            mUploadMessage = null;
                        } else {
                            mUploadMessage.onReceiveValue(Uri.EMPTY);
                            mUploadMessage = null;
                        }
                    }

                    break;
                case PHOTO_REQUEST:
                    if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
                    Uri result_image = data == null || resultCode != RESULT_OK ? null : data.getData();
                    if (mUploadCallbackAboveL != null) {
                        onActivityResultAboveL(requestCode, resultCode, data);
                    } else if (mUploadMessage != null) {
                        if (result_image != null) {
                            String path = getPath(mActivity, result_image);
                            Uri uri = Uri.fromFile(new File(path));
                            mUploadMessage.onReceiveValue(uri);
                        } else {
                            mUploadMessage.onReceiveValue(imageUri);
                        }
                        mUploadMessage = null;
                    }
                    break;
                default:
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            } else if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            }
        }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }                // TODO handle non-primary volumes
            } else if ( isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}

