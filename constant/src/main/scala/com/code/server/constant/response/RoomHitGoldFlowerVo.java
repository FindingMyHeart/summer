package com.code.server.constant.response;

/**
 * Created by sunxianping on 2017/8/17.
 */
public class RoomHitGoldFlowerVo extends RoomVo {
    public long bankerId = 0;
    //扎金花专用
    protected double caiFen;
    protected int menPai;
    protected int cricleNumber;//轮数
    public long getBankerId() {
        return bankerId;
    }

    public RoomHitGoldFlowerVo setBankerId(long bankerId) {
        this.bankerId = bankerId;
        return this;
    }
    public double getCaiFen() {
        return caiFen;
    }

    public void setCaiFen(double caiFen) {
        this.caiFen = caiFen;
    }

    public int getMenPai() {
        return menPai;
    }

    public void setMenPai(int menPai) {
        this.menPai = menPai;
    }

    public int getCricleNumber() {
        return cricleNumber;
    }

    public void setCricleNumber(int cricleNumber) {
        this.cricleNumber = cricleNumber;
    }
}
