package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.bmobentity.ImageTypeEntity;

/**
 * Created by Administrator on 2017/7/1.
 */

public class SortAdapter extends BaseQuickAdapter<ImageTypeEntity, BaseViewHolder> {
    private RequestOptions mOptions;
    private int mScreenWidth;

    public SortAdapter(@Nullable List<ImageTypeEntity> data) {
        super(R.layout.adapter_item_adv, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
        mOptions = new RequestOptions()
                .centerCrop();
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageTypeEntity item) {

    }
}
