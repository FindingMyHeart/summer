package com.code.server.game.mahjong.logic;


import com.code.server.constant.data.DataManager;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.*;
import com.code.server.constant.response.*;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomInfo extends RoomExtendGold {


//    private static final Logger logger = Logger.getLogger("game");

    protected String modeTotal;
    protected String mode;
    protected Map<Integer, Long> bankerMap = new HashMap<>();
    protected Map<Integer, Integer> circleNumber = new HashMap<>();//圈数，key存圈数，value存庄家换人的次数
    protected int maxCircle;

    protected boolean isHasGangBlackList = true;

    protected Map<Long, Integer> laZhuang = new HashMap<>();
    protected Map<Long, Boolean> laZhuangStatus = new HashMap<>();
    protected Map<Long, Integer> huNum = new HashMap<>();
    protected Map<Long, Integer> dianPaoNum = new HashMap<>();
    protected Map<Long, Integer> lianZhuangNum = new HashMap<>();
    protected Map<Long, Integer> moBaoNum = new HashMap<>();
    //荒庄后是否换庄家
    private boolean isChangeBankerAfterHuangZhuang = false;
    protected boolean isYipaoduoxiang = false;


    protected String each = "";//4人平分房卡

    protected boolean canChi;
    protected boolean haveTing;


    public String getEach() {
        return each;
    }


    public void setEach(String each) {
        this.each = each;
    }


    /**
     *
     * @param roomId
     * @param userId
     * @param modeTotal
     * @param mode
     * @param multiple
     * @param gameNumber
     * @param personNumber
     * @param createUser
     * @param bankerId
     * @param mustZimo
     */
    public void init(String roomId, long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, long createUser, long bankerId, int mustZimo) {
        this.roomId = roomId;
        this.modeTotal = modeTotal;
        this.mode = mode;
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.personNumber = personNumber;
        this.createUser = createUser;
        this.bankerId = bankerId;
        this.isInGame = false;
        this.bankerMap.put(1, bankerId);
        this.maxCircle = gameNumber;
        this.circleNumber.put(1, 1);
        this.mustZimo = mustZimo;

        try {
            this.createNeedMoney = this.getNeedMoney();
        } catch (DataNotFoundException e) {
            this.createNeedMoney = 3;
            e.printStackTrace();
        }
        this.isAddGold = DataManager.data.getRoomDataMap().get(this.gameType).getIsAddGold() == 1;
        clubRoomSetId();
    }



    protected boolean isHasMode(int type) {
        int c = Integer.parseInt(this.mode);
        return (c & (1 << type)) >> type == 1;
    }

    public void setUserSocre(long userId, double score) {
        if (!userScores.containsKey(userId)) {
//            logger.error("===设置分数时出错 userId = "+userId +"users: "+userScores.toString());
            return;
        }
        double s = userScores.get(userId);
        userScores.put(userId, s + score);
    }

    public void clearReadyStatus(boolean isAddGameNum) {
//        GameManager.getInstance().remove(game);
        clearReadyStatusGoldRoom(isAddGameNum);
        this.setGame(null);

        this.setInGame(false);
        for (Map.Entry<Long, Integer> entry : this.userStatus.entrySet()) {
            entry.setValue(STATUS_JOIN);
        }
        if(isAddGameNum){

            this.curGameNumber += 1;
        }
        //每局的庄家
        this.bankerMap.put(curGameNumber, bankerId);

        //选择状态置成没选过
        this.users.forEach(uid -> {
            if (uid != bankerId) {
                laZhuangStatus.put(uid, false);
            }
        });


    }


    private GameInfo getGameInfoInstance() {
        switch (this.getGameType()) {
            case "SY"://松原麻将
                return new GameInfoSongYuan().setHasJieGangHu(true);
            case "JC"://进城麻将
                return new GameInfoJinCheng().setHasJieGangHu(true);
            case "SS"://盛世麻将
                return new GameInfoShengShi().setHasJieGangHu(true);
            case "124"://进城124麻将
                return new GameInfoJinCheng124().setHasJieGangHu(true);
            case "JCSS"://进城麻将盛世玩法
                return new GameInfoJinChengSS().setHasJieGangHu(true);
            case "JZ"://九州麻将
                return new GameInfoJiuZhou().setHasJieGangHu(true);
            case "JL":
            case "DS":
//            case "HT":
            case "DY"://大运
            case "BAIXING":
                return new GameInfo().setHasJieGangHu(true);
            case "TJ":
            case "DFH":
                return new GameInfoTJ().setHasJieGangHu(false);
            case "DH":
                return new GameInfoDonghu();
            case "NZZ":
                return new GameInfoNZZ().setHasJieGangHu(true);
            case "HELE":
                return new GameInfoHasChi().setHasJieGangHu(true);
            case "HM":
                return new GameInfoHM().setHasJieGangHu(true);
            case "BENGBU":
                return new GameInfoBengbu().setHasJieGangHu(true);
            case "NIUYEZI":
                this.setChangeBankerAfterHuangZhuang(true);
                return new GameInfoNiuyezi();
            case "HS":
                return new GameInfoHS().setHasJieGangHu(true);
            case "XXPB":
                return new GameInfoXXPB().setHasJieGangHu(true);
            case "XYKD":
                return new GameInfoXYKD().setHasJieGangHu(true);
            case "KXZHZ":
                return new GameInfoZhuohaozi().setHasJieGangHu(true);
            default:
                return new GameInfo();
        }
    }

    public void startGame() {

        //确定庄家
        if (this.bankerId == 0) {
            this.bankerId = users.get(0);
        }
        this.isInGame = true;

        if (this.gameType.equals("JC") && this.modeTotal.equals("124")) {
            this.gameType = "124";
        } else if (this.gameType.equals("JC") && this.modeTotal.equals("13")) {
            this.gameType = "JCSS";
        } else if(this.gameType.equals("CHUANQI")){
            this.gameType = "LQ";
        }
        GameInfo gameInfo = getGameInfoInstance();

        if(this.gameType.equals("HELE")){
            this.gameType = "LQ";
        }

        if (!isOpen && isCreaterJoin) {
            spendMoney();
        }
        //金币房的处理
        goldRoomStart();

        //游戏开始 代建房 去除定时解散
        if (!isOpen && !this.isCreaterJoin()) {
            GameTimer.removeNode(prepareRoomTimerNode);
        }

        gameInfo.init(0, this.bankerId, this.users, this);
//        gameInfo.fapai();
        this.game = gameInfo;


        //通知其他人游戏已经开始


        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", toJSONObjectOfGameBegin()), this.getUsers());
        pushScoreChange();
        notifyCludGameStart();
        this.isOpen = true;

        //记录局数
        RedisManager.getLogRedisService().addGameNum(getGameLogKeyStr(), 1);
    }


    //游戏开始
    public Map toJSONObjectOfGameBegin() {
        Map<String, Object> result = new HashMap<>();
        result.put("gameId", 0);
        result.put("roomId", this.roomId);

        result.put("currentBanker", ((GameInfo) this.game).getFirstTurn());
        result.put("gameNumber", this.getCurGameNumber());
        result.put("circleNum", this.getCurCircle());

        return result;
    }

    public List<UserOfResult> getUserOfResult() {
        // 结果类
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();

        long time = System.currentTimeMillis();
        for (UserBean eachUser : RedisManager.getUserRedisService().getUserBeans(this.users)) {
            UserOfResult resultObj = new UserOfResult();
            resultObj.setUsername(eachUser.getUsername());
            resultObj.setImage(eachUser.getImage());
            resultObj.setScores(this.userScores.get(eachUser.getId()) + "");
            resultObj.setUserId(eachUser.getId());
            resultObj.setTime(time);

            //设置胡牌次数

            if (this.getHuNum().containsKey(eachUser.getId())) {
                resultObj.setHuNum(this.getHuNum().get(eachUser.getId()));
            }
            if (this.getLianZhuangNum().containsKey(eachUser.getId())) {
                resultObj.setLianZhuangNum(this.getLianZhuangNum().get(eachUser.getId()));
            }
            if (this.getDianPaoNum().containsKey(eachUser.getId())) {
                resultObj.setDianPaoNum((this.getDianPaoNum().get(eachUser.getId())));
            }
            if (this.getMoBaoNum().containsKey(eachUser.getId())) {
                resultObj.setMoBaoNum(this.getMoBaoNum().get(eachUser.getId()));
            }
            userOfResultList.add(resultObj);

            //删除映射关系
//            RedisManager.getUserRedisService().moveFull2NotFullRoom(eachUser.getId());
        }
        return userOfResultList;
    }

    public void dissolutionRoom() {

        //算杠
        GameInfo gameInfo = (GameInfo) this.game;
        if (gameInfo != null && !gameInfo.isAlreadyHu) {
            gameInfo.computeAllGang();
        }

        RoomManager.removeRoom(this.roomId);

        // 结果类
        List<UserOfResult> userOfResultList = getUserOfResult();


        boolean isChange = scoreIsChange();
        if ((this.isInGame||!isCreaterJoin) && this.curGameNumber == 1 && !isChange) {
            drawBack();
            GameTimer.removeNode(this.prepareRoomTimerNode);
        }

        if (isChange && gameInfo != null) {
            gameInfo.genRecord();
        }

        this.isInGame = false;
        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResultList);


        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users);
