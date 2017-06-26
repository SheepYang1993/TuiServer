package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.entity.SettingEntity;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class SettingAdapter extends BaseQuickAdapter<SettingEntity, BaseViewHolder> {

    public SettingAdapter(@Nullable List<SettingEntity> data) {
        super(R.layout.adapter_item_main, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingEntity item) {
        helper.setText(R.id.tv_text, item.getText());
    }
}
