package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.bmobentity.AdvEntity;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class AdvAdapter extends BaseQuickAdapter<AdvEntity, BaseViewHolder> {
    private RequestOptions mOptions;
    private int mScreenWidth;

    public AdvAdapter(@Nullable List<AdvEntity> data) {
        super(R.layout.adapter_item_adv, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
        mOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.adv_placeholder);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdvEntity item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_adv).getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenWidth / 3;
        helper.getView(R.id.iv_adv).setLayoutParams(lp);

        Glide.with(mContext)
                .load(item.getPic().getFileUrl())
                .apply(mOptions)
                .into((ImageView) helper.getView(R.id.iv_adv));
    }
}
