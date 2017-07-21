package me.sheepyang.tuiserver.model.bmobentity;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by SheepYang on 2017-07-21.
 */

public class VIPEntity extends BmobObject {
    private BmobFile pic;//vip展示图片
    private Boolean isShow;//是否展示该条VIP信息
    private String openDesc;//开通流程描述
    private List<VIPDetailEntity> vipList;//套餐列表

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public String getOpenDesc() {
        return openDesc;
    }

    public void setOpenDesc(String openDesc) {
        this.openDesc = openDesc;
    }

    public List<VIPDetailEntity> getVipList() {
        return vipList;
    }

    public void setVipList(List<VIPDetailEntity> vipList) {
        this.vipList = vipList;
    }
}
