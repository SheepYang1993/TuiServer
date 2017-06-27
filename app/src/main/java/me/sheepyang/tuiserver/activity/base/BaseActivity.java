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

    @Override
    protected void onDestroy() {
        mActivity = null;
        mDialog = null;
        super.onDestroy();
    }
}
