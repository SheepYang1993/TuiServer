package me.sheepyang.tuiserver.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by SheepYang on 2017-07-03.
 */

public class UserEntity extends BmobUser implements Serializable {
    private Integer habit;//喜好 0全部；1男生；2女生
    private Integer level;//账号级别 0普通用户；1管理员；2模特；3VIP
    private Integer sex;//性别 1男；2女；
    private Boolean isVip;//是否是VIP，level==2
    private String nick;//昵称
    private BmobFile avatar;//头像

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getHabit() {
        return habit;
    }

    public void setHabit(Integer habit) {
        this.habit = habit;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
        setVip(level != null && (level == 1 || level >= 3));
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
