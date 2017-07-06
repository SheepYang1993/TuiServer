package me.sheepyang.tuiserver.activity.model;

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
import me.sheepyang.tuiserver.activity.photos.bag.BagListActivity;
import me.sheepyang.tuiserver.adapter.ModelAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class ModelListActivity extends BaseRefreshActivity {
    private static final int TO_ADD_MODEL = 0x001;
    private static final int TO_MODIFY_MODEL = 0x002;
    private List<ModelEntity> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("模特列表");
        setBarRight("添加", (View v) -> {
            startActivityForResult(new Intent(mActivity, ModifyModelActivity.class), TO_ADD_MODEL);
        });
        mRefreshLayout.startRefresh();
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                ModelEntity entity = (ModelEntity) adapter.getData().get(position);
                Intent intent = new Intent(mActivity, ModifyModelActivity.class);
                intent.putExtra(ModifyModelActivity.TYPE, ModifyModelActivity.TYPE_MODIFY);
                intent.putExtra(ModifyModelActivity.ENTITY_DATA, entity);
                startActivityForResult(intent, TO_MODIFY_MODEL);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ModelEntity entity = (ModelEntity) adapter.getData().get(position);
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + entity.getNick() + " 这个模特吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            deleteModelPic(entity);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void deleteModelPic(ModelEntity entity) {
        BmobFile file = entity.getAvatar();
        if (entity.getAvatar() != null && !TextUtils.isEmpty(entity.getAvatar().getUrl())) {
            showDialog("正在删除模特图片...");
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        closeDialog();
                        deleteModelEntity(entity);
                    } else {
                        closeDialog();
                        if (151 == e.getErrorCode()) {
                            KLog.i(Constants.TAG, "找不到图片，删除失败");
                            deleteModelEntity(entity);
                        } else {
                            BmobExceptionUtil.handler(e);
                        }
                    }
                }
            });
            return;
        }
        deleteModelEntity(entity);
    }

    private void deleteModelEntity(final ModelEntity entity) {
        showDialog("正在删除模特...");
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
        getModelList(1, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        getModelList(0, refreshLayout);
    }

    @Override
    public void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    private void getModelList(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<ModelEntity> query = new BmobQuery<ModelEntity>();
        query.addWhereEqualTo("level", 2);
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
        query.findObjects(new FindListener<ModelEntity>() {

            @Override
            public void done(List<ModelEntity> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        KLog.i(Constants.TAG, object.size());
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
        return new ModelAdapter(mDatas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_MODEL:
            case TO_MODIFY_MODEL:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }
}