//        serverContext.sendToOnlinePlayer(noticeEndResult, this.users);

        //战绩
        genRoomRecord();

    }



    public  TimerNode getDissolutionRoomTimerNode(){
        return new TimerNode(System.currentTimeMillis(), IGameConstant.ONE_HOUR, false, this::dissolutionRoom);
    }





    public void addHuNum(long userId) {
        if (huNum.containsKey(userId)) {
            huNum.put(userId, huNum.get(userId) + 1);
        } else {
            huNum.put(userId, 1);
        }
    }

    public void addLianZhuangNum(long userId) {
        if (lianZhuangNum.containsKey(userId)) {
            lianZhuangNum.put(userId, lianZhuangNum.get(userId) + 1);
        } else {
            lianZhuangNum.put(userId, 1);
        }
    }

    public void addDianPaoNum(long userId) {
        if (dianPaoNum.containsKey(userId)) {
            dianPaoNum.put(userId, dianPaoNum.get(userId) + 1);
        } else {
            dianPaoNum.put(userId, 1);
        }
    }

    public void addMoBaoNum(long userId) {
        if (moBaoNum.containsKey(userId)) {
            moBaoNum.put(userId, moBaoNum.get(userId) + 1);
        } else {
            moBaoNum.put(userId, 1);
        }
    }


    public Map<String, Object> toJSONObject() {
        Map<String, Object> result = new HashMap<>();
        result.put("roomType", this.roomType);
        result.put("roomId", this.roomId);
        result.put("modeTotal", this.modeTotal);
        result.put("mode", this.mode);
        result.put("multiple", this.multiple);
        result.put("gameNumber", this.gameNumber);
        result.put("personNumber", this.personNumber);
        result.put("createUser", this.createUser);
        result.put("userList", RedisManager.getUserRedisService().getUserBeans(this.users));
        result.put("mustZimo", this.mustZimo);
        result.put("each", this.each);//1是4个分开付，0是user付
        result.put("yipaoduoxiang", this.isYipaoduoxiang);
        result.put("canChi", this.canChi);
        result.put("haveTing", this.haveTing);
        result.put("clubId", this.getClubId());
        result.put("clubRoomModel", this.getClubRoomModel());


        return result;
    }


    /**
     * 设定文件
     *
     * @return void    返回类型
     * @throws
     * @Title: 添加1
     * @Creater: Clark
     * @Description:
     */
    public void addOneToCircleNumber() {
        int temp = getCurCircle();
        this.circleNumber.put(temp, this.circleNumber.get(temp) + 1);
        if (this.circleNumber.get(temp) >= 5) {//4人轮完，下一圈
            this.circleNumber.put(temp + 1, 1);
        }
    }

    public int getCurCircle() {
        int temp = 1;
        for (Integer i : this.circleNumber.keySet()) {
            if (i > temp) {
                temp = i;
            }
        }
        return temp;
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomInfoVo roomVo = new RoomInfoVo();
        roomVo.roomType = this.getRoomType();
        roomVo.createType = this.getCreateType();
        roomVo.roomId = this.getRoomId();
        roomVo.multiple = this.getMultiple();
        roomVo.gameNumber = this.getGameNumber();
        roomVo.createUser = this.getCreateUser();
        roomVo.userStatus.putAll(this.getUserStatus());
        roomVo.userScores.putAll(this.getUserScores());
        roomVo.curGameNumber = this.getCurGameNumber();
        roomVo.goldRoomType = this.getGoldRoomType();
        roomVo.goldRoomPermission = this.getGoldRoomPermission();
        roomVo.isLastDraw = this.isLastDraw();
        roomVo.drawForLeaveChip = this.getDrawForLeaveChip();
        roomVo.personNumber = this.getPersonNumber();
        roomVo.hasNine = this.getHasNine();
        roomVo.isOpen = this.isOpen;
        roomVo.yipaoduoxiang = this.isYipaoduoxiang;
        roomVo.canChi = this.canChi;
        roomVo.haveTing = this.haveTing;
        roomVo.setClubId(this.getClubId());
        roomVo.setClubRoomModel(this.getClubRoomModel());
        roomVo.setMode(this.getMode());
        roomVo.setModeTotal(this.getModeTotal());
        roomVo.setEach(this.getEach());
        roomVo.setMustZimo(this.mustZimo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.getGame() != null) {
            roomVo.game = this.game.toVo(userId);
        }
        return roomVo;
    }

    @Override
    public PrepareRoom getPrepareRoomVo() {
        PrepareRoomMj prepareRoom = new PrepareRoomMj();
        prepareRoom.createTime = System.currentTimeMillis();
        prepareRoom.personNumber = this.personNumber;
        prepareRoom.gameType = this.getGameType();
        prepareRoom.roomType = this.getRoomType();
        prepareRoom.roomId = this.roomId;
        prepareRoom.multiple = this.multiple;
        prepareRoom.gameNumber = this.gameNumber;
        prepareRoom.mode = this.mode;
        prepareRoom.modeTotal = this.modeTotal;
        prepareRoom.mustZimo = this.mustZimo;
        prepareRoom.yipaoduoxiang = this.isYipaoduoxiang;
        prepareRoom.canChi = this.canChi;
        prepareRoom.haveTing = this.haveTing;
        prepareRoom.clubId = this.getClubId();
        prepareRoom.clubRoomModel = this.getClubRoomModel();
        return prepareRoom;
    }

    @Override
    public GameLogKey getGameLogKey() {
        GameLogKey gameLogKey =  super.getGameLogKey();
        gameLogKey.getParams().put("mode", mode);
        gameLogKey.getParams().put("modeTotal", modeTotal);
        return gameLogKey;
    }

    public String getModeTotal() {
        return modeTotal;
    }

    public RoomInfo setModeTotal(String modeTotal) {
        this.modeTotal = modeTotal;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public RoomInfo setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public Map<Integer, Long> getBankerMap() {
        return bankerMap;
    }

    public RoomInfo setBankerMap(Map<Integer, Long> bankerMap) {
        this.bankerMap = bankerMap;
        return this;
    }

    public Map<Integer, Integer> getCircleNumber() {
        return circleNumber;
    }

    public RoomInfo setCircleNumber(Map<Integer, Integer> circleNumber) {
        this.circleNumber = circleNumber;
        return this;
    }

    public int getMaxCircle() {
        return maxCircle;
    }

    public RoomInfo setMaxCircle(int maxCircle) {
        this.maxCircle = maxCircle;
        return this;
    }

    public boolean isHasGangBlackList() {
        return isHasGangBlackList;
    }

    public RoomInfo setHasGangBlackList(boolean hasGangBlackList) {
        isHasGangBlackList = hasGangBlackList;
        return this;
    }

    public Map<Long, Integer> getHuNum() {
        return huNum;
    }

    public RoomInfo setHuNum(Map<Long, Integer> huNum) {
        this.huNum = huNum;
        return this;
    }

    public Map<Long, Integer> getDianPaoNum() {
        return dianPaoNum;
    }

    public RoomInfo setDianPaoNum(Map<Long, Integer> dianPaoNum) {
        this.dianPaoNum = dianPaoNum;
        return this;
    }

    public Map<Long, Integer> getLianZhuangNum() {
        return lianZhuangNum;
    }

    public RoomInfo setLianZhuangNum(Map<Long, Integer> lianZhuangNum) {
        this.lianZhuangNum = lianZhuangNum;
        return this;
    }

    public Map<Long, Integer> getMoBaoNum() {
        return moBaoNum;
    }

    public RoomInfo setMoBaoNum(Map<Long, Integer> moBaoNum) {
        this.moBaoNum = moBaoNum;
        return this;
    }


    public boolean isCanDissloution() {
        return isCanDissloution;
    }

    public RoomInfo setCanDissloution(boolean canDissloution) {
        isCanDissloution = canDissloution;
        return this;
    }

    public boolean isChangeBankerAfterHuangZhuang() {
        return isChangeBankerAfterHuangZhuang;
    }

    public RoomInfo setChangeBankerAfterHuangZhuang(boolean changeBankerAfterHuangZhuang) {
        isChangeBankerAfterHuangZhuang = changeBankerAfterHuangZhuang;
        return this;
    }

    public int getMustZimo() {
        return mustZimo;
    }

    public void setMustZimo(int mustZimo) {
        this.mustZimo = mustZimo;
    }

    public Map<Long, Integer> getLaZhuang() {
        return laZhuang;
    }

    public RoomInfo setLaZhuang(Map<Long, Integer> laZhuang) {
        this.laZhuang = laZhuang;
        return this;
    }

    public Map<Long, Boolean> getLaZhuangStatus() {
        return laZhuangStatus;
    }

    public RoomInfo setLaZhuangStatus(Map<Long, Boolean> laZhuangStatus) {
        this.laZhuangStatus = laZhuangStatus;
        return this;
    }

    public boolean isYipaoduoxiang() {
        return isYipaoduoxiang;
    }


    public RoomInfo setYipaoduoxiang(boolean yipaoduoxiang) {
        isYipaoduoxiang = yipaoduoxiang;
        return this;
    }

    public boolean isCanChi() {
        return canChi;
    }

    public void setCanChi(boolean canChi) {
        this.canChi = canChi;
    }

    public boolean isHaveTing() {
        return haveTing;
    }

    public void setHaveTing(boolean haveTing) {
        this.haveTing = haveTing;
    }




}
