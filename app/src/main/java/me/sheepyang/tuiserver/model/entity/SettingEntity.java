package me.sheepyang.tuiserver.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SheepYang on 2017-06-21.
 */

public class SettingEntity implements Parcelable {
    private String text;
    private String desc;
    private Class clazz;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.desc);
        dest.writeSerializable(this.clazz);
    }

    public SettingEntity() {
    }

    public SettingEntity(String text, Class clazz) {
        this.text = text;
        this.clazz = clazz;
    }

    protected SettingEntity(Parcel in) {
        this.text = in.readString();
        this.desc = in.readString();
        this.clazz = (Class) in.readSerializable();
    }

    public static final Creator<SettingEntity> CREATOR = new Creator<SettingEntity>() {
        @Override
        public SettingEntity createFromParcel(Parcel source) {
            return new SettingEntity(source);
        }

        @Override
        public SettingEntity[] newArray(int size) {
            return new SettingEntity[size];
        }
    };
}
