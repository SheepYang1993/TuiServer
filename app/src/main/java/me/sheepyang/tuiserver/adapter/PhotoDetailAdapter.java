package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.model.bmobentity.PhotoDetailEntity;

/**
 * Created by Administrator on 2017/7/1.
 */

public class PhotoDetailAdapter extends BaseQuickAdapter<PhotoDetailEntity, BaseViewHolder> {
    private int mScreenWidth;

    public PhotoDetailAdapter(@Nullable List<PhotoDetailEntity> data) {
        super(R.layout.adapter_item_photo_detail, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoDetailEntity item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = mScreenWidth / 2;
        lp.height = (int) (lp.width * 0.75);
        helper.getView(R.id.iv_photo).setLayoutParams(lp);
    }
}
