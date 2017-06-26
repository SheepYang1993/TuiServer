package me.sheepyang.tuiserver.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.socks.library.KLog;
import com.squareup.leakcanary.LeakCanary;

import me.sheepyang.tuiserver.BuildConfig;


/**
 * Created by SheepYang on 2017-06-26.
 */

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        KLog.init(BuildConfig.LOG_DEBUG, Constants.TAG);
        //leakCanary内存泄露检查
        if (BuildConfig.USE_CANARY) {
            LeakCanary.install(this);
        }
    }
}
