package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiserver.utils.DateUtil;
import me.sheepyang.tuiserver.utils.GlideApp;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class PhotoBagAdapter extends BaseQuickAdapter<PhotoBagEntity, BaseViewHolder> {
    private int mScreenWidth;
    private LinearLayout.LayoutParams mParams;
    private MultiTransformation mTransform;

    public PhotoBagAdapter(@Nullable List<PhotoBagEntity> data) {
        super(R.layout.adapter_item_photo_bag, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
        mTransform = new MultiTransformation<>(new CenterCrop(), new CircleCrop());
        mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoBagEntity item) {
//        KLog.i(Constants.TAG
//                , "position:" + helper.getLayoutPosition()
//                , "模特id:" + item.getModel().getObjectId()
//                , "模特头像:" + item.getModel().getAvatar().getFileUrl()
//                , "模特姓名:" + item.getModel().getNick()
//                , "更新时间:" + item.getUpdatedAt()
//                , "封面图片:" + item.getCoverPic().getFileUrl()
//                , "标签:" + item.getLabel()
//                , "标题:" + item.getTitle()
//                , "描述:" + item.getDesc()
//                , "收藏基数:" + item.getCollectedBaseNum()
//                , "收藏数:" + item.getCollectedNum()
//                , "浏览基数:" + item.getSeeBaseNum()
//                , "浏览数:" + item.getSeeNum()
//        );
        helper.getView(R.id.iv_cover).setLayoutParams(mParams);
        helper.setText(R.id.tv_name, item.getModel().getNick());
        helper.setText(R.id.tv_image_date, DateUtil.getStringByFormat(item.getCreatedAt(), "yyyy.MM.dd"));
        helper.setText(R.id.tv_label, "#" + item.getLabel() + "#");
        helper.setText(R.id.tv_desc, item.getDesc());
        helper.setText(R.id.tv_image_num, item.getPhotoNum() + "");
        helper.setText(R.id.tv_collection_num, (item.getCollectedBaseNum() + item.getCollectorIdList().size()) + "");

        if (item.getModel().getAvatar() != null && !TextUtils.isEmpty(item.getModel().getAvatar().getFileUrl())) {
            GlideApp.with(mContext)
                    .load(item.getModel().getAvatar().getFileUrl())
                    .placeholder(R.drawable.ico_user_avatar)
                    .error(R.drawable.ico_user_avatar)
                    .transform(mTransform)
                    .into((ImageView) helper.getView(R.id.iv_avatar));
        } else {
            GlideApp.with(mContext)
                    .load("")
                    .placeholder(R.drawable.ico_user_avatar)
                    .error(R.drawable.ico_user_avatar)
                    .transform(mTransform)
                    .into((ImageView) helper.getView(R.id.iv_avatar));
        }

        if (item.getCoverPic() != null && !TextUtils.isEmpty(item.getCoverPic().getFileUrl())) {
            GlideApp.with(mContext)
                    .load(item.getCoverPic().getFileUrl())
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_cover));
        } else {
            GlideApp.with(mContext)
                    .load("")
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_cover));
        }
    }
}
