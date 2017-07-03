package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.ImageTypeEntity;

/**
 * Created by Administrator on 2017/7/1.
 */

public class SortAdapter extends BaseQuickAdapter<ImageTypeEntity, BaseViewHolder> {
    private RequestOptions mOptions;
    private int mScreenWidth;

    public SortAdapter(@Nullable List<ImageTypeEntity> data) {
        super(R.layout.adapter_item_sort, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
        mOptions = new RequestOptions()
                .placeholder(R.drawable.ico_loading2)
                .error(R.drawable.ico_error_avatar_white)
                .centerCrop();
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageTypeEntity item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = mScreenWidth / 2;
        lp.height = (int) (lp.width * 0.75);
        helper.getView(R.id.iv_photo).setLayoutParams(lp);

        helper.setText(R.id.tv_name, item.getName());
        if (item.getNum() != null) {
            helper.setText(R.id.tv_photo_num, item.getNum() + "");
        } else {
            helper.setText(R.id.tv_photo_num, "0");
        }

        helper.setText(R.id.tv_is_show, item.getShow() ? "正在展示" : "未展示");
        helper.setText(R.id.tv_is_vip, item.getVip() ? "会员专属" : "");
        helper.setText(R.id.tv_is_blur, item.getBlur() ? "需模糊" : "");
        helper.setVisible(R.id.tv_is_vip, item.getVip());
        helper.setVisible(R.id.tv_is_blur, item.getBlur());

        if (item.getPic() != null && !TextUtils.isEmpty(item.getPic().getFileUrl())) {
            Glide.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .apply(mOptions)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        } else {
            Glide.with(mContext)
                    .load("")
                    .apply(mOptions)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        }
    }
}
