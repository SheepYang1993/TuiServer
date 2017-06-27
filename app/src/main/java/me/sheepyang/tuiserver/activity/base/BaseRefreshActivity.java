package me.sheepyang.tuiserver.activity.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import butterknife.BindView;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.widget.QBar;
import me.sheepyang.tuiserver.widget.dialog.LoadingDialog;

public abstract class BaseRefreshActivity extends BaseActivity {

    @BindView(R.id.q_bar)
    QBar mQBar;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    protected TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView.Adapter mAdapter;

    @Override
    public int setLayoutId() {
        return R.layout.activity_base_refresh;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    public void setBarBack(boolean isBack) {
        mQBar.setBack(isBack);
    }

    public void showDialog() {
        mDialog = new LoadingDialog(mActivity, "玩命加载中...");
        mDialog.show();
    }

    public void closeDialog() {
        mDialog.close();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                startRefresh(refreshLayout);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                startLoadMore(refreshLayout);
            }
        });
    }

    protected abstract void startLoadMore(TwinklingRefreshLayout refreshLayout);

    protected abstract void startRefresh(TwinklingRefreshLayout refreshLayout);

    public void setBarTitle(String title) {
        mQBar.setTitle(title);
    }

    private void initView() {
        initRefreshLayout(mRefreshLayout);
        initRecyclerView();
        setAdapter(initAdapter());
    }

    public void initRefreshLayout(TwinklingRefreshLayout refreshLayout) {
        setRefreshHeadBottomView(refreshLayout);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public abstract RecyclerView.Adapter initAdapter();

    public void setRefreshHeadBottomView(TwinklingRefreshLayout refreshLayout) {
        setRefreshHeadBottomView(refreshLayout, null, null);
    }

    public void setBarRight(String text, View.OnClickListener onClickListener) {
        mQBar.setRightText(text);
        mQBar.setOnRightClickListener(onClickListener);
    }

    public void setRefreshHeadBottomView(TwinklingRefreshLayout refreshLayout, IHeaderView headerView, IBottomView bottomView) {
        if (headerView == null) {
            headerView = new SinaRefreshView(mActivity);
            ((SinaRefreshView) headerView).setArrowResource(R.drawable.ico_pink_arrow);
        }
        if (bottomView == null) {
            bottomView = new LoadingView(mActivity);
        }
        refreshLayout.setBottomView(bottomView);
        refreshLayout.setHeaderView(headerView);
    }
}
