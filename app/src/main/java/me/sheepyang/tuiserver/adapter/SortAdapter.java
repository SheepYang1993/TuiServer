package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.SortEntity;
import me.sheepyang.tuiserver.utils.GlideApp;

/**
 * Created by Administrator on 2017/7/1.
 */

public class SortAdapter extends BaseQuickAdapter<SortEntity, BaseViewHolder> {
    private int mScreenWidth;

    public SortAdapter(@Nullable List<SortEntity> data) {
        super(R.layout.adapter_item_sort, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, SortEntity item) {
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
            GlideApp.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_photo));
        } else {
            GlideApp.with(mContext)
                    .load("")
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_photo));
        }
    }
}
