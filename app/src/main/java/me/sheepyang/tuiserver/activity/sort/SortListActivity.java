package me.sheepyang.tuiserver.activity.sort;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.adapter.SortAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.SortEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class SortListActivity extends BaseRefreshActivity {
    private static final int TO_ADD_SORT = 0x001;
    private static final int TO_MODIFY_SORT = 0x002;
    private List<SortEntity> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("分类设置");
        setBarRight("添加", (View v) -> {
            startActivityForResult(new Intent(mActivity, ModifySortActivity.class), TO_ADD_SORT);
        });
        mRefreshLayout.startRefresh();
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                SortEntity entity = (SortEntity) adapter.getData().get(position);
                Intent intent = new Intent(mActivity, ModifySortActivity.class);
                intent.putExtra(ModifySortActivity.TYPE, ModifySortActivity.TYPE_MODIFY);
                intent.putExtra(ModifySortActivity.ENTITY_DATA, entity);
                startActivityForResult(intent, TO_MODIFY_SORT);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                SortEntity entity = (SortEntity) adapter.getData().get(position);
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + entity.getName() + " 这个分类吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            deleteSortPic(entity);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void deleteSortPic(SortEntity entity) {
        BmobFile file = entity.getPic();
        if (entity.getPic() != null && !TextUtils.isEmpty(entity.getPic().getUrl())) {
            showDialog("正在删除封面...");
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        closeDialog();
                        deleteSortEntity(entity);
                    } else {
                        closeDialog();
                        if (151 == e.getErrorCode()) {
                            KLog.i(Constants.TAG, "找不到图片，删除失败");
                            deleteSortEntity(entity);
                        } else {
                            BmobExceptionUtil.handler(e);
                        }
                    }
                }
            });
            return;
        }
        deleteSortEntity(entity);
    }

    private void deleteSortEntity(final SortEntity entity) {
        showDialog("正在删除分类...");
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
        getSortList(1, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        getSortList(0, refreshLayout);
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    private void getSortList(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<SortEntity> query = new BmobQuery<SortEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(mPageSize);
        switch (type) {
            case 0://下拉刷新
                mCurrentPage = 0;
                break;
        }
        query.setSkip(mCurrentPage * mPageSize);
//        query.order("-updatedAt");
        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<SortEntity>() {

            @Override
            public void done(List<SortEntity> object, BmobException e) {
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
        return new SortAdapter(mDatas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_SORT:
            case TO_MODIFY_SORT:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }
}
