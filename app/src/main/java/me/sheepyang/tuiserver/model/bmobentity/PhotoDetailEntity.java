package me.sheepyang.tuiserver.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by SheepYang on 2017-07-03.
 * 套图详情-所有图片
 */

public class PhotoDetailEntity extends BmobObject implements Serializable {
    private Integer type;//图片类型(后期可设置是否跳转淘宝，外链等)
    private String desc;//描述
    private BmobFile pic;//图片
    private PhotoBagEntity photoBag;//所属套图
    private ModelEntity model;//所属模特
    private Boolean isBlur;//是否模糊
    private Boolean isVip;//是否VIP
    private Boolean isShow;//是否展示
    private SortEntity sort;//分类

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PhotoBagEntity getPhotoBag() {
        return photoBag;
    }

    public void setPhotoBag(PhotoBagEntity photoBag) {
        this.photoBag = photoBag;
    }

    public ModelEntity getModel() {
        return model;
    }

    public void setModel(ModelEntity model) {
        this.model = model;
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

    public SortEntity getSort() {
        return sort;
    }

    public void setSort(SortEntity sort) {
        this.sort = sort;
    }
}
