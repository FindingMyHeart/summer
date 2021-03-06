package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.ErrorCode;
import com.code.server.game.mahjong.util.HuType;
import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class GameInfoTJ extends GameInfo {

    public static final int mode_素本混龙 = 0;
    public static final int mode_拉龙五 = 1;
    public static final int mode_无杠黄庄 = 2;
    public static final int mode_金杠 = 3;
    public static final int mode_铲 = 4;
    public static final int mode_天胡 = 5;





    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;
        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        //打乱顺序
        Collections.shuffle(remainCards);
//        initHun(room);
        handleLaZhuang();
    }

    /**
     * 处理拉庄
     */
    protected void handleLaZhuang() {

        //初始化拉庄
        if (this.room.laZhuang.size() == 0) {
            this.users.forEach(userId -> {
                this.room.laZhuang.put(userId, 0);
                this.room.laZhuangStatus.put(userId, false);
            });
        }
        //如果庄家没选做不做庄
        if (!this.room.laZhuangStatus.get(firstTurn)) {
            //通知坐庄
            Map<String, Object> bid = new HashMap<>();
            bid.put("userId", firstTurn);
            MsgSender.sendMsg2Player("gameService", "zuoZhuang", this.room.laZhuang, users);
        } else {//选了并且还有未拉庄的

            boolean isNotice = this.room.laZhuang.keySet().stream().filter(id -> id != firstTurn && this.room.laZhuang.get(id) == 0).count() != 0;
            MsgSender.sendMsg2Player("gameService", "laZhuangAll", this.room.laZhuang, users);
            if (isNotice) {
            } else {
                initHun(room);
            }
        }
    }

    public int laZhuang(long userId, int num) {
        //是庄家
        if (userId == firstTurn) {
            //庄家已经选了
            if (this.room.laZhuangStatus.get(userId)) {
                return ErrorCode.CAN_NOT_LAZHUANG;
            }
            this.room.laZhuangStatus.put(userId, true);
            this.room.laZhuang.put(userId, num);
            //开始游戏
            initHun(room);

        } else {
            //都选择了拉庄 或者 都选择过
            this.room.laZhuangStatus.put(userId, true);
            this.room.laZhuang.put(userId, num);
            boolean isAllChoise = this.room.laZhuangStatus.values().stream().filter(s -> !s).count() == 0;
            boolean isAllLa = this.room.laZhuang.keySet().stream().filter(id -> id != firstTurn && this.room.laZhuang.get(id) == 0).count() == 0;

            boolean flag = true;
            for (Map.Entry<Long,Boolean> las : this.room.laZhuangStatus.entrySet()) {
                if (las.getKey() != firstTurn) {
                    if (!las.getValue() && this.room.laZhuang.get(las.getKey()) == 0) {
                        flag = false;
                    }
                }
            }
            if (flag) {
                initHun(room);
            }


        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("num", num);
        MsgSender.sendMsg2Player("gameService", "laZhuang", this.room.laZhuang, users);

        return 0;
    }

    protected boolean isRoomOver() {
        return room.getCurCircle() > room.maxCircle;
    }

    /**
     * 初始化混
     *
     * @param room
     */
    public void initHun(RoomInfo room) {
        //随机混
        Random rand = new Random();
        int hunIndex = 2 + rand.nextInt(11);
        List<String> hunRemoveCards = new ArrayList<>();
        for (int i = 0; i < hunIndex * 2; i++) {
            hunRemoveCards.add(remainCards.get(0));
            remainCards.remove(0);
        }

        //确定混
        String card = hunRemoveCards.get(0);
        hunRemoveCards.remove(0);
        this.hunRemoveCards = hunRemoveCards;
        int cardType = CardTypeUtil.getTypeByCard(card);
        //todo 刮大风
        this.hun = HuWithHun.getHunTypeGDF(cardType);

        //通知混
        MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);
        //发牌
        fapai();
    }


    @Override
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        //是否是杠后摸牌
        int size = playerCardsInfo.operateList.size();
        if (size > 0 && playerCardsInfo.operateList.get(size - 1) == PlayerCardsInfoMj.type_gang && chanCards.size() > 0) {
            return false;
        } else {
            return remainCards.size() == 0;
        }

    }

    @Override
    protected void handleHuangzhuang(long userId) {

        //算杠分
        computeAllGang();

        //无杠荒庄
        if (this.room.isHasMode(mode_无杠黄庄)) {
            boolean isHasGang = playerCardsInfos.values().stream().filter(playerInfo -> playerInfo.getScore() != 0).count() != 0;
            //没有杠的话 每个人给庄家2分
            if (!isHasGang) {
                PlayerCardsInfoTJ banker = (PlayerCardsInfoTJ) playerCardsInfos.get(this.firstTurn);
                banker.computeAddScore(2, this.firstTurn, false);
            }
        }
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);
        //庄家换下个人
        if (room instanceof RoomInfo) {
            RoomInfo roomInfo = (RoomInfo) room;
            if (roomInfo.isChangeBankerAfterHuangZhuang()) {
                room.setBankerId(nextTurnId(room.getBankerId()));

                //清理拉庄信息
                this.users.forEach(uid -> {
                    room.laZhuangStatus.put(uid, false);
                    room.laZhuang.put(uid, 0);
                });
            }

        }

    }


    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.userId, null);
        //圈
        if (this.getFirstTurn() != playerCardsInfo.getUserId()) {
            //换庄

            room.addOneToCircleNumber();
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);

            //清理拉庄信息

            this.users.forEach(uid -> {
                room.laZhuangStatus.put(uid, false);
                room.laZhuang.put(uid, 0);
            });
        }
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }

    /**
     * 摸一张牌
     *
     * @param playerCardsInfo
     * @return
     */
    @Override
    protected String getMoPaiCard(PlayerCardsInfoMj playerCardsInfo) {
        //拿出一张
        String card = null;
        //有换牌需求
        if (isTest && playerCardsInfo.nextNeedCard != -1) {
            String needCard = getCardByTypeFromRemainCards(playerCardsInfo.nextNeedCard);
            playerCardsInfo.nextNeedCard = -1;
            if (needCard != null) {
                card = needCard;
                remainCards.remove(needCard);
            } else {
                card = remainCards.remove(0);
            }
        } else {
            //是否是杠后摸牌
            int size = playerCardsInfo.operateList.size();
            //如果是杠后摸牌 从废牌里拿出一张
            if (size > 0 && playerCardsInfo.operateList.get(size - 1) == PlayerCardsInfoMj.type_gang && chanCards.size() > 0) {

                card = hunRemoveCards.remove(0);
            } else {
                card = remainCards.remove(0);
            }
        }
        return card;
    }


    @Override
    public int chupai(long userId, String card) {
        int rtn = super.chupai(userId, card);

        if (rtn == 0 && this.room.isHasMode(mode_铲)) {
            //铲
            if (chanCards.size() <= 7) {

                chanCards.add(card);
                int chanCardSize = chanCards.size();
                if (chanCardSize % this.users.size() == 0) {
                    if (isCardSame(chanCards.subList(chanCardSize - 4, chanCardSize))) {
                        //todo 通知有铲
                        //通知混
                        MsgSender.sendMsg2Player("gameService", "noticeChan", 0, users);
                    }

                }
            }
        }
        return rtn;
    }


    protected void doGang_hand_after(PlayerCardsInfoMj playerCardsInfo, boolean isMing, int userId, String card) {
        boolean isJinGang = this.hun.contains(CardTypeUtil.getTypeByCard(card));
        playerCardsInfo.gangCompute(room, this, isMing, -1, card);
        //金杠直接胡
        if (isJinGang) {
            playerCardsInfo.winType.add(HuType.hu_金杠);
            computeAllGang();
            handleHu(playerCardsInfo);
        } else {
            mopai(playerCardsInfo.getUserId(), "userId : " + playerCardsInfo.getUserId() + " 自摸杠抓牌");
        }
        turnId = playerCardsInfo.getUserId();


    }


    /**
     * 获得铲的个数
     *
     * @return
     */
    protected int getChanNum() {
        int result = 0;
        int chanNum = chanCards.size() / 4;

        if (chanNum > 1) {
            if (isCardSame(chanCards.subList(0, 4))) {
                result = 1;
            }
        }
        if (result == 1 && chanNum == 2) {
            if (isCardSame(chanCards.subList(4, 8))) {
                result = 2;
            }
        }
        return result;
    }
}
