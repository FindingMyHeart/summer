package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/5/15.
 */
public class TwoLevelInfoVo {

    private String categoryName;

    private String image;

    private String username;

    private String money;

    public String getImage() {
        return image;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
