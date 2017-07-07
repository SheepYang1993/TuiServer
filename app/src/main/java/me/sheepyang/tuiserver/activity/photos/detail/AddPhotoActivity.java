package me.sheepyang.tuiserver.activity.photos.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiserver.adapter.AddPhotoDetailAdapter;
import me.sheepyang.tuiserver.app.Constants;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class AddPhotoActivity extends BaseRefreshActivity implements View.OnClickListener {
    private static final int TO_ADD_PHOTO = 0x001;
    private static final int TO_MODIFY_SORT = 0x002;
    private List<String> mDatas = new ArrayList<>();
    private int mPageSize = 10;
    private int mCurrentPage = 0;
    private Button mBtnSelectPhoto;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("添加图片");
        setBarRight("添加", (View v) -> {
            showMessage("确定添加吗");
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
//                ImageTypeEntity entity = (ImageTypeEntity) adapter.getData().get(position);
//                Intent intent = new Intent(mActivity, ModifySortActivity.class);
//                intent.putExtra(ModifySortActivity.TYPE, ModifySortActivity.TYPE_MODIFY);
//                intent.putExtra(ModifySortActivity.ENTITY_DATA, entity);
//                startActivityForResult(intent, TO_MODIFY_SORT);
//            }
//        });
//        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
//            @Override
//            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//                ImageTypeEntity entity = (ImageTypeEntity) adapter.getData().get(position);
//                new AlertDialog.Builder(mActivity)
//                        .setMessage("确定要删除 " + entity.getName() + " 这个分类吗？")
//                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
//                            deleteSortPic(entity);
//                        })
//                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
//                            dialog.dismiss();
//                        })
//                        .show();
//            }
//        });
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
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .selectionMedia(mImageSelectList)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .compressMaxKB(500)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
                .compressWH(1000, 1000) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
                .cropWH(1000, 1000)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
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
}
