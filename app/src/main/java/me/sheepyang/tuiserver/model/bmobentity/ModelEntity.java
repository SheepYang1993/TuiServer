package me.sheepyang.tuiserver.model.bmobentity;


import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by SheepYang on 2017-07-03.
 */

public class ModelEntity extends UserEntity implements Serializable {
    private BmobDate birthday;//生日
    private String cupSize;//罩杯
    private String waistSize;//腰围
    private String bustSize;//胸围
    private String hipSize;//臀围
    private String weight;//体重
    private Boolean isShow;//是否展示
    private List<PhotoBagEntity> photoBagList;//套图列表

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public void setBirthday(BmobDate birthday) {
        this.birthday = birthday;
    }

    public String getCupSize() {
        return cupSize;
    }

    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }

    public String getWaistSize() {
        return waistSize;
    }

    public void setWaistSize(String waistSize) {
        this.waistSize = waistSize;
    }

    public String getBustSize() {
        return bustSize;
    }

    public void setBustSize(String bustSize) {
        this.bustSize = bustSize;
    }

    public String getHipSize() {
        return hipSize;
    }

    public void setHipSize(String hipSize) {
        this.hipSize = hipSize;
    }

    public List<PhotoBagEntity> getPhotoBagList() {
        return photoBagList;
    }

    public void setPhotoBagList(List<PhotoBagEntity> photoBagList) {
        this.photoBagList = photoBagList;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
