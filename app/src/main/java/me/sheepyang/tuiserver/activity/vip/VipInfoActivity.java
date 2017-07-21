package me.sheepyang.tuiserver.activity.vip;

import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import me.sheepyang.tuiserver.R;
import me.sheepyang.tuiserver.activity.base.BaseActivity;
import me.sheepyang.tuiserver.widget.QBar;

public class VipInfoActivity extends BaseActivity {

    @BindView(R.id.QBar)
    QBar mQBar;

    @Override
    public int setLayoutId() {
        return R.layout.activity_vip_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQBar.setOnRightClickListener((View v) -> {
            addVIPInfo();
        });
    }

    private void addVIPInfo() {

    }
}
