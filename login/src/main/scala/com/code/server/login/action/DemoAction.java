package com.code.server.login.action;
import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.IChargeType;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.*;
import com.code.server.db.model.*;
import com.code.server.login.anotation.DemoChecker;
import com.code.server.login.service.AgentService;
import com.code.server.login.service.GameUserService;
import com.code.server.login.service.HomeService;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.util.CookieUtils;
import com.code.server.login.util.MD5Util;
import com.code.server.login.vo.DChargeAdminVo;
import com.code.server.login.vo.DChildVo;
import com.code.server.login.vo.GameAgentVo;
import com.code.server.redis.service.RedisManager;
import com.code.server.rpc.idl.ChargeType;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import javafx.scene.chart.Chart;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.management.Agent;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
/**
 * Created by dajuejinxian on 2018/6/22.
 */
@RestController
@RequestMapping("/admin")
public class DemoAction extends Cors{

    @Autowired
    private IAgentUserDao agentUserDao;

    @Autowired
    private ILogRecordDao logRecordDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IChargeDao chargeDao;

    @Autowired
    private IGameAgentDao gameAgentDao;

    @Autowired
    private HomeService homeService;

    @Autowired
    private AgentService agentService;

    public static final int MONEY_TYPE = 0;

    public static final int GOLD_TYPE = 1;

    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    public static boolean isNumber(String str) {
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        //可以判断正负、整数小数
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();

        return isInt || isDouble;
    }

    public static Map<String, Object> getUserInfo(HttpServletRequest request){
        return (Map<String, Object>)AgentUtil.caches.get("a");
    }

