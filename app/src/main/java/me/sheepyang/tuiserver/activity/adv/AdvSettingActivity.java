package me.sheepyang.tuiserver.activity.adv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.socks.library.KLog;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.bmobentity.AdvEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class AdvSettingActivity extends BaseRefreshActivity {

    private static final int TO_ADD_ADV = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("广告设置");
        setBarRight("添加", (View v) -> {
            startActivityForResult(new Intent(mActivity, ModifyAdvActivity.class), TO_ADD_ADV);
        });
        mRefreshLayout.startRefresh();
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {
        showDialog();
        getAdvList(1, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        showDialog();
        getAdvList(0, refreshLayout);
    }

    private void getAdvList(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<AdvEntity> query = new BmobQuery<AdvEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<AdvEntity>() {

            @Override
            public void done(List<AdvEntity> object, BmobException e) {
                KLog.i("SheepYang", "done");
                if (e == null) {
                    if (object != null) {
                        KLog.i("SheepYang", object.size());
                    } else {
                        KLog.i("SheepYang", "object null");
                    }
                } else {
                    BmobExceptionUtil.handler(e);
                }

                closeDialog();
                switch (type) {
                    case 0:
                        refreshLayout.finishRefreshing();
                        break;
                    case 1:
                        refreshLayout.finishLoadmore();
                        break;
                }
            }
        });
    }

    @Override
    public RecyclerView.Adapter initAdapter() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_ADV:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }
}
