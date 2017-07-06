package me.sheepyang.tuiserver.activity.photos.bag;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
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
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.utils.AppUtil;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;
import me.sheepyang.tuiserver.utils.DateUtil;
import me.sheepyang.tuiserver.utils.GlideApp;
import me.sheepyang.tuiserver.utils.transformation.GlideCircleTransform;
import me.sheepyang.tuiserver.widget.QBar;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class ModifyBagActivity extends BaseActivity implements View.OnClickListener {
    public static final String TYPE_MODIFY = "type_modify";
    public static final String TYPE_ADD = "type_add";
    public static final String TYPE = "type";
    public static final String ENTITY_DATA = "entity_data";
    private static final int TO_PHOTO_LIST = 0x001;
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
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private boolean mIsNeedDeleteBmobImage;
    private String mType;
    private ModelEntity mModelEntity;
    private EditText mEdtConfirmPassword;

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
        mEdtConfirmPassword = new EditText(mActivity);
        mEdtConfirmPassword.setHint("请输入密码");
    }

    private void initData() {
        if (mModelEntity != null) {

        }
    }

    private void initIntent(Intent intent) {
        mModelEntity = (ModelEntity) intent.getSerializableExtra(ENTITY_DATA);
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
                .transform(new MultiTransformation<>(new CenterCrop(), new GlideCircleTransform(mActivity)))
                .into(mIvAvatar);


        if (TYPE_MODIFY.equals(mType)) {
            mQBar.setTitle("套图详情");
            mQBar.setRightText("修改");
        }
    }

    private void initListener() {
//        mIvAvatar.setOnLongClickListener((View v) -> {
//            if (mImageSelectList != null && mImageSelectList.size() > 0) {
//                String msg = "";
//                if (!TextUtils.isEmpty(mEdtNick.getText().toString())) {
//                    msg = mEdtNick.getText().toString();
//                } else if (mModelEntity != null) {
//                    msg = mModelEntity.getNick();
//                }
//                new AlertDialog.Builder(mActivity)
//                        .setMessage("确定要删除 " + msg + " 的头像吗？")
//                        .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
//                            showDialog("正在删除头像");
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
                mEdtConfirmPassword.setText("");
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要修改模特信息吗？")
                        .setView(mEdtConfirmPassword)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                KLog.e();
                                modifyModel(mModelEntity, EncryptUtils.encryptMD5ToString(mEdtConfirmPassword.getText().toString()).toLowerCase());
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
                addModel();
            });
        }
    }

    private void modifyModel(ModelEntity entity, String password) {
//        KeyboardUtils.hideSoftInput(mActivity);
//        if (TextUtils.isEmpty(password)) {
//            showMessage("请输入密码");
//            return;
//        }
//        ModelEntity bu2 = new ModelEntity();
//        bu2.setUsername(entity.getUsername());
//        bu2.setPassword(password);
//        showDialog("正在登录模特账号");
//        toLogin(bu2, new SaveListener<BmobUser>() {
//
//            @Override
//            public void done(BmobUser bmobUser, BmobException e) {
//                closeDialog();
//                if (e == null) {
//                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
//                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
//
//                    String nick = mEdtNick.getText().toString();
//                    String cupSize = mEdtCup.getText().toString();
//                    String bustSize = mEdtBustSize.getText().toString();
//                    String waistSize = mEdtWaistSize.getText().toString();
//                    String hipSize = mEdtHipSize.getText().toString();
//                    String weight = mEdtWeight.getText().toString();
//                    String birthday = mEdtBirthday.getText().toString();
//                    if (TextUtils.isEmpty(nick)) {
//                        showMessage("请输入昵称");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(cupSize)) {
//                        showMessage("请输入罩杯");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(bustSize)) {
//                        showMessage("请输入胸围");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(waistSize)) {
//                        showMessage("请输入腰围");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(hipSize)) {
//                        showMessage("请输入臀围");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(weight)) {
//                        showMessage("请输入体重");
//                        return;
//                    }
//                    if (TextUtils.isEmpty(birthday)) {
//                        showMessage("请输入生日");
//                        return;
//                    }
//
//                    entity.setNick(nick);
//                    entity.setCupSize(cupSize);
//                    entity.setBustSize(bustSize);
//                    entity.setWaistSize(waistSize);
//                    entity.setHipSize(hipSize);
//                    entity.setWeight(weight);
//
//                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        entity.setBirthday(new BmobDate(sdf.parse(birthday)));
//                    } catch (ParseException e1) {
//                        showMessage("生日格式不正确");
//                        e1.printStackTrace();
//                        return;
//                    }
//
//                    switch (mRgSex.getCheckedRadioButtonId()) {
//                        //性别 1男；2女；
//                        case R.id.rbtn_sex_man:
//                            entity.setSex(1);
//                            break;
//                        case R.id.rbtn_sex_woman:
//                            entity.setSex(2);
//                            break;
//                        default:
//                            entity.setSex(1);
//                            break;
//                    }
//                    entity.setShow(mCbIsShow.isChecked());
//                    KLog.e();
//                    modifyModelImage(entity, new OnModifyModelImageListener() {
//                        @Override
//                        public void onSuccess(ModelEntity entity) {
//                            KLog.e();
//                            updateModelEntity(entity);
//                        }
//
//                        @Override
//                        public void onError(BmobException e) {
//                            KLog.e();
//                            closeDialog();
//                            AppUtil.logout();
//                            BmobExceptionUtil.handler(e);
//                        }
//                    });
//                } else {
//                    AppUtil.logout();
//                    BmobExceptionUtil.handler(e);
//                }
//            }
//        });
    }

    private void toLogin(ModelEntity entity, SaveListener listener) {
        entity.login(listener);
    }

    private void updateModelEntity(ModelEntity entity) {
        KLog.i(Constants.TAG
                , "昵称:" + entity.getNick()
                , "罩杯:" + entity.getCupSize()
                , "胸围:" + entity.getBustSize()
                , "腰围:" + entity.getWaistSize()
                , "臀围:" + entity.getHipSize()
                , "生日:" + entity.getBirthday().getDate()
                , "账号:" + entity.getUsername()
                , "性别:" + entity.getSex()
                , "显示:" + entity.getShow()
        );
        showDialog("正在修改模特...");
        if (entity.getAvatar() == null || TextUtils.isEmpty(entity.getAvatar().getFileUrl())) {
            entity.remove("avatar");
        }
        entity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("模特修改成功");
                    AppUtil.logout();
                    setResult(RESULT_OK);
//                    mIvAvatar.postDelayed(() -> {
//                        onBackPressed();
//                    }, 500);
                } else {
                    AppUtil.logout();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void deleteModelPic(ModelEntity entity, UpdateListener listener) {
        BmobFile file = new BmobFile();
        file.setUrl(entity.getAvatar().getUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        KLog.i(entity.getAvatar().getUrl());
        if (entity.getAvatar() != null && !TextUtils.isEmpty(entity.getAvatar().getUrl())) {
            file.delete(listener);
        }
    }

    private void modifyModelImage(ModelEntity entity, OnModifyModelImageListener listener) {
        KLog.e();
        if (entity.getAvatar() == null || TextUtils.isEmpty(entity.getAvatar().getFileUrl())) {//服务器无图
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
                    showDialog("正在上传头像...");
                    uploadPic(bmobFile, new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                closeDialog();
                                KLog.e();
                                entity.setAvatar(bmobFile);
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
            entity.setAvatar(null);
            listener.onSuccess(entity);
        } else {//服务器已经有图
            KLog.e();
            if (mIsNeedDeleteBmobImage) {
                mIsNeedDeleteBmobImage = false;
                showDialog("正在修改头像...");
                deleteModelPic(entity, new UpdateListener() {
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
                                                entity.setAvatar(bmobFile);
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
                            entity.setAvatar(null);
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

    private interface OnModifyModelImageListener {
        void onSuccess(ModelEntity entity);

        void onError(BmobException e);
    }

    private void addModel() {
        KeyboardUtils.hideSoftInput(mActivity);
//        String nick = mEdtNick.getText().toString();
//        String cupSize = mEdtCup.getText().toString();
//        String bustSize = mEdtBustSize.getText().toString();
//        String waistSize = mEdtWaistSize.getText().toString();
//        String hipSize = mEdtHipSize.getText().toString();
//        String weight = mEdtWeight.getText().toString();
//        String birthday = mEdtBirthday.getText().toString();
//        String account = mEdtAccount.getText().toString();
//        String password = mEdtPassword.getText().toString();
//        if (TextUtils.isEmpty(nick)) {
//            showMessage("请输入昵称");
//            return;
//        }
//        if (TextUtils.isEmpty(cupSize)) {
//            showMessage("请输入罩杯");
//            return;
//        }
//        if (TextUtils.isEmpty(bustSize)) {
//            showMessage("请输入胸围");
//            return;
//        }
//        if (TextUtils.isEmpty(waistSize)) {
//            showMessage("请输入腰围");
//            return;
//        }
//        if (TextUtils.isEmpty(hipSize)) {
//            showMessage("请输入臀围");
//            return;
//        }
//        if (TextUtils.isEmpty(weight)) {
//            showMessage("请输入体重");
//            return;
//        }
//        if (TextUtils.isEmpty(birthday)) {
//            showMessage("请输入生日");
//            return;
//        }
//        if (TextUtils.isEmpty(account)) {
//            showMessage("请输入登录手机号");
//            return;
//        }
//        if (!RegexUtils.isMobileExact(account)) {
//            showMessage("手机号码格式不正确");
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            showMessage("请输入密码");
//            return;
//        }
//
//        ModelEntity entity = new ModelEntity();
//        entity.setLevel(2);
//        entity.setNick(nick);
//        entity.setCupSize(cupSize);
//        entity.setBustSize(bustSize);
//        entity.setWaistSize(waistSize);
//        entity.setHipSize(hipSize);
//        entity.setWeight(weight);
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            entity.setBirthday(new BmobDate(sdf.parse(birthday)));
//        } catch (ParseException e) {
//            showMessage("生日格式不正确");
//            e.printStackTrace();
//            return;
//        }
//
//        entity.setUsername(account);
//        entity.setMobilePhoneNumber(account);
//        password = EncryptUtils.encryptMD5ToString(password).toLowerCase();
//        entity.setPassword(password);
//        switch (mRgSex.getCheckedRadioButtonId()) {
//            //性别 1男；2女；
//            case R.id.rbtn_sex_man:
//                entity.setSex(1);
//                break;
//            case R.id.rbtn_sex_woman:
//                entity.setSex(2);
//                break;
//            default:
//                entity.setSex(1);
//                break;
//        }
//        entity.setShow(mCbIsShow.isChecked());
//
//        // 例如 LocalMedia 里面返回三种path
//        // 1.media.getPath(); 为原图path
//        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
//        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
//        // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//        if (mImageSelectList != null && mImageSelectList.size() > 0) {
//            LocalMedia media = mImageSelectList.get(0);
//            if (media != null && media.isCompressed()) {
//                KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
//                BmobFile bmobFile = new BmobFile(new File(media.getCompressPath()));
//                entity.setAvatar(bmobFile);
//                showDialog("正在上传头像...");
//                uploadPic(bmobFile, new UploadFileListener() {
//
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            closeDialog();
//                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
//                            KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
//                            saveModelEntity(entity);
//                        } else {
//                            closeDialog();
//                            BmobExceptionUtil.handler(e);
//                        }
//                    }
//
//                    @Override
//                    public void onProgress(Integer value) {
//                        // 返回的上传进度（百分比）
//                    }
//                });
//                return;
//            } else {
//                showMessage("图片选取失败，请重新选择图片");
//                mImageSelectList = new ArrayList<>();
//                return;
//            }
//        }
//        saveModelEntity(entity);
    }

    private void saveModelEntity(ModelEntity entity) {
        KLog.i(Constants.TAG
                , "昵称:" + entity.getNick()
                , "罩杯:" + entity.getCupSize()
                , "胸围:" + entity.getBustSize()
                , "腰围:" + entity.getWaistSize()
                , "臀围:" + entity.getHipSize()
                , "生日:" + entity.getBirthday().getDate()
                , "账号:" + entity.getUsername()
                , "性别:" + entity.getSex()
                , "显示:" + entity.getShow()
        );
        showDialog("正在添加模特...");
        entity.signUp(new SaveListener<ModelEntity>() {
            @Override
            public void done(ModelEntity s, BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("模特添加成功");
                    setResult(RESULT_OK);
//                    mIvAvatar.postDelayed(() -> {
//                        onBackPressed();
//                    }, 500);
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
    @OnClick({/*R.id.iv_avatar, R.id.btn_select_avatar, R.id.btn_all_photos*/})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
            case R.id.btn_select_avatar:
                selectPhoto();
                break;
            case R.id.btn_all_photos:
                if (mModelEntity != null) {
                    Intent intent = new Intent(mActivity, BagListActivity.class);
                    intent.putExtra(BagListActivity.ENTITY_DATA, mModelEntity);
                    startActivityForResult(intent, TO_PHOTO_LIST);
                }
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
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .selectionMedia(mImageSelectList)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .compressMaxKB(300)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
                .compressWH(500, 500) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
                .cropWH(500, 500)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
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
                            if (mModelEntity != null && mModelEntity.getAvatar() != null && !TextUtils.isEmpty(mModelEntity.getAvatar().getFileUrl())) {
                                mIsNeedDeleteBmobImage = true;
                            }
                            KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
//                            GlideApp.with(mActivity)
//                                    .load(media.getCompressPath())
//                                    .transform(new MultiTransformation<>(new CenterCrop(), new GlideCircleTransform(mActivity)))
//                                    .into(mIvAvatar);
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
}
