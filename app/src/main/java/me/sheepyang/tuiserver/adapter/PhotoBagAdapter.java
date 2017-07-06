package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.PhotoBagEntity;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class PhotoBagAdapter extends BaseQuickAdapter<PhotoBagEntity, BaseViewHolder> {
    private int mScreenWidth;
    private LinearLayout.LayoutParams mParams;

    public PhotoBagAdapter(@Nullable List<PhotoBagEntity> data) {
        super(R.layout.adapter_item_photo_bag, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
        mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoBagEntity item) {
        helper.getView(R.id.iv_desc).setLayoutParams(mParams);
    }
}
