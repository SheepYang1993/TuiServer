package me.sheepyang.tuiserver.activity.photos.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.adapter.AddPhotoDetailAdapter;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoDetailEntity;
import me.sheepyang.tuiserver.model.bmobentity.SortEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;
import static com.luck.picture.lib.tools.DebugUtil.log;

public class AddPhotoActivity extends BaseRefreshActivity implements View.OnClickListener {
    private static final int TO_ADD_PHOTO = 0x001;
    private static final int TO_MODIFY_SORT = 0x002;
    public static final String PHOTO_BAG_ENTITY_DATA = "photo_bag_entity_data";
    public static final String MODEL_ENTITY_DATA = "model_entity_data";
    public static final String SORT_ENTITY_DATA = "sort_entity_data";

    private List<String> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;
    private Button mBtnSelectPhoto;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private PhotoBagEntity mPhotoBagEntity;
    private ModelEntity mModelEntity;
    private SortEntity mSortEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("添加图片");
        setBarRight("添加", (View v) -> {
            new AlertDialog.Builder(mActivity)
                    .setMessage("确定要添加图片吗？")
                    .setPositiveButton("添加", (DialogInterface dialog, int which) -> {
                        upLoadPic();
                    })
                    .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
        initIntent(getIntent());
    }

    private void initIntent(Intent intent) {
        mModelEntity = (ModelEntity) intent.getSerializableExtra(MODEL_ENTITY_DATA);
        mSortEntity = (SortEntity) intent.getSerializableExtra(SORT_ENTITY_DATA);
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
        if (mSortEntity == null) {
            showMessage("找不到分类信息");
            onBackPressed();
            return;
        }
    }

    private void upLoadPic() {
        if (mDatas == null || mDatas.size() <= 0) {
            showMessage("请先选择图片~");
            return;
        }
        //详细示例可查看BmobExample工程中BmobFileActivity类
        String[] filePaths = new String[mDatas.size()];
        mDatas.toArray(filePaths);
        showDialog("正在上传图片...");
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                    addPhotos(files);
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                BmobExceptionUtil.handler(new BmobException(statuscode, errormsg));
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
                setDialogMessage("正在上传图片(" + curIndex + "/" + total + ")...");
            }
        });
    }

    private void addPhotos(List<BmobFile> files) {
        setDialogMessage("正在创建图片信息...");
        List<BmobObject> photoList = new ArrayList<BmobObject>();
        for (BmobFile file :
                files) {
            PhotoDetailEntity photo = new PhotoDetailEntity();
            photo.setPhotoBag(mPhotoBagEntity);
            photo.setModel(mModelEntity);
            photo.setSort(mSortEntity);
            photo.setPic(file);
            photoList.add(photo);
        }
        new BmobBatch().insertBatch(photoList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    boolean isSuccess = true;
                    BmobException eeee = null;
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            log("第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                        } else {
                            isSuccess = false;
                            eeee = ex;
                            break;
                        }
                    }
                    if (isSuccess) {
                        modifyPhotoBagNum(o.size());
                    } else {
                        closeDialog();
                        BmobExceptionUtil.handler(eeee);
                    }
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void modifyPhotoBagNum(int size) {
        if (mPhotoBagEntity.getPhotoNum() != null) {
            size += mPhotoBagEntity.getPhotoNum();
        }
        mPhotoBagEntity.setPhotoNum(size);
//        photoBagEntity.increment("photoNum", size);
        setDialogMessage("正在修改套图数据...");
        int finalSize = size;
        KLog.i(Constants.TAG, "PhotoBagEntity.getObjectId:" + mPhotoBagEntity.getObjectId());
        mPhotoBagEntity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    modifySortNum(finalSize);
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void modifySortNum(int size) {
        if (mSortEntity.getNum() != null) {
            size += mSortEntity.getNum();
        }
//        SortEntity sortEntity = new SortEntity();
//        sortEntity.increment("num", size);
        mSortEntity.setNum(size);
        setDialogMessage("正在修改分类数据...");
        KLog.i(Constants.TAG, "SortEntity.getObjectId:" + mSortEntity.getObjectId());
        mSortEntity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("添加成功");
                    setResult(RESULT_OK);
                    mBtnSelectPhoto.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    @Override
    public void initRefreshLayout(TwinklingRefreshLayout refreshLayout) {
        super.initRefreshLayout(refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
    }

    @Override
    public void initListener() {
        super.initListener();
//        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
//            @Override
//            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                SortEntity entity = (SortEntity) adapter.getData().get(position);
//                Intent intent = new Intent(mActivity, ModifySortActivity.class);
//                intent.putExtra(ModifySortActivity.TYPE, ModifySortActivity.TYPE_MODIFY);
//                intent.putExtra(ModifySortActivity.ENTITY_DATA, entity);
//                startActivityForResult(intent, TO_MODIFY_SORT);
//            }
//        });
        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + position + " 这站图片吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            deletePic(position);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void deletePic(int position) {
        if (mDatas != null && mDatas.size() >= position) {
            mDatas.remove(position);
            mImageSelectList.remove(position);
        }
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {

    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {

    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    @Override
    public BaseQuickAdapter initAdapter() {
        mAdapter = new AddPhotoDetailAdapter(mDatas);
        mBtnSelectPhoto = new Button(mActivity);
        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        mBtnSelectPhoto.setText("选择图片");
        mAdapter.addHeaderView(mBtnSelectPhoto);
        return mAdapter;
    }

    private void selectPhoto() {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(mActivity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.mypicture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(50)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath(Constants.IMAGE_FILE_PATH)// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                        .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .selectionMedia(mImageSelectList)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
//                .compressMaxKB(500)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
//                .compressWH(1000, 1000) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
//                .cropWH(1000, 1000)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
//                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
//                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
//                        .videoQuality()// 视频录制质量 0 or 1 int
//                        .videoSecond()// 显示多少秒以内的视频or音频也可适用 int
//                        .recordVideoSecond()//视频秒数录制 默认60s int
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
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

            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    // 图片选择结果回调
                    mImageSelectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (mImageSelectList != null && mImageSelectList.size() > 0) {
                        mDatas.clear();
                        for (LocalMedia media :
                                mImageSelectList) {
                            KLog.i(Constants.TAG, media.getCompressPath());
                            mDatas.add(media.getCompressPath());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(mActivity);
        super.onDestroy();
    }
}
