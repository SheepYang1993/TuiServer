package me.sheepyang.tuiserver.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;

import butterknife.ButterKnife;
import me.sheepyang.tuiserver.utils.AppManager;
import me.sheepyang.tuiserver.widget.dialog.LoadingDialog;

/**
 * Created by SheepYang on 2017-06-26.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public Activity mActivity;
    public LoadingDialog mDialog;

    public abstract
    @LayoutRes
    int setLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(mActivity);
    }

    public void showMessage(CharSequence charSequence) {
        ToastUtils.showShortToast(charSequence);
    }


    public void showDialog(String msg) {
        mDialog = new LoadingDialog(mActivity, msg);
        mDialog.show();
    }

    public void setDialogMessage(String msg) {
        if (mDialog != null) {
            mDialog.setMessage(msg);
        }
    }

    public void closeDialog() {
        if (mDialog != null) {
            mDialog.close();
        }
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.close();
            mDialog = null;
        }
        mActivity = null;
        AppManager.getAppManager().finishActivity();
        super.onDestroy();
    }
}
