package me.sheepyang.tuiserver.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import me.sheepyang.tuiserver.R;

/**
 * Created by SheepYang on 2017-06-27.
 */

public class LoadingDialog {
    private TextView mLoadingText;
    private String mMsg;
    AVLoadingIndicatorView mLoadingView;
    Dialog mLoadingDialog;

    public LoadingDialog(Context context, String msg) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        mLoadingView = (AVLoadingIndicatorView) view.findViewById(R.id.lv_circularring);
        // 页面中显示文本
        mLoadingText = (TextView) view.findViewById(R.id.loading_text);
        // 显示文本
        mMsg = msg;
        mLoadingText.setText(mMsg);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void show() {
        mLoadingDialog.show();
        mLoadingView.smoothToShow();
    }

    public void close() {
        close(false);
    }

    public void close(boolean isDestroy) {
        if (mLoadingDialog != null && mLoadingView != null) {
            mLoadingView.smoothToHide();
            mLoadingDialog.dismiss();
            if (isDestroy) {
                mLoadingDialog = null;
                mLoadingView = null;
            }
        }
    }

    public void setMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            mMsg = msg;
            if (mLoadingText != null) {
                mLoadingText.setText(mMsg);
            }
        }
    }
}
