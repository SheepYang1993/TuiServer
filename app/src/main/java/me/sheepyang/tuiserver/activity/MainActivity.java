package me.sheepyang.tuiserver.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import me.sheepyang.tuiserver.activity.adv.AdvSettingActivity;
import me.sheepyang.tuiserver.activity.adv.ModifyAdvActivity;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.activity.sort.SortSettingActivity;
import me.sheepyang.tuiserver.adapter.SettingAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.entity.SettingEntity;
import me.sheepyang.tuiserver.utils.AppManager;
import me.sheepyang.tuiserver.utils.AppUtil;
import me.sheepyang.tuiserver.utils.UiUtils;

public class MainActivity extends BaseRefreshActivity {
    private static final int REQUEST_READ_PHONE_STATE = 200;//请求读取手机状态权限
    private Handler mHandler;
    private long exitTime = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("推-控制台");
        setBarBack(false);
        this.mHandler = new Handler();
        initData();
    }

    @Override
    public BaseQuickAdapter initAdapter() {
        return null;
    }

    private void initData() {
        AndPermission.with(mActivity)
                .requestCode(REQUEST_READ_PHONE_STATE)
                .permission(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .rationale((requestCode, rationale) -> {
                            // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(mActivity, rationale).show();
                        }
                )
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantedPermissions) {
                        // 权限申请成功回调。
                        // 这里的requestCode就是申请时设置的requestCode。
                        // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
                        if (requestCode == REQUEST_READ_PHONE_STATE) {
                            getPermissionSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                        // 权限申请失败回调。
                        if (requestCode == REQUEST_READ_PHONE_STATE) {
                            // 是否有不再提示并拒绝的权限。
                            if (AndPermission.hasAlwaysDeniedPermission(mActivity, deniedPermissions)) {
                                // 第二种：用自定义的提示语。
                                AndPermission.defaultSettingDialog(mActivity, REQUEST_READ_PHONE_STATE)
                                        .setTitle("权限申请失败")
                                        .setMessage("您拒绝了我们必要的一些权限，已经没法愉快的玩耍了，请在设置中开启权限！")
                                        .setPositiveButton("好，去设置")
                                        .setNegativeButton("不了", (DialogInterface dialog, int which) -> {
                                            dialog.dismiss();
                                            exitApp(exitTime);
                                        })
                                        .show();
                            }
                        }
                    }
                })
                .start();
    }

    @Override
    public void initRefreshLayout(TwinklingRefreshLayout refreshLayout) {
        super.initRefreshLayout(refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
    }

    private void getPermissionSuccess() {
        AppUtil.initBmob(mActivity, Constants.BMOB_APP_ID, "bmob");
        setAdapter(new SettingAdapter(getMainList()));
    }

    @Override
    public void initRecyclerView() {
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        UiUtils.configRecycleView(mRecyclerView, new LinearLayoutManager(mActivity));
    }

    private void exitApp(long delayMillis) {
        mHandler.postDelayed(() -> {
            AppManager.getAppManager().AppExit(getApplicationContext());
        }, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private List<SettingEntity> getMainList() {
        List<SettingEntity> list = new ArrayList<>();
        list.add(new SettingEntity("广告设置", AdvSettingActivity.class));
        list.add(new SettingEntity("图片分类设置", SortSettingActivity.class));
        list.add(new SettingEntity("广告图片设置2", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置3", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置4", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        list.add(new SettingEntity("广告图片设置5", ModifyAdvActivity.class));
        return list;
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Class clazz = ((SettingEntity) adapter.getData().get(position)).getClazz();
                if (clazz != null)
                    startActivity(new Intent(mActivity, clazz));
            }
        });
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {
        refreshLayout.finishLoadmore();
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        refreshLayout.finishRefreshing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                initData();
                break;
            }
        }
    }
}
