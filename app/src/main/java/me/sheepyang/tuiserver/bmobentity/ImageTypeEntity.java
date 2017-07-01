package me.sheepyang.tuiserver.bmobentity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/7/1.
 */

public class ImageTypeEntity extends BmobObject {
    private String name;//分类名称
    private Integer num;//该分类照片数量
    private BmobFile pic;//封面

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
}
