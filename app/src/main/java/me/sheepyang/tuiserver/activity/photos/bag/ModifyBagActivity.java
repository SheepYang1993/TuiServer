package me.sheepyang.tuiserver.activity.photos.bag;

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
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
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
import me.sheepyang.tuiserver.activity.photos.detail.AddPhotoActivity;
import me.sheepyang.tuiserver.activity.photos.detail.PhotoDetailListActivity;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiserver.utils.AppUtil;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;
import me.sheepyang.tuiserver.utils.DateUtil;
import me.sheepyang.tuiserver.utils.GlideApp;
import me.sheepyang.tuiserver.widget.QBar;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class ModifyBagActivity extends BaseActivity implements View.OnClickListener {
    public static final String TYPE_MODIFY = "type_modify";
    public static final String TYPE_ADD = "type_add";
    public static final String TYPE = "type";
    public static final String MODEL_ENTITY_DATA = "model_entity_data";
    public static final String ENTITY_DATA = "entity_data";
    private static final int TO_PHOTO_LIST = 0x001;
    private static final int TO_PHOTO_DETAIL = 0x002;
    @BindView(R.id.QBar)
    QBar mQBar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_tz_sw)
    TextView mTvTzSw;
    @BindView(R.id.tv_birthday)
    TextView mTvBirthday;
    @BindView(R.id.tv_is_show)
    TextView mTvIsShow;
    @BindView(R.id.tv_cup)
    TextView mTvCup;
    @BindView(R.id.tv_weight)
    TextView mTvWeight;
    @BindView(R.id.tv_total_photos)
    TextView mTvTotalPhotos;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.edt_title)
    EditText mEdtTitle;
    @BindView(R.id.edt_desc)
    EditText mEdtDesc;
    @BindView(R.id.edt_label)
    EditText mEdtLabel;
    @BindView(R.id.edt_see_num)
    EditText mEdtSeeNum;
    @BindView(R.id.edt_collect_num)
    EditText mEdtCollectNum;
    @BindView(R.id.edt_see_base_num)
    EditText mEdtSeeBaseNum;
    @BindView(R.id.edt_collect_base_num)
    EditText mEdtCollectBaseNum;
    @BindView(R.id.cb_is_vip)
    CheckBox mCbIsVip;
    @BindView(R.id.cb_is_blur)
    CheckBox mCbIsBlur;
    @BindView(R.id.cb_is_show)
    CheckBox mCbIsShow;
    @BindView(R.id.btn_all_photos)
    Button mBtnAllPhotos;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private boolean mIsNeedDeleteBmobImage;
    private String mType;
    private ModelEntity mModelEntity;
    private PhotoBagEntity mPhotoBagEntity;

    @Override
    public int setLayoutId() {
        return R.layout.activity_modify_photo_bag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    private void initView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mIvImage.getLayoutParams();
        lp.height = ScreenUtils.getScreenWidth();
        mIvImage.setLayoutParams(lp);
    }

    private void initIntent(Intent intent) {
        mModelEntity = (ModelEntity) intent.getSerializableExtra(MODEL_ENTITY_DATA);
        if (mModelEntity == null) {
            showMessage("找不到模特信息");
            onBackPressed();
            return;
        }
        KLog.i(Constants.TAG
                , "昵称:" + mModelEntity.getNick()
                , "罩杯:" + mModelEntity.getCupSize()
                , "胸围:" + mModelEntity.getBustSize()
                , "腰围:" + mModelEntity.getWaistSize()
                , "臀围:" + mModelEntity.getHipSize()
                , "体重:" + mModelEntity.getWeight()
                , "生日:" + mModelEntity.getBirthday().getDate()
                , "性别:" + (mModelEntity.getSex() == 1 ? "男" : "女")
                , "是否立即显示:" + mModelEntity.getShow()
                , "手机号码:" + mModelEntity.getMobilePhoneNumber()
        );
        mType = intent.getStringExtra(TYPE);
        if (TextUtils.isEmpty(mType)) {
            mType = TYPE_ADD;
        }

        mTvName.setText("昵称:" + mModelEntity.getNick());
        switch (mModelEntity.getSex()) {
            case 1:
                mTvSex.setText("性别:男");
                break;
            case 2:
                mTvSex.setText("性别:女");
                break;
        }

        StringBuilder tz_sw = new StringBuilder();
        tz_sw.append("三围:");
        if (!TextUtils.isEmpty(mModelEntity.getBustSize())) {
            tz_sw.append(mModelEntity.getBustSize());
        } else {
            tz_sw.append("0");
        }
        if (!TextUtils.isEmpty(mModelEntity.getWaistSize())) {
            tz_sw.append("-" + mModelEntity.getWaistSize());
        } else {
            tz_sw.append("-0");
        }
        if (!TextUtils.isEmpty(mModelEntity.getHipSize())) {
            tz_sw.append("-" + mModelEntity.getHipSize());
        } else {
            tz_sw.append("-0");
        }

        mTvTzSw.setText(tz_sw);
        mTvBirthday.setText("生日:" + DateUtil.getStringByFormat(mModelEntity.getBirthday().getDate(), DateUtil.dateFormatYMD));
        mTvIsShow.setText(mModelEntity.getShow() ? "正在展示" : "未展示");
        mTvCup.setText("罩杯:" + mModelEntity.getCupSize());
        if (mModelEntity.getWeight() != null) {
            mTvWeight.setText("体重:" + mModelEntity.getWeight() + "KG");
        } else {
            mTvWeight.setText("体重:0KG");
        }
        if (mModelEntity.getPhotoBagList() != null && mModelEntity.getPhotoBagList().size() > 0) {
            mTvTotalPhotos.setText("套图:" + mModelEntity.getPhotoBagList().size() + "套");
        } else {
            mTvTotalPhotos.setText("套图:0套");
        }

        String avatar = "";
        if (mModelEntity.getAvatar() != null && !TextUtils.isEmpty(mModelEntity.getAvatar().getFileUrl())) {
            avatar = mModelEntity.getAvatar().getFileUrl();
        }
        GlideApp.with(mActivity)
                .load(avatar)
                .placeholder(R.drawable.ico_user_avatar)
                .error(R.drawable.ico_user_avatar)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mIvAvatar);


        if (TYPE_MODIFY.equals(mType)) {
            mPhotoBagEntity = (PhotoBagEntity) intent.getSerializableExtra(ENTITY_DATA);
            if (mPhotoBagEntity == null) {
                showMessage("找不到套图信息");
                onBackPressed();
                return;
            }
            mQBar.setTitle("套图详情");
            mQBar.setRightText("修改");
            mBtnAllPhotos.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        if (mPhotoBagEntity != null) {
            mEdtTitle.setText(mPhotoBagEntity.getTitle());
            mEdtLabel.setText(mPhotoBagEntity.getLabel());
            mEdtDesc.setText(mPhotoBagEntity.getDesc());
            mEdtSeeNum.setText(mPhotoBagEntity.getSeeNum() + "");
            mEdtCollectNum.setText(mPhotoBagEntity.getCollectedNum() + "");
            mEdtSeeBaseNum.setText(mPhotoBagEntity.getSeeBaseNum() + "");
            mEdtCollectBaseNum.setText(mPhotoBagEntity.getCollectedBaseNum() + "");
            mCbIsVip.setChecked(mPhotoBagEntity.getVip());
            mCbIsBlur.setChecked(mPhotoBagEntity.getBlur());
            mCbIsShow.setChecked(mPhotoBagEntity.getShow());

            if (mPhotoBagEntity.getCoverPic() != null && !TextUtils.isEmpty(mPhotoBagEntity.getCoverPic().getFileUrl())) {
                GlideApp.with(mActivity)
                        .load(mPhotoBagEntity.getCoverPic().getFileUrl())
                        .placeholder(R.drawable.ico_user_avatar)
                        .error(R.drawable.ico_user_avatar)
                        .centerCrop()
                        .into(mIvImage);
            }
        }
    }

    private void initListener() {
//        mIvImage.setOnLongClickListener((View v) -> {
//            if (mImageSelectList != null && mImageSelectList.size() > 0) {
//                String msg = "";
//                if (!TextUtils.isEmpty(mEdtTitle.getText().toString())) {
//                    msg = mEdtTitle.getText().toString();
//                } else if (mPhotoBagEntity != null) {
//                    msg = mPhotoBagEntity.getTitle();
//                }
//                new AlertDialog.Builder(mActivity)
//                        .setMessage("确定要删除 " + msg + " 这套图吗？")
//                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
//                            showDialog("正在删除封面");
//                            mImageSelectList = new ArrayList<LocalMedia>();
//                            GlideApp.with(mActivity)
//                                    .load("")
//                                    .centerCrop()
//                                    .placeholder(R.drawable.ico_user_avatar)
//                                    .into(mIvAvatar);
//                            closeDialog();
//                        })
//                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
//                            dialog.dismiss();
//                        })
//                        .show();
//            } else if (mModelEntity != null && mModelEntity.getAvatar() != null && !TextUtils.isEmpty(mModelEntity.getAvatar().getFileUrl())) {
//                new AlertDialog.Builder(mActivity)
//                        .setMessage("确定要删除 " + mModelEntity.getNick() + " 的头像吗？")
//                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
//                            mIsNeedDeleteBmobImage = true;
//                            GlideApp.with(mActivity)
//                                    .load("")
//                                    .placeholder(R.drawable.ico_user_avatar)
//                                    .centerCrop()
//                                    .into(mIvAvatar);
//                        })
//                        .setNegativeButton("取消", (DialogInterface dialog, int which) -> {
//                            dialog.dismiss();
//                        })
//                        .show();
//            }
//            return true;
//        });
        if (TYPE_MODIFY.equals(mType)) {
            mQBar.setOnRightClickListener((View v) -> {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要修改套图信息吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                modifyPhotoBag(mPhotoBagEntity);
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
                addPhotoBag();
            });
        }
    }

    private void modifyPhotoBag(PhotoBagEntity entity) {
        KeyboardUtils.hideSoftInput(mActivity);
        String title = mEdtTitle.getText().toString();
        String desc = mEdtDesc.getText().toString();
        String label = mEdtLabel.getText().toString();
        String seeNum = mEdtSeeNum.getText().toString();
        String collectNum = mEdtCollectNum.getText().toString();
        String seeBaseNum = mEdtSeeBaseNum.getText().toString();
        String collectBaseNum = mEdtCollectBaseNum.getText().toString();
        if (TextUtils.isEmpty(title)) {
            showMessage("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            showMessage("请输入描述");
            return;
        }
        if (TextUtils.isEmpty(label)) {
            showMessage("请输入标签");
            return;
        }
        if (TextUtils.isEmpty(seeNum)) {
            showMessage("请输入浏览数");
            return;
        }
        if (TextUtils.isEmpty(seeBaseNum)) {
            showMessage("请输入浏览基数");
            return;
        }
        if (TextUtils.isEmpty(collectNum)) {
            showMessage("请输入收藏数");
            return;
        }
        if (TextUtils.isEmpty(collectBaseNum)) {
            showMessage("请输入收藏基数");
            return;
        }

        entity.setModel(mModelEntity);
        entity.setTitle(title);
        entity.setDesc(desc);
        entity.setLabel(label);
        entity.setSeeNum(Integer.valueOf(seeNum));
        entity.setSeeBaseNum(Integer.valueOf(seeBaseNum));
        entity.setCollectedNum(Integer.valueOf(collectNum));
        entity.setCollectedBaseNum(Integer.valueOf(collectBaseNum));
        entity.setVip(mCbIsVip.isChecked());
        entity.setBlur(mCbIsBlur.isChecked());
        entity.setShow(mCbIsShow.isChecked());

        KLog.e();
        modifyPhotoBagImage(entity, new OnModifyPhotoBagImageListener() {
            @Override
            public void onSuccess(PhotoBagEntity entity) {
                KLog.e();
                updateModelEntity(entity);
            }

            @Override
            public void onError(BmobException e) {
                KLog.e();
                closeDialog();
                AppUtil.logout();
                BmobExceptionUtil.handler(e);
            }
        });

    }

    private void updateModelEntity(PhotoBagEntity entity) {
        showDialog("正在修改套图...");
        if (entity.getCoverPic() == null || TextUtils.isEmpty(entity.getCoverPic().getFileUrl())) {
            entity.remove("coverPic");
        }
        entity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("套图修改成功");
                    AppUtil.logout();
                    setResult(RESULT_OK);
                    mIvAvatar.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    AppUtil.logout();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void deletePhotoBagPic(PhotoBagEntity entity, UpdateListener listener) {
        BmobFile file = new BmobFile();
        file.setUrl(entity.getCoverPic().getUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        KLog.i(entity.getCoverPic().getUrl());
        if (entity.getCoverPic() != null && !TextUtils.isEmpty(entity.getCoverPic().getUrl())) {
            file.delete(listener);
        }
    }

    private void modifyPhotoBagImage(PhotoBagEntity entity, OnModifyPhotoBagImageListener listener) {
        KLog.e();
        if (entity.getCoverPic() == null || TextUtils.isEmpty(entity.getCoverPic().getFileUrl())) {//服务器无图
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
                                entity.setCoverPic(bmobFile);
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
            entity.setCoverPic(null);
            listener.onSuccess(entity);
        } else {//服务器已经有图
            KLog.e();
            if (mIsNeedDeleteBmobImage) {
                mIsNeedDeleteBmobImage = false;
                showDialog("正在修改封面...");
                deletePhotoBagPic(entity, new UpdateListener() {
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
                                                entity.setCoverPic(bmobFile);
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
                            entity.setCoverPic(null);
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

    private interface OnModifyPhotoBagImageListener {
        void onSuccess(PhotoBagEntity entity);

        void onError(BmobException e);
    }

    private void addPhotoBag() {
        KeyboardUtils.hideSoftInput(mActivity);
        String title = mEdtTitle.getText().toString();
        String desc = mEdtDesc.getText().toString();
        String label = mEdtLabel.getText().toString();
        String seeNum = mEdtSeeNum.getText().toString();
        String collectNum = mEdtCollectNum.getText().toString();
        String seeBaseNum = mEdtSeeBaseNum.getText().toString();
        String collectBaseNum = mEdtCollectBaseNum.getText().toString();
        if (TextUtils.isEmpty(title)) {
            showMessage("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            showMessage("请输入描述");
            return;
        }
        if (TextUtils.isEmpty(label)) {
            showMessage("请输入标签");
            return;
        }
        if (TextUtils.isEmpty(seeNum)) {
            showMessage("请输入浏览数");
            return;
        }
        if (TextUtils.isEmpty(seeBaseNum)) {
            showMessage("请输入浏览基数");
            return;
        }
        if (TextUtils.isEmpty(collectNum)) {
            showMessage("请输入收藏数");
            return;
        }
        if (TextUtils.isEmpty(collectBaseNum)) {
            showMessage("请输入收藏基数");
            return;
        }

        PhotoBagEntity entity = new PhotoBagEntity();
        entity.setModel(mModelEntity);
        entity.setTitle(title);
        entity.setDesc(desc);
        entity.setLabel(label);
        entity.setSeeNum(Integer.valueOf(seeNum));
        entity.setSeeBaseNum(Integer.valueOf(seeBaseNum));
        entity.setCollectedNum(Integer.valueOf(collectNum));
        entity.setCollectedBaseNum(Integer.valueOf(collectBaseNum));
        entity.setPhotoNum(0);
        entity.setVip(mCbIsVip.isChecked());
        entity.setBlur(mCbIsBlur.isChecked());
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
                entity.setCoverPic(bmobFile);
                showDialog("正在上传封面...");
                uploadPic(bmobFile, new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            closeDialog();
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                            savePhotoBagEntity(entity);
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
        savePhotoBagEntity(entity);
    }

    private void savePhotoBagEntity(PhotoBagEntity entity) {
        KLog.i(Constants.TAG
                , "模特id:" + entity.getModel().getObjectId()
                , "标题:" + entity.getTitle()
                , "描述:" + entity.getDesc()
                , "标签:" + entity.getLabel()
                , "浏览基数:" + entity.getSeeBaseNum()
                , "收藏基数:" + entity.getCollectedBaseNum()
                , "浏览数:" + entity.getSeeNum()
                , "收藏数:" + entity.getCollectedNum()
                , "是否VIP:" + entity.getVip()
                , "是否模糊:" + entity.getBlur()
                , "是否展示:" + entity.getShow()
        );
        showDialog("正在添加套图...");
        entity.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("套图添加成功");
                    setResult(RESULT_OK);
                    mIvAvatar.postDelayed(() -> {
                        onBackPressed();
                    }, 500);
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void uploadPic(BmobFile bmobFile, UploadFileListener listener) {
        bmobFile.uploadblock(listener);
    }

    @Override
    @OnClick({R.id.iv_image, R.id.btn_select_image, R.id.btn_all_photos})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
            case R.id.btn_select_image:
                selectPhoto();
                break;
            case R.id.btn_all_photos:
                Intent intent = new Intent(mActivity, PhotoDetailListActivity.class);
                intent.putExtra(PhotoDetailListActivity.MODEL_ENTITY_DATA, mModelEntity);
                intent.putExtra(PhotoDetailListActivity.PHOTO_BAG_ENTITY_DATA, mPhotoBagEntity);
                startActivityForResult(intent, TO_PHOTO_DETAIL);
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
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
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
                        LocalMedia media = mImageSelectList.get(0);
                        if (media != null && media.isCompressed()) {
                            if (mPhotoBagEntity != null && mPhotoBagEntity.getCoverPic() != null && !TextUtils.isEmpty(mPhotoBagEntity.getCoverPic().getFileUrl())) {
                                mIsNeedDeleteBmobImage = true;
                            }
                            KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                            GlideApp.with(mActivity)
                                    .load(media.getCompressPath())
                                    .centerCrop()
                                    .into(mIvImage);
                        } else {
                            showMessage("图片选取失败");
                            mImageSelectList = new ArrayList<>();
                        }
                    }
                }
                break;
            case TO_PHOTO_LIST:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(mActivity);
        super.onDestroy();
    }
}
