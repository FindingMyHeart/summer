package com.code.server.constant.game;


import com.code.server.constant.response.IfaceGameVo;

/**
 * Created by sunxianping on 2017/5/24.
 */
public interface IfaceGame extends IGameConstant {
    IfaceGameVo toVo();
    IfaceGameVo toVo(long watchUser);
}