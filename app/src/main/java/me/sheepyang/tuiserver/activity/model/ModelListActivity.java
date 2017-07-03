package me.sheepyang.tuiserver.activity.model;

import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;

public class ModelListActivity extends BaseRefreshActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("模特列表");
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {

    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {

    }

    @Override
    public BaseQuickAdapter initAdapter() {
        return null;
    }
}
