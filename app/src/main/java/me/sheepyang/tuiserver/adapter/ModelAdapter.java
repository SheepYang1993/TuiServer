package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.ModelEntity;
import me.sheepyang.tuiserver.utils.DateUtil;
import me.sheepyang.tuiserver.utils.GlideApp;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class ModelAdapter extends BaseQuickAdapter<ModelEntity, BaseViewHolder> {

    public ModelAdapter(@Nullable List<ModelEntity> data) {
        super(R.layout.adapter_item_model, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ModelEntity item) {
        helper.setText(R.id.tv_name, "昵称:" + item.getNick());
        switch (item.getSex()) {
            case 1:
                helper.setText(R.id.tv_sex, "性别:男");
                break;
            case 2:
                helper.setText(R.id.tv_sex, "性别:女");
                break;
        }

        StringBuilder tz_sw = new StringBuilder();
        tz_sw.append("三围:");
        if (!TextUtils.isEmpty(item.getBustSize())) {
            tz_sw.append(item.getBustSize());
        } else {
            tz_sw.append("0");
        }
        if (!TextUtils.isEmpty(item.getWaistSize())) {
            tz_sw.append("-" + item.getWaistSize());
        } else {
            tz_sw.append("-0");
        }
        if (!TextUtils.isEmpty(item.getHipSize())) {
            tz_sw.append("-" + item.getHipSize());
        } else {
            tz_sw.append("-0");
        }

        helper.setText(R.id.tv_tz_sw, tz_sw);
        helper.setText(R.id.tv_birthday, "生日:" + DateUtil.getStringByFormat(item.getBirthday().getDate(), DateUtil.dateFormatYMD));
        helper.setText(R.id.tv_is_show, item.getShow() ? "正在展示" : "未展示");
        helper.setText(R.id.tv_cup, "罩杯:" + item.getCupSize());
        if (item.getWeight() != null) {
            helper.setText(R.id.tv_weight, "体重:" + item.getWeight() + "KG");
        } else {
            helper.setText(R.id.tv_weight, "体重:0KG");
        }
        if (item.getPhotoBagList() != null && item.getPhotoBagList().size() > 0) {
            helper.setText(R.id.tv_total_photos, "套图:" + item.getPhotoBagList().size() + "套");
        } else {
            helper.setText(R.id.tv_total_photos, "套图:0套");
        }

        String avatar = "";
        if (item.getAvatar() != null && !TextUtils.isEmpty(item.getAvatar().getFileUrl())) {
            avatar = item.getAvatar().getFileUrl();
        }
        GlideApp.with(mContext)
                .load(avatar)
                .placeholder(R.drawable.ico_user_avatar)
                .error(R.drawable.ico_user_avatar)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into((ImageView) helper.getView(R.id.iv_avatar));
    }
}