    public static int getRole(long userId){
        int role = 1;
        if (RedisManager.getAgentRedisService().isExit(userId)){
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
            if (agentBean.getIsPartner() == 1){
                role = 3;
            }else {
                role = 2;
            }
        }
        return role;
    }
    @DemoChecker
    @RequestMapping("/timeSearch")
    public AgentResponse doSearch(String t1, String t2, int curPage){

        if (curPage > 0){
            curPage--;
        }
        String[] sA = null;
        if (t1.contains(",")){
            sA = t1.split(",", 1000);
        }

        String[] sB = null;
        if (t2.contains(",")){
            sB = t2.split(",", 1000);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Date> listA = new ArrayList<>();
        if (sA != null){
            for (String str : sA){
                try {
                    Date date = simpleDateFormat.parse(str);
                    listA.add(date);
                } catch (ParseException e) {
//                e.printStackTrace();
                }
            }
        }

        List<Date> listB = new ArrayList<>();
        if (sB != null){
            for (String str : sB){
                try {
                    Date date = simpleDateFormat.parse(str);
                    listB.add(date);
                } catch (ParseException e) {
                e.printStackTrace();
                }
            }
        }

        int pageSize = 20;
        List<User> list = null;
        long count = 0;
        AgentResponse agentResponse = new AgentResponse();
        if (sA != null && sB == null){
            list = homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else if (sB != null && sB == null){
//            list = userDao.findUsersByLastLoginDateBetween(listB.get(0), listB.get(1));
            list = homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else if (sA != null && sB != null){
//            list = userDao.findUsersByRegistDateBetweenAndLastLoginDateBetween(listA.get(0), listA.get(1), listB.get(0), listB.get(1));
            list =  homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else {
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            agentResponse.setMsg("请选择日期");
            return agentResponse;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("list", list);

        agentResponse.setData(result);
        return agentResponse;
    }

    @DemoChecker
    @RequestMapping("/roleInfo")
    public AgentResponse roleInfo(long userId){
        long role = getRole(userId);
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(role);
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/toAgent")
    public AgentResponse toAgent(long userId) {
        AgentResponse agentResponse = new AgentResponse();
        long role = getRole(userId);
        if (role == 2){
            agentResponse.setMsg("设置失败!");
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            return agentResponse;
        }

        agentService.change2Agent(userId);
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/toUser")
    public AgentResponse toUser(long userId) {

        AgentResponse agentResponse = new AgentResponse();
        long role = getRole(userId);
        if (role == 1){
            agentResponse.setMsg("设置失败!");
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            return agentResponse;
        }

        agentService.change2Player(userId);
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/toPartner")
    public AgentResponse toPartner(long userId) {

        AgentResponse agentResponse = new AgentResponse();
        long role = getRole(userId);
        if (role == 3){
            agentResponse.setMsg("设置失败!");
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            return agentResponse;
        }
        agentService.change2Partner(userId);
        return agentResponse;
    }

    @DemoChecker
    @RequestMapping("/fetchAllPlayers")
    public AgentResponse fetchAllPlayers(int pageSize, int curPage, HttpServletRequest request){

        if (curPage > 0){
            curPage--;
        }
        Page page =  userDao.findAll(new PageRequest(curPage, pageSize));
        List<User> list = page.getContent();
        Map<String, Object> result = new HashMap<>();
        long count = userDao.count();
        result.put("total", count);
        result.put("list", list);
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(result);
        return agentResponse;
    }

    @DemoChecker
    @RequestMapping("/fetchPlayer")
    public AgentResponse fetchPlayer(long userId, HttpServletRequest request){

        if (userId == 0){
            return fetchAllPlayers(20, 1, request);
        }
        User user =  userDao.findOne(userId);
        List<User> list = new ArrayList<>();
        AgentResponse agentResponse = new AgentResponse();
        if (user == null){
            agentResponse.setData(list);
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            Map<String, Object> result = new HashMap<>();
            agentResponse.setMsg("没有记录");
            result.put("total", 0);
            result.put("list", list);
            agentResponse.setData(result);
        }else {
            list.add(user);
            Map<String, Object> result = new HashMap<>();
            result.put("total", 1);
            result.put("list", list);
            agentResponse.setData(result);
        }
        return agentResponse;
    };

    @DemoChecker
    @RequestMapping("/fetchDelegate")
    public AgentResponse fetchDelegate(long userId){
        if (userId == 0){
            return fetchDelegates(1);
        }
//        GameAgent gameAgent = gameAgentDao.findOne(userId);
        GameAgent gameAgent = homeService.findOneDelegate(userId);
        List<GameAgentVo> list = new ArrayList<>();
        AgentResponse agentResponse = new AgentResponse();
        if (gameAgent == null){
            agentResponse.setData(list);
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            Map<String, Object> result = new HashMap<>();
            agentResponse.setMsg(" 没有记录 ");
            result.put("total", 0);
            result.put("list", list);
            agentResponse.setData(result);
        }else {
            GameAgentVo gameAgentVo = new GameAgentVo();
            BeanUtils.copyProperties(gameAgent, gameAgentVo);
            gameAgentVo.setIsPartnerDes(gameAgent.getIsPartner() == 1 ? "合伙人" : "代理");
            list.add(gameAgentVo);
            Map<String, Object> result = new HashMap<>();
            result.put("total", 1);
            result.put("list", list);
            agentResponse.setData(result);
        }

        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/fetchDelegates")
    public AgentResponse fetchDelegates(int curPage){

        if (curPage > 0){
            curPage--;
        }
        int pageSize = 20;
//        Page<GameAgent> page = gameAgentDao.findAll(new PageRequest(curPage, pageSize));
        Page<GameAgent> page = homeService.findDelegates(new PageRequest(curPage, pageSize));
        List<GameAgent> list = page.getContent();

        List<GameAgentVo> voList = new ArrayList<>();
        for (GameAgent gameAgent : list){
            GameAgentVo gameAgentVo = new GameAgentVo();
            BeanUtils.copyProperties(gameAgent, gameAgentVo);
            User user = userDao.findOne(gameAgent.getId());
            gameAgentVo.setName(user.getUsername());
            voList.add(gameAgentVo);
        }

        long count = homeService.delegatesCount();
        Map<String, Object> rs = new HashMap<>();
        rs.put("total", count);
        rs.put("list", voList);

        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(rs);
        return agentResponse;
    }

    @DemoChecker
    @RequestMapping("/fetchPartner")
    public AgentResponse fetchPartner(long userId){
        if (userId == 0){
            return fetchPartners(1);
        }
//        GameAgent gameAgent = gameAgentDao.findOne(userId);
        GameAgent gameAgent = homeService.findOnePartner(userId);
        List<GameAgentVo> list = new ArrayList<>();
        AgentResponse agentResponse = new AgentResponse();
        if (gameAgent == null){
            agentResponse.setData(list);
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            Map<String, Object> result = new HashMap<>();
            agentResponse.setMsg(" 没有记录 ");
            result.put("total", 0);
            result.put("list", list);
            agentResponse.setData(result);
        }else {
            GameAgentVo gameAgentVo = new GameAgentVo();
            BeanUtils.copyProperties(gameAgent, gameAgentVo);
            gameAgentVo.setIsPartnerDes(gameAgent.getIsPartner() == 1 ? "合伙人" : "代理");
            list.add(gameAgentVo);
            Map<String, Object> result = new HashMap<>();
            result.put("total", 1);
            result.put("list", list);
            agentResponse.setData(result);
        }
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/fetchPartners")
    public AgentResponse fetchPartners(int curPage){
        if (curPage > 0){
            curPage--;
        }
        int pageSize = 20;
        Page<GameAgent> page = homeService.findPartner(new PageRequest(curPage,pageSize));
        List<GameAgent> list = page.getContent();

        List<GameAgentVo> voList = new ArrayList<>();
        for (GameAgent gameAgent : list){
            GameAgentVo gameAgentVo = new GameAgentVo();
            BeanUtils.copyProperties(gameAgent, gameAgentVo);
            User user = userDao.findOne(gameAgent.getId());
            gameAgentVo.setName(user.getUsername());
            voList.add(gameAgentVo);
        }

        long count = homeService.partnerCount();
        Map<String, Object> rs = new HashMap<>();
        rs.put("total", count);
        rs.put("list", voList);

        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(rs);
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/logout")
    public AgentResponse logout(){

        String token = AgentUtil.findTokenInHeader();
        if (AgentUtil.caches.keySet().contains(token)){
            AgentUtil.caches.remove(token);
        }
        AgentResponse agentResponse = new AgentResponse();
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping(value = "/doChargeNew", method = RequestMethod.POST)
    public AgentResponse doChargeNew(HttpServletRequest request, long userId, @RequestParam(value = "money", required = true) long money, String type){

        AgentResponse agentResponse = new AgentResponse();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        UserService userService = SpringUtil.getBean(UserService.class);
        User user = userService.getUserByUserId(userId);
        String name = "";
        if (userBean == null) {
            if (user != null) {
                if (type.equals("1")) {
                    user.setMoney(user.getMoney() + money);
                } else if (type.equals("2")) {
                    user.setGold(user.getGold() + money);
                }
                userService.save(user);
                name = user.getUsername();
            } else {
               agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
               return agentResponse;
            }

        } else {//在redis里
            name = userBean.getUsername();
            if (type.equals("1")) {
                RedisManager.getUserRedisService().addUserMoney(userId, money);
                GameUserService.saveUserBean(userId);
            } else if (type.equals("2")) {
                RedisManager.getUserRedisService().addUserGold(userId, money);
                GameUserService.saveUserBean(userId);
            }
        }

        Charge charge = new Charge();
        charge.setOrderId("" + IdWorker.getDefaultInstance().nextId());
        charge.setUserid(userId);
        charge.setUsername(name);
        charge.setCreatetime(new Date());
        charge.setCallbacktime(new Date());
        charge.setOrigin(1);
        charge.setMoney(money);
        charge.setMoney_point(0);
        charge.setRecharge_source("" + IChargeType.AGENT);
        charge.setStatus(1);
        charge.setChargeType(type == "1" ? 0 : 1);
        SpringUtil.getBean(ChargeService.class).save(charge);

        Map<String, Object> rs = new HashMap<>();
        rs.put("money", money);
        rs.put("type", type);
        agentResponse.setData(rs);
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/downward")
    public AgentResponse downwardDelegate(HttpServletRequest request, long agentId){

        //先给个demo
        if (agentId == 0){
            Map<String, Object> rrss = assDemo();

            AgentResponse agentResponse = new AgentResponse();
            agentResponse.setData(rrss);
            return agentResponse;
        }

        //如果代理是空的
        if (RedisManager.getAgentRedisService().getAgentBean(agentId) == null){
            AgentResponse agentResponse = new AgentResponse();
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            agentResponse.setMsg("代理不存在");
            return agentResponse;
        }

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        //直接玩家
        List<Long> aList = new ArrayList<>();
        //二级代理
        List<Long> bList = new ArrayList<>();
        //三级代理
        List<Long> cList = new ArrayList<>();

        agentBean.getChildList().stream()
                .forEach(x -> {

                    if (RedisManager.getAgentRedisService().isExit(x)){
                        bList.add(x);
                    }else {
                        aList.add(x);
                    }
                });

        bList.stream()
                .forEach(x -> {
                    if (RedisManager.getAgentRedisService().isExit(x)){
                        cList.add(x);
                    }
                });

        List<User> aUsers = userDao.findUsersByIdIn(aList);
        List<User> bUsers = userDao.findUsersByIdIn(bList);
//        List<User> cUsers = userDao.findUsersByIdIn(cList);

        Map<String, Object> rs =  assembleDelegateRelationship(agentId, aUsers, bUsers);
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(rs);
        System.out.println("====");
        System.out.println(agentResponse);
        return agentResponse;

//        //直接
//        Map<String, Object> rs = new HashMap<>();
//        List<Object> rsList = new ArrayList();
//        rs.put("children", rsList);
//
//        Map<String, Object> players = new HashMap<>();
//        rsList.add(players);
//        players.put("name", "直接玩家");
//        List<Object> playerList = new ArrayList<>();
//        players.put("children", playerList);
//
//        for (int i = 0; i < 10; i++){
//            DChildVo childVo = new DChildVo();
//            childVo.setName(i + "");
//            childVo.setValue(i);
//            playerList.add(childVo);
//        }
//
//        rs.put("name", "root");
//
//        Map<String, Object> rrss = ass();
//
//        AgentResponse agentResponse = new AgentResponse();
//        agentResponse.setData(rrss);
//        return agentResponse;
    }

    public String transformStr(long uid){
        User user = userDao.findOne(uid);
        String str ="ID:" + user.getId() + "名:" + user.getUsername();
        return str;
    }

    public String transformStr(User user){
        String str ="ID:" + user.getId() + "名:" + user.getUsername();
        return str;
    }

    public Map<String, Object> assembleDelegateRelationship(long agentId, List<User> aList, List<User> bList){

        Map<String, Object> nodeRoot = new HashMap<>();
        nodeRoot.put("name", transformStr(agentId));

        List<Object> childrenRoot = new ArrayList<>();
        nodeRoot.put("children", childrenRoot);

        Map<String, Object> node1_1 = new HashMap<>();
        childrenRoot.add(node1_1);
        node1_1.put("name", "直接玩家");

        List<Object> children1_1 = new ArrayList<>();
        node1_1.put("children", children1_1);

        //直接玩家
        for (User user : aList){
            DChildVo childVo = new DChildVo();
            childVo.setName(transformStr(user));
            childVo.setValue((int) user.getId());
            children1_1.add(childVo);
        }

        Map<String, Object> node1_2 = new HashMap<>();
        childrenRoot.add(node1_2);
        node1_2.put("name", "二级代理");

        List<Object> children1_2 = new ArrayList<>();
        node1_2.put("children", children1_2);

//        for (int i = 10; i < 20; i++){
//
//            Map<String, Object> node2_x = new HashMap<>();
//            node2_x.put("name", i);
//            children1_2.add(node2_x);
//
//            List<Object> child2_x = new ArrayList<>();
//            node2_x.put("children", child2_x);
//
//            for (int j = 100; j < 110; j++){
//                DChildVo childVo = new DChildVo();
//                childVo.setValue(j);
//                childVo.setName("三级代理" + j);
//                child2_x.add(childVo);
//            }
//        }

        //二级代理
        for (User user : bList){

            Map<String, Object> node2_x = new HashMap<>();
            node2_x.put("name", transformStr(user));
            children1_2.add(node2_x);

            List<Object> child2_x = new ArrayList<>();
            node2_x.put("children", child2_x);

//            //三级代理
//            for (int j = 100; j < 110; j++){
//                DChildVo childVo = new DChildVo();
//                childVo.setValue(j);
//                childVo.setName("三级代理" + j);
//                child2_x.add(childVo);
//            }

            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(user.getId());
            if (agentBean == null) continue;
            for (Long id : agentBean.getChildList()){
                DChildVo dChildVo = new DChildVo();
                dChildVo.setName(transformStr(id));
                child2_x.add(dChildVo);
            }

        }

        return nodeRoot;
    }

    public Map<String, Object> assDemo(){

        Map<String, Object> nodeRoot = new HashMap<>();
        nodeRoot.put("name", "self");

        List<Object> childrenRoot = new ArrayList<>();
        nodeRoot.put("children", childrenRoot);

        Map<String, Object> node1_1 = new HashMap<>();
        childrenRoot.add(node1_1);
        node1_1.put("name", "直接玩家");

        List<Object> children1_1 = new ArrayList<>();
        node1_1.put("children", children1_1);

        //直接玩家
        for (int i = 0; i < 5; i++){
            DChildVo childVo = new DChildVo();
            childVo.setName(i + "");
            childVo.setValue(i);
            children1_1.add(childVo);
        }

        Map<String, Object> node1_2 = new HashMap<>();
        childrenRoot.add(node1_2);
        node1_2.put("name", "二级代理");

        List<Object> children1_2 = new ArrayList<>();
        node1_2.put("children", children1_2);

        for (int i = 10; i < 20; i++){

            Map<String, Object> node2_x = new HashMap<>();
            node2_x.put("name", i);
            children1_2.add(node2_x);

            List<Object> child2_x = new ArrayList<>();
            node2_x.put("children", child2_x);

            for (int j = 100; j < 110; j++){
                DChildVo childVo = new DChildVo();
                childVo.setValue(j);
                childVo.setName("三级代理" + j);
                child2_x.add(childVo);
            }
        }

        //二级代理
        for (int i = 5; i < 10; i++){

            Map<String, Object> node2_x = new HashMap<>();
            node2_x.put("name", i + "");
            children1_2.add(node2_x);

            List<Object> child2_x = new ArrayList<>();
            node2_x.put("children", child2_x);

            //三级代理
            for (int j = 100; j < 110; j++){
                DChildVo childVo = new DChildVo();
                childVo.setValue(j);
                childVo.setName("三级代理" + j);
                child2_x.add(childVo);
            }
        }

        return nodeRoot;

    }

    public  Map<String, Object> ass(){

        Map<String, Object> rs = new HashMap<>();
        rs.put("name", "flare");

        List<Object> list = new ArrayList<>();
        rs.put("children", list);

        Map<String, Object> inner = new HashMap<>();
        list.add(inner);
        inner.put("name", "analytics");

        List<Object> analytics = new ArrayList<>();
        inner.put("children", analytics);

        Map<String, Object> map = new HashMap<>();
        map.put("name","cluster");

        analytics.add(map);

        List<Object> cluster = new ArrayList<>();
        map.put("children", cluster);

        for (int i = 0; i < 35; i++){
            DChildVo childVo = new DChildVo();
            childVo.setName(i + "");
            childVo.setValue(i);
            cluster.add(childVo);
        }

        return rs;
    }
    @DemoChecker
    @RequestMapping("/oFindCharge")
    public AgentResponse findChargeByOrderId(long orderId){
        Charge charge =  homeService.findChargeByOrderId(orderId);
        List<Charge> list = new ArrayList<>();

        AgentResponse agentResponse = new AgentResponse();
        if (charge == null){
            Map<String, Object> rs = new HashMap<>();
            rs.put("list", list);
            rs.put("total", 0);
            agentResponse.setData(rs);
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
        }else {
            list.add(charge);
            Map<String, Object> rs = new HashMap<>();
            rs.put("list", list);
            rs.put("total", 1);
            agentResponse.setData(rs);
//            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
        }
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/uFindCharge")
    public AgentResponse findChargeByUserId(long userId){

        Charge charge =  homeService.findChargeByUserId(userId);
        List<Charge> list = new ArrayList<>();

        AgentResponse agentResponse = new AgentResponse();
        if (charge == null){
            Map<String, Object> rs = new HashMap<>();
            rs.put("list", list);
            rs.put("total", 0);
            agentResponse.setData(rs);
            agentResponse.setMsg("没有记录");
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
        }else {
            list.add(charge);
            Map<String, Object> rs = new HashMap<>();
            rs.put("list", list);
            rs.put("total", 1);
            agentResponse.setData(rs);
        }
        return agentResponse;
    }
    @DemoChecker
    @RequestMapping("/chargeTimeSearch")
    public AgentResponse chargeTimerSearch(String time, int curPage){

        if (curPage > 0){
            curPage--;
        }
        String[] sA = null;
        if (time.contains(",")){
            sA = time.split(",", 1000);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> list = new ArrayList<>();

        Arrays.stream(sA)
                .forEach(x ->{
                    try {
                         list.add(simpleDateFormat.parse(x));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });

        Page<Charge> page = homeService.timeSearchCharges(list, new PageRequest(curPage, 20));

        AgentResponse agentResponse = new AgentResponse();
        Map<String, Object> rs = new HashMap<>();
        rs.put("list", page.getContent());
        rs.put("total", page.getTotalElements());
        agentResponse.setData(rs);

        return agentResponse;
    }

    @DemoChecker
    @RequestMapping("/changePwd")
    @Transactional
    public AgentResponse changePwd(String pwd, HttpServletRequest request){

        String token = AgentUtil.findTokenInHeader();
        int agentId = (int)AgentUtil.getUserIdByToken(token);
        AgentUser agentUser = agentUserDao.findOne(agentId);
        agentUser.setPassword(pwd);
        AgentUser au = agentUserDao.save(agentUser);
        if (au != null){
            AgentResponse agentResponse = new AgentResponse();
            return agentResponse;
        }else {
            AgentResponse agentResponse = new AgentResponse();
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            agentResponse.setMsg("修改失败");
            return agentResponse;
        }
    }
    @DemoChecker
    @RequestMapping("/findCharges")
    public AgentResponse findCharges(int curPage){
        if (curPage > 0){
            curPage--;
        }
        Page<Charge> page = homeService.findCharges(new PageRequest(curPage, 20));
        List<DChargeAdminVo> list = new ArrayList<>();
        page.getContent().stream()
                .forEach(x -> {
                    DChargeAdminVo dChargeAdminVo = new DChargeAdminVo();
                    BeanUtils.copyProperties(x , dChargeAdminVo);
                    list.add(dChargeAdminVo);
                });
        Long count = homeService.chargesCount();

        AgentResponse agentResponse = new AgentResponse();

        Map<String, Object> rs = new HashMap<>();
        rs.put("list", list);
        rs.put("total", count);
        agentResponse.setData(rs);

        return agentResponse;
    }

    @RequestMapping("/login")
    public AgentResponse agentLogin(HttpServletRequest request, HttpServletResponse response, String username, String password){

        AgentUser agentUser = agentUserDao.findAgentUserByUsernameAndPassword(username, password);
        AgentResponse agentResponse = null;
        Map<String, Object> result = new HashMap<>();
        if (agentUser != null){
            //todo token 和 玩家的关联
            Map<String, Object> rs = new HashMap<>();
            rs.put("id", agentUser.getId());
            rs.put("username", agentUser.getUsername());
            String token = getToken(agentUser.getId());
            //清除缓存
            AgentUtil.clearUserTokenByUserId(agentUser.getId());
            AgentUtil.caches.put(token, rs);

            agentResponse = new AgentResponse(0, result);
            Map<String, Object> rrr = new HashMap<>();
            rrr.put("token", token);
            return agentResponse.setData(rrr);

        }else {

            agentResponse = new AgentResponse(ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR,result);
            agentResponse.msg = "用户不存在";
        }
        return agentResponse;
    }

    @RequestMapping("/info")
    public AgentResponse userInfo(String token){
        //todo token 验证
        AgentUtil.caches.get(token);
        Map<String, Object> r = new HashMap<>();
        int[] roles = new int[]{1};
        r.put("userId", 1);
        r.put("roles", roles);
        return new AgentResponse(0, r);
    }
    @DemoChecker
    @RequestMapping("/onlineInfo")
    public AgentResponse onlineInfo(String date){
        //todo token 验证
        LogRecord logRecord = logRecordDao.findOne(date);
        return new AgentResponse(0, JsonUtil.toJson(logRecord));
    }

    @DemoChecker
    @RequestMapping("/getLogByDates")
    public AgentResponse getLogByDates(int num) {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>();
        for(int i=0;i<num;i++) {
            LocalDate temp = today.minusDays(i + 1);
            days.add(temp.toString());
        }

        return new AgentResponse(0, logRecordDao.findByIdIn(days));
    }

    @RequestMapping("/test")
    public Map<String, Object> test(){

        return AgentUtil.caches;
    }

    @RequestMapping("/ttt")
    public String hello(){
        return "Hello World";
    }

    public static void main(String[] args) {
//        LocalDate today = LocalDate.now();
//        for(int i=0;i<7;i++) {
//            LocalDate temp = today.minusDays(i + 1);
//            System.out.println(temp.toString());
//        }
//        Map<String, Object> oo = ass();
//        System.out.println(oo);
    }
}
