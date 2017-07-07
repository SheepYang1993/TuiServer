package me.sheepyang.tuiserver.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by SheepYang on 2017-07-03.
 * 套图详情-所有图片
 */

public class PhotoDetailEntity extends BmobObject implements Serializable {
    private BmobFile pic;

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
}
