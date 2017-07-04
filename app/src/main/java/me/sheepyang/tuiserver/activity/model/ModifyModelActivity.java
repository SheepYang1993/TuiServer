package me.sheepyang.tuiserver.activity.model;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.socks.library.KLog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseActivity;
import me.sheepyang.tuiserver.app.Constants;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.utils.BmobExceptionUtil;
import me.sheepyang.tuiserver.utils.GlideApp;
import me.sheepyang.tuiserver.utils.transformation.GlideCircleTransform;
import me.sheepyang.tuiserver.widget.QBar;

import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

public class ModifyModelActivity extends BaseActivity implements View.OnClickListener {
    public static final String TYPE_MODIFY = "type_modify";
    public static final String TYPE_ADD = "type_add";
    public static final String TYPE = "type";
    public static final String ENTITY_DATA = "entity_data";
    @BindView(R.id.QBar)
    QBar mQBar;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.edt_nick)
    EditText mEdtNick;
    @BindView(R.id.edt_cup)
    EditText mEdtCup;
    @BindView(R.id.edt_bust_size)
    EditText mEdtBustSize;
    @BindView(R.id.edt_waist_size)
    EditText mEdtWaistSize;
    @BindView(R.id.edt_hip_size)
    EditText mEdtHipSize;
    @BindView(R.id.edt_weight)
    EditText mEdtWeight;
    @BindView(R.id.edt_birthday)
    EditText mEdtBirthday;
    @BindView(R.id.edt_account)
    EditText mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;
    @BindView(R.id.rbtn_sex_man)
    RadioButton mRbtnSexMan;
    @BindView(R.id.rbtn_sex_woman)
    RadioButton mRbtnSexWoman;
    @BindView(R.id.rg_sex)
    RadioGroup mRgSex;
    @BindView(R.id.cb_is_show)
    CheckBox mCbIsShow;
    private List<LocalMedia> mImageSelectList = new ArrayList<>();
    private boolean mIsNeedDeleteBmobImage;

    @Override
    public int setLayoutId() {
        return R.layout.activity_modify_model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQBar.setOnRightClickListener((View v) -> {
            addModel();
        });
    }

    private void addModel() {
        KeyboardUtils.hideSoftInput(mActivity);
        String nick = mEdtNick.getText().toString();
        String cupSize = mEdtCup.getText().toString();
        String bustSize = mEdtBustSize.getText().toString();
        String waistSize = mEdtWaistSize.getText().toString();
        String hipSize = mEdtHipSize.getText().toString();
        String weight = mEdtWeight.getText().toString();
        String birthday = mEdtBirthday.getText().toString();
        String account = mEdtAccount.getText().toString();
        String password = mEdtPassword.getText().toString();
        if (TextUtils.isEmpty(nick)) {
            showMessage("请输入昵称");
            return;
        }
        if (TextUtils.isEmpty(cupSize)) {
            showMessage("请输入罩杯");
            return;
        }
        if (TextUtils.isEmpty(bustSize)) {
            showMessage("请输入胸围");
            return;
        }
        if (TextUtils.isEmpty(waistSize)) {
            showMessage("请输入腰围");
            return;
        }
        if (TextUtils.isEmpty(hipSize)) {
            showMessage("请输入臀围");
            return;
        }
        if (TextUtils.isEmpty(weight)) {
            showMessage("请输入体重");
            return;
        }
        if (TextUtils.isEmpty(birthday)) {
            showMessage("请输入生日");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            showMessage("请输入登录手机号");
            return;
        }
        if (!RegexUtils.isMobileExact(account)) {
            showMessage("手机号码格式不正确");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showMessage("请输入密码");
            return;
        }

        ModelEntity entity = new ModelEntity();
        entity.setLevel(2);
        entity.setNick(nick);
        entity.setCupSize(cupSize);
        entity.setBustSize(bustSize);
        entity.setWaistSize(waistSize);
        entity.setHipSize(hipSize);
        entity.setWeight(weight);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            entity.setBirthday(new BmobDate(sdf.parse(birthday)));
        } catch (ParseException e) {
            showMessage("生日格式不正确");
            e.printStackTrace();
            return;
        }

        entity.setUsername(account);
        entity.setMobilePhoneNumber(account);
        password = EncryptUtils.encryptMD5ToString(password).toLowerCase();
        entity.setPassword(password);
        switch (mRgSex.getCheckedRadioButtonId()) {
            //性别 1男；2女；
            case R.id.rbtn_sex_man:
                entity.setSex(1);
                break;
            case R.id.rbtn_sex_woman:
                entity.setSex(2);
                break;
            default:
                entity.setSex(1);
                break;
        }
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
                entity.setAvatar(bmobFile);
                showDialog("正在上传头像...");
                uploadPic(bmobFile, new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            closeDialog();
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            KLog.i(Constants.TAG, "上传文件成功:" + bmobFile.getFileUrl());
                            saveModelEntity(entity);
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
        saveModelEntity(entity);
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
    @OnClick({R.id.iv_avatar})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
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
//                            if (mAdvEntity != null && mAdvEntity.getPic() != null && !TextUtils.isEmpty(mAdvEntity.getPic().getFileUrl())) {
//                                mIsNeedDeleteBmobImage = true;
//                            }
                            KLog.i(Constants.TAG, media.getPath(), media.getCutPath(), media.getCompressPath());
                            GlideApp.with(mActivity)
                                    .load(media.getCompressPath())
                                    .transform(new MultiTransformation<>(new CenterCrop(), new GlideCircleTransform(mActivity)))
                                    .into(mIvAvatar);
                        } else {
                            showMessage("图片选取失败");
                            mImageSelectList = new ArrayList<>();
                        }
                    }
                    break;
            }
        }
    }
}
