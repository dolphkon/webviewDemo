package com.guaguadev.webviewlibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.guaguadev.webviewlibrary.R;

/**
 * ****************************************************
 * Project: webviewDemo
 * PackageName: com.guaguadev.webviewlibrary.utils
 * ClassName: CustomDialog
 * Author: kongdexi
 * Date: 2020/10/09 17:34
 * Description:自定义对话框dialog(确认对话框/提示对话框)
 * *****************************************************
 */
public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private Drawable drawable;
        private int gravity = Gravity.CENTER;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setDrawable(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }


        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }


        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }


        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText){
            this.positiveButtonText=(String)context.getText(positiveButtonText);
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**  positiveTextColor:确认按钮的颜色
         *   negativeTextColor取消按钮的颜色
         *   PS：当取消按钮文字为null时，默认negativeTextColor传0
         * */
        public CustomDialog create(Context context,int positiveTextColor,int negativeTextColor) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialog);
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setCanceledOnTouchOutside(false);

            ((TextView) layout.findViewById(R.id.title)).setText(title);
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positive_button)).setText(positiveButtonText);
                ((Button) layout.findViewById(R.id.positive_button)).setTextColor(positiveTextColor);
                layout.findViewById(R.id.positive_button).setOnClickListener((view)->{
                    if (positiveButtonClickListener!=null){
                        positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }else {
                        dialog.dismiss();
                    }
                });
            }
            if (negativeButtonText != null) {
                layout.findViewById(R.id.negative_button).setSelected(true);
                ((Button) layout.findViewById(R.id.negative_button)).setText(negativeButtonText);
                ((Button) layout.findViewById(R.id.negative_button)).setTextColor(negativeTextColor);

                layout.findViewById(R.id.negative_button).setOnClickListener((view) -> {
                    if (negativeButtonClickListener!=null){
                        negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                    }else{
                        dialog.dismiss();
                    }
                });
            }else {
                layout.findViewById(R.id.negative_button).setVisibility(View.GONE);
            }
            if (positiveButtonText != null && negativeButtonText != null) {
                layout.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            } else {
                layout.findViewById(R.id.divider).setVisibility(View.GONE);
            }
            if (positiveButtonText == null && negativeButtonText == null) {
                layout.findViewById(R.id.view_buttom).setVisibility(View.GONE);
                layout.findViewById(R.id.ll_buttom).setVisibility(View.GONE);
            }
            if (positiveButtonText == null && negativeButtonText == null) {
                layout.findViewById(R.id.view_buttom).setVisibility(View.GONE);
                layout.findViewById(R.id.ll_buttom).setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(title)) {
                layout.findViewById(R.id.title).setVisibility(View.GONE);
            }

            ImageView img = layout.findViewById(R.id.imageview);
            if (drawable != null) {
                img.setVisibility(View.VISIBLE);
                img.setImageDrawable(drawable);
            }
            if (message != null) {
                TextView msgTv = layout.findViewById(R.id.message);
                msgTv.setGravity(gravity);
                msgTv.setText(message);

                if (TextUtils.isEmpty(title)) {
                    msgTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                    msgTv.setTextColor(ContextCompat.getColor(context,R.color.common_tv_black));
                }

            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(
                        contentView, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);

    }
}

