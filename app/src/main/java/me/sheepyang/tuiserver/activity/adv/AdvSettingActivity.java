package me.sheepyang.tuiserver.activity.adv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.adapter.AdvAdapter;
import me.sheepyang.tuiserver.bmobentity.AdvEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class AdvSettingActivity extends BaseRefreshActivity {

    private static final int TO_ADD_ADV = 0x001;
    private static final int TO_MODIFY_ADV = 0x002;
    private List<AdvEntity> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;

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
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                AdvEntity entity = (AdvEntity) adapter.getData().get(position);
                Intent intent = new Intent(mActivity, ModifyAdvActivity.class);
                intent.putExtra(ModifyAdvActivity.TYPE, ModifyAdvActivity.TYPE_MODIFY);
                intent.putExtra(ModifyAdvActivity.ENTITY_DATA, entity);
                startActivityForResult(intent, TO_MODIFY_ADV);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                AdvEntity entity = (AdvEntity) adapter.getData().get(position);
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + entity.getTitle() + " 这条广告吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            deleteAdvPic(entity);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void deleteAdvPic(AdvEntity entity) {
        BmobFile file = entity.getPic();
        if (entity.getPic() != null && !TextUtils.isEmpty(entity.getPic().getUrl())) {
            showDialog("正在删除广告图片...");
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        closeDialog();
                        deleteAdvEntity(entity);
                    } else {
                        closeDialog();
                        BmobExceptionUtil.handler(e);
                    }
                }
            });
            return;
        }
        deleteAdvEntity(entity);
    }

    private void deleteAdvEntity(final AdvEntity entity) {
        showDialog("正在删除广告...");
        entity.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    closeDialog();
                    showMessage("删除成功");
                    mDatas.remove(entity);
                    mAdapter.setNewData(mDatas);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        getAdvList(1, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        getAdvList(0, refreshLayout);
    }

    @Override
    public void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    private void getAdvList(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<AdvEntity> query = new BmobQuery<AdvEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(mPageSize);
        switch (type) {
            case 0://下拉刷新
                mCurrentPage = 0;
                break;
        }
        query.setSkip(mCurrentPage * mPageSize);
        query.order("-updatedAt");
        //执行查询方法
        query.findObjects(new FindListener<AdvEntity>() {

            @Override
            public void done(List<AdvEntity> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        switch (type) {
                            case 0://下拉刷新
                                mDatas = object;
                                break;
                            case 1://上拉加载更多
                                mDatas.addAll(object);
                                break;
                        }
                        mAdapter.setNewData(mDatas);
                        mCurrentPage++;
                    } else {
                        switch (type) {
                            case 0://下拉刷新
                                mDatas.clear();
                                mAdapter.setNewData(mDatas);
                                showMessage(getString(R.string.no_data));
                                break;
                            case 1://上拉加载更多
                                showMessage(getString(R.string.no_more_data));
                                break;
                        }
                    }
                } else {
                    closeDialog();
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
    public BaseQuickAdapter initAdapter() {
        return new AdvAdapter(mDatas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_ADV:
            case TO_MODIFY_ADV:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }
}
