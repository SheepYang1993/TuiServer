package me.sheepyang.tuiserver.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.bmobentity.AdvEntity;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class AdvAdapter extends BaseQuickAdapter<AdvEntity, BaseViewHolder> {
    private int mScreenWidth;

    public AdvAdapter(@Nullable List<AdvEntity> data) {
        super(R.layout.adapter_item_adv, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, AdvEntity item) {
        //可以在任何可以拿到Application的地方,拿到AppComponent,从而得到用Dagger管理的单例对象
//        AppComponent appComponent = ((BaseApplication) mContext.getApplicationContext())
//                .getAppComponent();
//        ImageLoader imageLoader = appComponent.imageLoader();
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_adv).getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenWidth / 3;
        helper.getView(R.id.iv_adv).setLayoutParams(lp);

//        imageLoader.loadImage(appComponent.appManager().getCurrentActivity() == null
//                        ? appComponent.application() : appComponent.appManager().getCurrentActivity(),
//                GlideImageConfig
//                        .builder()
//                        .url(item.getPic() != null ? item.getPic().getFileUrl() : "")
//                        .imageView(helper.getView(R.id.iv_adv))
//                        .build());
    }
}
