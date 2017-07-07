package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.utils.GlideApp;

/**
 * Created by Administrator on 2017/7/1.
 */

public class AddPhotoDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int mScreenWidth;

    public AddPhotoDetailAdapter(@Nullable List<String> data) {
        super(R.layout.adapter_item_photo_detail, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = mScreenWidth / 2;
        lp.height = (int) (lp.width * 0.75);
        helper.getView(R.id.iv_photo).setLayoutParams(lp);

        GlideApp.with(mContext)
                .load(item)
                .placeholder(R.drawable.ico_loading2)
                .error(R.drawable.ico_error_avatar_white)
                .into((ImageView) helper.getView(R.id.iv_photo));
    }
}
