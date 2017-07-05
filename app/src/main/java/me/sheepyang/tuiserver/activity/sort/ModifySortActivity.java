package me.sheepyang.tuiserver.activity.sort;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.KeyboardUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseActivity;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ImageTypeEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;
import me.sheepyang.tuiserver.widget.QBar;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class ModifySortActivity extends BaseActivity implements View.OnClickListener {

    public static final String TYPE_MODIFY = "type_modify";
    public static final String TYPE_ADD = "type_add";
    public static final String TYPE = "type";
    public static final String ENTITY_DATA = "entity_data";

    @BindView(R.id.QBar)
    QBar mQBar;
    @BindView(R.id.edt_name)
    EditText mEdtName;
    @BindView(R.id.edt_desc)
    EditText mEdtDesc;
    @BindView(R.id.rbtn_habit_all)
    RadioButton mRbtnHabitAll;
    @BindView(R.id.rbtn_habit_man)
    RadioButton mRbtnHabitMan;
    @BindView(R.id.rbtn_habit_woman)
    RadioButton mRbtnHabitWoman;
    @BindView(R.id.rg_habit)
    RadioGroup mRgHabit;
    @BindView(R.id.cb_is_vip)
    CheckBox mCbIsVip;
    @BindView(R.id.cb_is_blur)
    CheckBox mCbIsBlur;
    @BindView(R.id.cb_is_show)
    CheckBox mCbIsShow;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.btn_model_list)
    Button mBtnModelList;
    private RequestOptions mOptions;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private String mType;
    private ImageTypeEntity mSortEntity;
    private boolean mIsNeedDeleteBmobImage;

    @Override
    public int setLayoutId() {
        return R.layout.activity_modify_sort;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    private void initIntent(Intent intent) {
        mType = intent.getStringExtra(TYPE);
        if (TextUtils.isEmpty(mType)) {
            mType = TYPE_ADD;
        }
        if (TYPE_MODIFY.equals(mType)) {
            mSortEntity = (ImageTypeEntity) intent.getSerializableExtra(ENTITY_DATA);
            if (mSortEntity == null) {
                showMessage("找不到分类信息");
                onBackPressed();
                return;
            }
            KLog.i(Constants.TAG
                    , "名称:" + mSortEntity.getName()
                    , "描述:" + mSortEntity.getDesc()
                    , "针对:" + mSortEntity.getHabit()
                    , "是否VIP:" + mSortEntity.getVip()
                    , "是否模糊:" + mSortEntity.getBlur()
                    , "是否立即显示:" + mSortEntity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
            );
            mQBar.setTitle("修改分类");
            mQBar.setRightText("修改");
        }
    }

    private void initData() {
        if (mSortEntity != null) {
            mEdtName.setText(mSortEntity.getName());
            mEdtDesc.setText(mSortEntity.getDesc());
            switch (mSortEntity.getHabit()) {
                case 0://全部
                    mRgHabit.check(R.id.rbtn_habit_all);
                    break;
                case 1://男生
                    mRgHabit.check(R.id.rbtn_habit_man);
                    break;
                case 2://女生
                    mRgHabit.check(R.id.rbtn_habit_woman);
                    break;
                default://全部
                    mRgHabit.check(R.id.rbtn_habit_all);
                    break;
            }
            mCbIsVip.setChecked(mSortEntity.getVip());
            mCbIsBlur.setChecked(mSortEntity.getBlur());
            mCbIsShow.setChecked(mSortEntity.getShow());

            if (mSortEntity.getPic() != null && !TextUtils.isEmpty(mSortEntity.getPic().getFileUrl())) {
                Glide.with(mActivity)
                        .load(mSortEntity.getPic().getFileUrl())
                        .apply(mOptions)
                        .into(mIvImage);
            }
        }
    }

    private void initListener() {
        mIvImage.setOnLongClickListener((View v) -> {
            if (mImageSelectList != null && mImageSelectList.size() > 0) {
                String msg = "";
                if (!TextUtils.isEmpty(mEdtName.getText().toString())) {
                    msg = mEdtName.getText().toString();
                } else if (mSortEntity != null) {
                    msg = mSortEntity.getName();
                }
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + msg + " 这张封面吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            showDialog("正在删除封面");
                            mImageSelectList = new ArrayList<LocalMedia>();
                            Glide.with(mActivity)
                                    .load("")
                                    .apply(mOptions)
                                    .into(mIvImage);
                            closeDialog();
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else if (mSortEntity != null && mSortEntity.getPic() != null && !TextUtils.isEmpty(mSortEntity.getPic().getFileUrl())) {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + mSortEntity.getName() + " 这张封面吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            mIsNeedDeleteBmobImage = true;
                            Glide.with(mActivity)
                                    .load("")
                                    .apply(mOptions)
                                    .into(mIvImage);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
            return true;
        });
        if (TYPE_MODIFY.equals(mType)) {
            mQBar.setOnRightClickListener((View v) -> {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要修改分类吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                KLog.e();
                                modifySort(mSortEntity);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            });
        } else {
            mQBar.setOnRightClickListener((View v) -> {
                addSort();
            });
        }
    }

    private void modifySort(ImageTypeEntity entity) {
        KLog.e();
        KeyboardUtils.hideSoftInput(mActivity);
        if (TextUtils.isEmpty(mEdtName.getText().toString())) {
            showMessage("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(mEdtDesc.getText().toString())) {
            showMessage("请输入描述");
            return;
        }
        entity.setName(mEdtName.getText().toString());
        entity.setDesc(mEdtDesc.getText().toString());
        int habit;//针对
        switch (mRgHabit.getCheckedRadioButtonId()) {
            case R.id.rbtn_habit_all://全部
                habit = 0;
                break;
            case R.id.rbtn_habit_man://男生
                habit = 1;
                break;
            case R.id.rbtn_habit_woman://女生
                habit = 2;
                break;
            default:
                habit = 0;//全部
                break;
        }
        entity.setHabit(habit);
        entity.setVip(mCbIsVip.isChecked());
        entity.setBlur(mCbIsBlur.isChecked());
        entity.setShow(mCbIsShow.isChecked());

        KLog.e();
        modifySortImage(entity, new OnModifySortImageListener() {
            @Override
            public void onSuccess(ImageTypeEntity entity) {
                KLog.e();
                updateSortEntity(entity);
            }

            @Override
            public void onError(BmobException e) {
                KLog.e();
                closeDialog();
                BmobExceptionUtil.handler(e);
            }
        });
    }

    private void deleteSortPic(ImageTypeEntity entity, UpdateListener listener) {
        BmobFile file = new BmobFile();
        file.setUrl(entity.getPic().getUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        KLog.i(entity.getPic().getUrl());
        if (entity.getPic() != null && !TextUtils.isEmpty(entity.getPic().getUrl())) {
            file.delete(listener);
        }
    }

    private void updateSortEntity(ImageTypeEntity entity) {
        KLog.e();
        KLog.i(Constants.TAG
                , "名称:" + entity.getName()
                , "描述:" + entity.getDesc()
                , "针对:" + entity.getHabit()
//                , "模特id:" + mEdtTitle.getText().toString()
                , "是否VIP:" + mSortEntity.getVip()
                , "是否模糊:" + mSortEntity.getBlur()
                , "是否立即显示:" + mSortEntity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
        );
        showDialog("正在修改分类...");
        if (entity.getPic() == null || TextUtils.isEmpty(entity.getPic().getFileUrl())) {
            entity.remove("pic");
        }
        entity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("分类修改成功");
                    setResult(RESULT_OK);
                    mIvImage.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }


    private void modifySortImage(ImageTypeEntity entity, OnModifySortImageListener listener) {
        KLog.e();
        if (entity.getPic() == null || TextUtils.isEmpty(entity.getPic().getFileUrl())) {//服务器无图
            KLog.e();
            // 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            if (mImageSelectList != null && mImageSelectList.size() > 0) {
                LocalMedia media = mImageSelectList.get(0);
                if (media != null && media.isCompressed()) {
                    KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                    BmobFile bmobFile = new BmobFile(new File(media.getCompressPath()));
                    showDialog("正在上传封面...");
                    uploadPic(bmobFile, new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                closeDialog();
                                KLog.e();
                                entity.setPic(bmobFile);
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                                listener.onSuccess(entity);
                            } else {
                                listener.onError(e);
                            }
                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                        }
                    });
                    return;
                } else {
                    BmobException e = new BmobException(500, "图片压缩失败，请重新选择图片");
                    mImageSelectList = new ArrayList<>();
                    listener.onError(e);
                    return;
                }
            }
            KLog.e();
            entity.setPic(null);
            listener.onSuccess(entity);
        } else {//服务器已经有图
            KLog.e();
            if (mIsNeedDeleteBmobImage) {
                mIsNeedDeleteBmobImage = false;
                showDialog("正在修改封面...");
                deleteSortPic(entity, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 例如 LocalMedia 里面返回三种path
                            // 1.media.getPath(); 为原图path
                            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                            if (mImageSelectList != null && mImageSelectList.size() > 0) {
                                LocalMedia media = mImageSelectList.get(0);
                                if (media != null && media.isCompressed()) {
                                    KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                                    BmobFile bmobFile = new BmobFile(new File(media.getCompressPath()));
                                    uploadPic(bmobFile, new UploadFileListener() {

                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                closeDialog();
                                                KLog.e();
                                                entity.setPic(bmobFile);
                                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                                KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                                                listener.onSuccess(entity);
                                            } else {
                                                listener.onError(e);
                                            }
                                        }

                                        @Override
                                        public void onProgress(Integer value) {
                                            // 返回的上传进度（百分比）
                                        }
                                    });
                                    return;
                                } else {
                                    BmobException e1 = new BmobException(500, "图片压缩失败，请重新选择图片");
                                    mImageSelectList = new ArrayList<>();
                                    listener.onError(e1);
                                    return;
                                }
                            }
                            KLog.e();
                            entity.setPic(null);
                            listener.onSuccess(entity);
                        } else {
                            listener.onError(e);
                        }
                    }
                });
            } else {
                KLog.e();
                listener.onSuccess(entity);
            }
        }
    }

    private interface OnModifySortImageListener {
        void onSuccess(ImageTypeEntity entity);

        void onError(BmobException e);
    }

    private void addSort() {
        KeyboardUtils.hideSoftInput(mActivity);
        if (TextUtils.isEmpty(mEdtName.getText().toString())) {
            showMessage("请输入名称");
            return;
        }
        if (TextUtils.isEmpty(mEdtDesc.getText().toString())) {
            showMessage("请输入描述");
            return;
        }
        ImageTypeEntity entity = new ImageTypeEntity();
        entity.setName(mEdtName.getText().toString());
        entity.setDesc(mEdtDesc.getText().toString());

        int habit;//针对
        switch (mRgHabit.getCheckedRadioButtonId()) {
            case R.id.rbtn_habit_all://全部
                habit = 0;
                break;
            case R.id.rbtn_habit_man://男生
                habit = 1;
                break;
            case R.id.rbtn_habit_woman://女生
                habit = 2;
                break;
            default:
                habit = 0;//全部
                break;
        }
        entity.setHabit(habit);
        entity.setShow(mCbIsShow.isChecked());
        entity.setVip(mCbIsVip.isChecked());
        entity.setBlur(mCbIsBlur.isChecked());


        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
        if (mImageSelectList != null && mImageSelectList.size() > 0) {
            LocalMedia media = mImageSelectList.get(0);
            if (media != null && media.isCompressed()) {
                KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                BmobFile bmobFile = new BmobFile(new File(media.getCompressPath()));
                entity.setPic(bmobFile);
                showDialog("正在上传封面...");
                uploadPic(bmobFile, new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            closeDialog();
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                            saveSortEntity(entity);
                        } else {
                            closeDialog();
                            BmobExceptionUtil.handler(e);
                        }
                    }

                    @Override
                    public void onProgress(Integer value) {
                        // 返回的上传进度（百分比）
                    }
                });
                return;
            } else {
                showMessage("图片选取失败，请重新选择图片");
                mImageSelectList = new ArrayList<>();
                return;
            }
        }
        saveSortEntity(entity);
    }

    private void uploadPic(BmobFile bmobFile, UploadFileListener listener) {
        bmobFile.uploadblock(listener);
    }

    private void saveSortEntity(ImageTypeEntity entity) {
        KLog.i(Constants.TAG
                , "名称:" + entity.getName()
                , "描述:" + entity.getDesc()
                , "针对:" + entity.getHabit()
                , "是否立即显示:" + entity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
        );
        showDialog("正在添加分类...");
        entity.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("分类添加成功");
                    setResult(RESULT_OK);
                    mIvImage.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }


    private void initView() {
        mOptions = new RequestOptions()
                .centerCrop();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mIvImage.getLayoutParams();
        lp.width = ScreenUtils.getScreenWidth(mActivity) / 2;
        lp.height = (int) (lp.width * 0.75);
        mIvImage.setLayoutParams(lp);
    }

    private void selectPhoto() {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(mActivity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.mypicture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath(Constants.IMAGE_FILE_PATH)// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                        .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(3, 2)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .selectionMedia(mImageSelectList)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .compressMaxKB(300)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
                .compressWH(1200, 800) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
                .cropWH(1200, 800)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    mImageSelectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (mImageSelectList != null && mImageSelectList.size() > 0) {
                        LocalMedia media = mImageSelectList.get(0);
                        if (media != null && media.isCompressed()) {
                            if (mSortEntity != null && mSortEntity.getPic() != null && !TextUtils.isEmpty(mSortEntity.getPic().getFileUrl())) {
                                mIsNeedDeleteBmobImage = true;
                            }
                            KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                            Glide.with(mActivity)
                                    .load(media.getCompressPath())
                                    .apply(mOptions)
                                    .into(mIvImage);
                        } else {
                            showMessage("图片选取失败");
                            mImageSelectList = new ArrayList<>();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(mActivity);
        super.onDestroy();
    }

    @Override
    @OnClick({R.id.btn_select_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_image:
            case R.id.iv_image:
                selectPhoto();
                break;
        }
    }
}
