package com.code.server.constant.game

import com.code.server.constant.db.UserInfo

/**
 * Created by sunxianping on 2017/5/27.
 */
class UserBean implements IUserBean {
    long id; // required
    String username; // required
    String image; // required
    String seatId; // required
    String account; // required
    String ipConfig; // required
    double money; // required
    String roomId; // required
    int vip; // required
    String uuid; // required
    String openId; // required
    int sex; // required
    String marquee; // required
    int referee;
    UserInfo UserInfo;
    String download2;
}