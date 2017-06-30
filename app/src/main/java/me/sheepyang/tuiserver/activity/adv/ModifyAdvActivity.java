package me.sheepyang.tuiserver.activity.adv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.TextView;

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
import me.sheepyang.tuiserver.bmobentity.AdvEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;
import me.sheepyang.tuiserver.widget.QBar;
import me.sheepyang.tuiserver.widget.dialog.LoadingDialog;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class ModifyAdvActivity extends BaseActivity implements View.OnClickListener {

    public static final String TYPE_MODIFY = "type_modify";
    public static final String TYPE_ADD = "type_add";
    public static final String TYPE = "type";
    public static final String ENTITY_DATA = "entity_data";
    @BindView(R.id.QBar)
    QBar mQBar;
    @BindView(R.id.edt_title)
    EditText mEdtTitle;
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
    @BindView(R.id.rbtn_type_none)
    RadioButton mRbtnTypeNone;
    @BindView(R.id.rbtn_type_web)
    RadioButton mRbtnTypeWeb;
    @BindView(R.id.rbtn_type_model)
    RadioButton mRbtnTypeModel;
    @BindView(R.id.rg_type)
    RadioGroup mRgType;
    @BindView(R.id.btn_select_model)
    Button mBtnSelectModel;
    @BindView(R.id.edt_web_address)
    EditText mEdtWebAddress;
    @BindView(R.id.cb_is_show)
    CheckBox mCbIsShow;
    @BindView(R.id.btn_select_adv_image)
    Button mBtnSelectAdvImage;
    @BindView(R.id.iv_adv)
    ImageView mIvAdv;
    @BindView(R.id.tv_web_address)
    TextView mTvWebAddress;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private String mType;
    private AdvEntity mAdvEntity;
    private RequestOptions mOptions;
    private boolean mIsNeedDeleteBmobImage;

    @Override
    public int setLayoutId() {
        return R.layout.activity_modify_adv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    private void initData() {
        if (mAdvEntity != null) {
            mEdtTitle.setText(mAdvEntity.getTitle());
            mEdtDesc.setText(mAdvEntity.getDesc());
            switch (mAdvEntity.getHabit()) {
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
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mCbIsShow.getLayoutParams();
            switch (mAdvEntity.getType()) {
                case 0://置空
                    mRgType.check(R.id.rbtn_type_none);
                    mBtnSelectModel.setVisibility(View.GONE);
                    mTvWebAddress.setVisibility(View.GONE);
                    mEdtWebAddress.setVisibility(View.GONE);
                    lp.topToBottom = R.id.rg_type;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                case 1://外链
                    mRgType.check(R.id.rbtn_type_web);
                    mEdtWebAddress.setText(mAdvEntity.getTempUrl());
                    mBtnSelectModel.setVisibility(View.GONE);
                    mTvWebAddress.setVisibility(View.VISIBLE);
                    mEdtWebAddress.setVisibility(View.VISIBLE);
                    lp.topToBottom = R.id.edt_web_address;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                case 2://模特
                    mRgType.check(R.id.rbtn_type_model);
                    mBtnSelectModel.setVisibility(View.VISIBLE);
                    mTvWebAddress.setVisibility(View.GONE);
                    mEdtWebAddress.setVisibility(View.GONE);
                    lp.topToBottom = R.id.btn_select_model;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                default://置空
                    mRgType.check(R.id.rbtn_type_none);
                    mBtnSelectModel.setVisibility(View.GONE);
                    mTvWebAddress.setVisibility(View.GONE);
                    mEdtWebAddress.setVisibility(View.GONE);
                    lp.topToBottom = R.id.rg_type;
                    mCbIsShow.setLayoutParams(lp);
                    break;
            }
            mCbIsShow.setChecked(mAdvEntity.getShow());

            if (mAdvEntity.getPic() != null && !TextUtils.isEmpty(mAdvEntity.getPic().getFileUrl())) {
                Glide.with(mActivity)
                        .load(mAdvEntity.getPic().getFileUrl())
                        .apply(mOptions)
                        .into(mIvAdv);
            }
        }
    }

    private void initIntent(Intent intent) {
        mType = intent.getStringExtra(TYPE);
        if (TextUtils.isEmpty(mType)) {
            mType = TYPE_ADD;
        }
        if (TYPE_MODIFY.equals(mType)) {
            mAdvEntity = (AdvEntity) intent.getSerializableExtra(ENTITY_DATA);
            if (mAdvEntity == null) {
                showMessage("找不到广告信息");
                onBackPressed();
                return;
            }
            KLog.i(Constants.TAG
                    , "标题:" + mAdvEntity.getTitle()
                    , "描述:" + mAdvEntity.getDesc()
                    , "针对:" + mAdvEntity.getHabit()
                    , "类型:" + mAdvEntity.getType()
                    , "外链地址:" + mAdvEntity.getTempUrl()
//                , "模特id:" + mEdtTitle.getText().toString()
                    , "是否立即显示:" + mAdvEntity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
            );
            mQBar.setTitle("修改广告");
            mQBar.setRightText("修改");
        }
    }

    private void initView() {
        mOptions = new RequestOptions()
                .centerCrop();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mIvAdv.getLayoutParams();
        lp.width = ScreenUtils.getScreenWidth(mActivity);
        lp.height = lp.width / 3;
        mIvAdv.setLayoutParams(lp);
    }


    public void showDialog(String msg) {
        if (mDialog == null) {
            mDialog = new LoadingDialog(mActivity, msg);
        } else {
            mDialog.setMessage(msg);
        }
        mDialog.show();
    }

    public void closeDialog() {
        mDialog.close();
    }

    private void initListener() {
        mIvAdv.setOnLongClickListener((View v) -> {
            if (mImageSelectList != null && mImageSelectList.size() > 0) {
                String msg = "";
                if (!TextUtils.isEmpty(mEdtTitle.getText().toString())) {
                    msg = mEdtTitle.getText().toString();
                } else if (mAdvEntity != null) {
                    msg = mAdvEntity.getTitle();
                }
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + msg + " 这张广告图片吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            showDialog("正在删除图片");
                            mImageSelectList = new ArrayList<LocalMedia>();
                            Glide.with(mActivity)
                                    .load("")
                                    .apply(mOptions)
                                    .into(mIvAdv);
                            closeDialog();
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else if (mAdvEntity != null && mAdvEntity.getPic() != null && !TextUtils.isEmpty(mAdvEntity.getPic().getFileUrl())) {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除 " + mAdvEntity.getTitle() + " 这张广告图片吗？")
                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                            mIsNeedDeleteBmobImage = true;
                            Glide.with(mActivity)
                                    .load("")
                                    .apply(mOptions)
                                    .into(mIvAdv);
                        })
                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
            return true;
        });
        mRgType.setOnCheckedChangeListener((RadioGroup group, @IdRes int checkedId) -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mCbIsShow.getLayoutParams();
            switch (checkedId) {
                case R.id.rbtn_type_none:
                    mBtnSelectModel.setVisibility(View.GONE);
                    mTvWebAddress.setVisibility(View.GONE);
                    mEdtWebAddress.setVisibility(View.GONE);
                    lp.topToBottom = R.id.rg_type;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                case R.id.rbtn_type_web:
                    mBtnSelectModel.setVisibility(View.GONE);
                    mTvWebAddress.setVisibility(View.VISIBLE);
                    mEdtWebAddress.setVisibility(View.VISIBLE);
                    lp.topToBottom = R.id.edt_web_address;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                case R.id.rbtn_type_model:
                    mBtnSelectModel.setVisibility(View.VISIBLE);
                    mTvWebAddress.setVisibility(View.GONE);
                    mEdtWebAddress.setVisibility(View.GONE);
                    lp.topToBottom = R.id.btn_select_model;
                    mCbIsShow.setLayoutParams(lp);
                    break;
                default:
                    break;
            }
        });
        if (TYPE_MODIFY.equals(mType)) {
            mQBar.setOnRightClickListener((View v) -> {
                modifyAdv(mAdvEntity);
            });
        } else {
            mQBar.setOnRightClickListener((View v) -> {
                addAdv();
            });
        }
    }

    private void modifyAdv(AdvEntity entity) {
        KLog.e();
        KeyboardUtils.hideSoftInput(mActivity);
        if (TextUtils.isEmpty(mEdtTitle.getText().toString())) {
            showMessage("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(mEdtDesc.getText().toString())) {
            showMessage("请输入描述");
            return;
        }
        entity.setTitle(mEdtTitle.getText().toString());
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
        int type;//类型
        switch (mRgType.getCheckedRadioButtonId()) {
            case R.id.rbtn_type_none://置空
                type = 0;
                entity.setTempUrl("");
                break;
            case R.id.rbtn_type_web://外链
                type = 1;
                if (TextUtils.isEmpty(mEdtWebAddress.getText().toString())) {
                    showMessage("请输入外链地址");
                    return;
                }
                entity.setTempUrl(mEdtWebAddress.getText().toString());
                break;
            case R.id.rbtn_type_model://模特
                type = 2;
                entity.setTempUrl("");
                showMessage("暂未开放模特类型");
                return;
            default:
                type = 0;
                break;
        }
        entity.setType(type);
        entity.setShow(mCbIsShow.isChecked());

        modifyAdvImage(entity, new OnModifyAdvImageListener() {
            @Override
            public void onSuccess(AdvEntity entity) {
                updateAdvEntity(entity);
            }

            @Override
            public void onError(BmobException e) {
                closeDialog();
                BmobExceptionUtil.handler(e);
            }
        });
    }

    private void modifyAdvImage(AdvEntity entity, OnModifyAdvImageListener listener) {
        if (entity.getPic() == null || TextUtils.isEmpty(entity.getPic().getFileUrl())) {//服务器无图
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
                    showDialog("正在上传图片...");
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
            if (mIsNeedDeleteBmobImage) {
                mIsNeedDeleteBmobImage = false;
                showDialog("正在修改图片...");
                deleteAdvPic(entity, new UpdateListener() {
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
            }
        }
    }

    private interface OnModifyAdvImageListener {
        void onSuccess(AdvEntity entity);

        void onError(BmobException e);
    }

    private void deleteAdvPic(AdvEntity entity, UpdateListener listener) {
        BmobFile file = new BmobFile();
        file.setUrl(entity.getPic().getUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        KLog.i(entity.getPic().getUrl());
        if (entity.getPic() != null && !TextUtils.isEmpty(entity.getPic().getUrl())) {
            file.delete(listener);
        }
    }

    private void updateAdvEntity(AdvEntity entity) {
        KLog.e();
        KLog.i(Constants.TAG
                , "标题:" + entity.getTitle()
                , "描述:" + entity.getDesc()
                , "针对:" + entity.getHabit()
                , "类型:" + entity.getType()
                , "外链地址:" + entity.getTempUrl()
//                , "模特id:" + mEdtTitle.getText().toString()
                , "是否立即显示:" + entity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
        );
        showDialog("正在修改广告...");
        if (entity.getPic() == null || TextUtils.isEmpty(entity.getPic().getFileUrl())) {
            entity.remove("pic");
        }
        entity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("广告修改成功");
                    setResult(RESULT_OK);
                    mIvAdv.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void addAdv() {
        KeyboardUtils.hideSoftInput(mActivity);
        if (TextUtils.isEmpty(mEdtTitle.getText().toString())) {
            showMessage("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(mEdtDesc.getText().toString())) {
            showMessage("请输入描述");
            return;
        }
        AdvEntity entity = new AdvEntity();
        entity.setTitle(mEdtTitle.getText().toString());
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
        int type;//类型
        switch (mRgType.getCheckedRadioButtonId()) {
            case R.id.rbtn_type_none:
                type = 0;
                break;
            case R.id.rbtn_type_web://外链
                type = 1;
                if (TextUtils.isEmpty(mEdtWebAddress.getText().toString())) {
                    showMessage("请输入外链地址");
                    return;
                }
                entity.setTempUrl(mEdtWebAddress.getText().toString());
                break;
            case R.id.rbtn_type_model://模特
                type = 2;
                showMessage("暂未开放模特类型");
                return;
            default:
                type = 0;
                break;
        }
        entity.setType(type);
        entity.setShow(mCbIsShow.isChecked());

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
                showDialog("正在上传图片...");
                uploadPic(bmobFile, new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            closeDialog();
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                            saveAdvEntity(entity);
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
        saveAdvEntity(entity);
    }

    private void uploadPic(BmobFile bmobFile, UploadFileListener listener) {
        bmobFile.uploadblock(listener);
    }

    private void saveAdvEntity(AdvEntity entity) {
        KLog.i(Constants.TAG
                , "标题:" + entity.getTitle()
                , "描述:" + entity.getDesc()
                , "针对:" + entity.getHabit()
                , "类型:" + entity.getType()
                , "外链地址:" + entity.getTempUrl()
//                , "模特id:" + mEdtTitle.getText().toString()
                , "是否立即显示:" + entity.getShow()
//                , "图片路径:" + entity.getPic().getFileUrl()
        );
        showDialog("正在添加广告...");
        entity.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("广告添加成功");
                    setResult(RESULT_OK);
                    mIvAdv.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    @Override
    @OnClick({R.id.btn_select_model, R.id.btn_select_adv_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_model:
                showMessage(getString(R.string.not_open));
                break;
            case R.id.btn_select_adv_image:
            case R.id.iv_adv:
                selectPhoto();
                break;
            default:
                break;
        }
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
                .withAspectRatio(3, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
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
                .compressWH(1200, 400) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
                .cropWH(1200, 400)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
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
                            if (mAdvEntity != null && mAdvEntity.getPic() != null && !TextUtils.isEmpty(mAdvEntity.getPic().getFileUrl())) {
                                mIsNeedDeleteBmobImage = true;
                            }
                            KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                            Glide.with(mActivity)
                                    .load(media.getCompressPath())
                                    .apply(mOptions)
                                    .into(mIvAdv);
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
}
