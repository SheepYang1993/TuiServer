package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    public AddPhotoDetailAdapter(@Nullable List<String> data) {
        super(R.layout.adapter_item_photo_detail, data);
//        mParams = new ConstraintLayout.LayoutParams((int) (ScreenUtils.getScreenWidth() / 2.0), (int) (ScreenUtils.getScreenWidth() / 2.0));
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = (int) (ScreenUtils.getScreenWidth() / 2.0);
        lp.height = lp.width;
        helper.getView(R.id.iv_photo).setLayoutParams(lp);

        GlideApp.with(mContext)
                .load(item)
                .placeholder(R.drawable.ico_loading2)
                .error(R.drawable.ico_error_avatar_white)
                .centerCrop()
                .into((ImageView) helper.getView(R.id.iv_photo));
    }
}
