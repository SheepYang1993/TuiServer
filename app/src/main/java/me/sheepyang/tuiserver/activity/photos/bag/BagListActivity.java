package me.sheepyang.tuiserver.activity.photos.bag;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
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
import me.sheepyang.tuiserver.adapter.PhotoBagAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class BagListActivity extends BaseRefreshActivity {
    private static final int TO_ADD_PHOTO_BAG = 0x001;
    private static final int TO_MODIFY_PHOTO_BAG = 0x002;
    public static final String ENTITY_DATA = "entity_data";
    private List<PhotoBagEntity> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;
    private ModelEntity mModelEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("套图详情");
        setBarRight("添加", (View v) -> {
            Intent intent = new Intent(mActivity, ModifyBagActivity.class);
            intent.putExtra(ModifyBagActivity.ENTITY_DATA, mModelEntity);
            startActivityForResult(intent, TO_ADD_PHOTO_BAG);
        });
        mModelEntity = (ModelEntity) getIntent().getSerializableExtra(ENTITY_DATA);
        if (mModelEntity == null) {
            showMessage("找不到模特信息");
            onBackPressed();
            return;
        }
        mRefreshLayout.startRefresh();
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                PhotoBagEntity entity = (PhotoBagEntity) adapter.getData().get(position);
                Intent intent = new Intent(mActivity, ModifyBagActivity.class);
                intent.putExtra(ModifyBagActivity.TYPE, ModifyBagActivity.TYPE_MODIFY);
                intent.putExtra(ModifyBagActivity.ENTITY_DATA, entity);
                startActivityForResult(intent, TO_MODIFY_PHOTO_BAG);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                PhotoBagEntity entity = (PhotoBagEntity) adapter.getData().get(position);
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + entity.getTitle() + " 这套套图吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            deletePhotoBagPic(entity);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void deletePhotoBagPic(PhotoBagEntity entity) {
        BmobFile file = entity.getCoverPic();
        if (entity.getCoverPic() != null && !TextUtils.isEmpty(entity.getCoverPic().getUrl())) {
            showDialog("正在删除套图图片...");
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        closeDialog();
                        deletePhotoBagEntity(entity);
                    } else {
                        closeDialog();
                        if (151 == e.getErrorCode()) {
                            KLog.i(Constants.TAG, "找不到图片，删除失败");
                            deletePhotoBagEntity(entity);
                        } else {
                            BmobExceptionUtil.handler(e);
                        }
                    }
                }
            });
            return;
        }
        deletePhotoBagEntity(entity);
    }

    private void deletePhotoBagEntity(final PhotoBagEntity entity) {
        showDialog("正在删除套图信息...");
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
        getPhotoBagList(1, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        getPhotoBagList(0, refreshLayout);
    }

    @Override
    public void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    private void getPhotoBagList(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<PhotoBagEntity> query = new BmobQuery<PhotoBagEntity>();
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
        query.findObjects(new FindListener<PhotoBagEntity>() {

            @Override
            public void done(List<PhotoBagEntity> object, BmobException e) {
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
                    mDatas.clear();
                    mDatas.add(new PhotoBagEntity());
                    mAdapter.setNewData(mDatas);
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
        return new PhotoBagAdapter(mDatas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_PHOTO_BAG:
            case TO_MODIFY_PHOTO_BAG:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }
}
