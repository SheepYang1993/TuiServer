package me.sheepyang.tuiserver.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by SheepYang on 2017-07-03.
 * 套图信息
 */

public class PhotoBagEntity extends BmobObject implements Serializable {
    private Integer collectedNum;//收藏数
    private Integer seeNum;//浏览数
    private String label;//标签
    private String title;//标题
    private String desc;//描述
    private Boolean isBlur;//是否模糊
    private Boolean isVip;//是否VIP
    private Boolean isShow;//是否展示
    private BmobFile coverPic;//封面
    private Integer photoNum;//套图照片数量

    public Integer getCollectedNum() {
        return collectedNum;
    }

    public void setCollectedNum(Integer collectedNum) {
        this.collectedNum = collectedNum;
    }

    public Integer getSeeNum() {
        return seeNum;
    }

    public void setSeeNum(Integer seeNum) {
        this.seeNum = seeNum;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getBlur() {
        return isBlur;
    }

    public void setBlur(Boolean blur) {
        isBlur = blur;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }
}
