package me.sheepyang.tuiserver.activity.photos.detail;

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
import me.sheepyang.tuiserver.activity.sort.ModifySortActivity;
import me.sheepyang.tuiserver.adapter.PhotoDetailAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ImageTypeEntity;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoDetailEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

public class PhotoDetailListActivity extends BaseRefreshActivity implements View.OnClickListener {
    private static final int TO_ADD_PHOTO = 0x001;
    private static final int TO_MODIFY_SORT = 0x002;
    public static final String PHOTO_BAG_ENTITY_DATA = "photo_bag_entity_data";
    public static final String MODEL_ENTITY_DATA = "model_entity_data";
    private List<PhotoDetailEntity> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;
    private PhotoBagEntity mPhotoBagEntity;
    private ModelEntity mModelEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("图片详情");
        setBarRight("添加", (View v) -> {
            Intent intent = new Intent(mActivity, AddPhotoActivity.class);
            intent.putExtra(AddPhotoActivity.MODEL_ENTITY_DATA, mModelEntity);
            intent.putExtra(AddPhotoActivity.PHOTO_BAG_ENTITY_DATA, mPhotoBagEntity);
            startActivityForResult(intent, TO_ADD_PHOTO);
        });
        initIntent(getIntent());
        mRefreshLayout.startRefresh();
    }

    private void initIntent(Intent intent) {
        mModelEntity = (ModelEntity) intent.getSerializableExtra(MODEL_ENTITY_DATA);
        mPhotoBagEntity = (PhotoBagEntity) intent.getSerializableExtra(PHOTO_BAG_ENTITY_DATA);
        if (mModelEntity == null) {
            showMessage("找不到模特信息");
            onBackPressed();
            return;
        }
        if (mPhotoBagEntity == null) {
            showMessage("找不到套图信息");
            onBackPressed();
            return;
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImageTypeEntity entity = (ImageTypeEntity) adapter.getData().get(position);
                Intent intent = new Intent(mActivity, ModifySortActivity.class);
                intent.putExtra(ModifySortActivity.TYPE, ModifySortActivity.TYPE_MODIFY);
                intent.putExtra(ModifySortActivity.ENTITY_DATA, entity);
                startActivityForResult(intent, TO_MODIFY_SORT);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ImageTypeEntity entity = (ImageTypeEntity) adapter.getData().get(position);
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

    private void deleteSortPic(ImageTypeEntity entity) {
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

    private void deleteSortEntity(final ImageTypeEntity entity) {
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
        BmobQuery<PhotoDetailEntity> query = new BmobQuery<PhotoDetailEntity>();
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
        query.findObjects(new FindListener<PhotoDetailEntity>() {

            @Override
            public void done(List<PhotoDetailEntity> object, BmobException e) {
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
        return new PhotoDetailAdapter(mDatas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_ADD_PHOTO:
            case TO_MODIFY_SORT:
                if (resultCode == RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
