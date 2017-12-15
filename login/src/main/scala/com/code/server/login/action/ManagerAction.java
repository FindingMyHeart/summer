package com.code.server.login.action;

import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.code.server.redis.config.IConstant.ROOM_USER;
import static com.code.server.redis.config.IConstant.USER_GATE;

/**
 * Created by sunxianping on 2017/6/27.
 */
@RestController
@EnableAutoConfiguration
public class ManagerAction {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/getOnlineUser")
    public Map<String, Object> getOnlineUser() {
        BoundHashOperations<String,String,String> user_gate = redisTemplate.boundHashOps(USER_GATE);
        BoundHashOperations<String,String,String> room = redisTemplate.boundHashOps(ROOM_USER);
        Map<String,Object> result = new HashMap<>();
        result.put("userNum",user_gate.size());
        result.put("roomNum",room.size());
        return result;
    }


    @RequestMapping("/getRoomUser")
    public Map<String, Object> getRoomUser(String roomId) {
        Map<String, Object> result = new HashMap<>();
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if(serverId == null){
            result.put("user", null);
        }else{
            result.put("user", RedisManager.getRoomRedisService().getUsers(roomId));
        }
        return result;

    }
}
