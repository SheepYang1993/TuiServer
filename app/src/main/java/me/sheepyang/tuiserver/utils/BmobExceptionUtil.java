package me.sheepyang.tuiserver.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.socks.library.KLog;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by SheepYang on 2017-06-27.
 */

public class BmobExceptionUtil {
    public static void handler(BmobException e) {
        KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
        ToastUtils.showShortToast("失败：" + e.getMessage() + "," + e.getErrorCode());
    }
}
