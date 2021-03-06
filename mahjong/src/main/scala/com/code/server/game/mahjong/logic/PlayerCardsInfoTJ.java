package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class PlayerCardsInfoTJ extends PlayerCardsInfoMj {


    public static List<Integer> zhuo5AndLong = new ArrayList<>();

    static {
        zhuo5AndLong.add(hu_捉五);
        zhuo5AndLong.add(hu_混儿吊捉五);

        zhuo5AndLong.add(hu_龙);
        zhuo5AndLong.add(hu_本混龙);

        zhuo5AndLong.add(hu_捉五龙);
        zhuo5AndLong.add(hu_本混捉五龙);

        zhuo5AndLong.add(hu_混儿吊龙);
        zhuo5AndLong.add(hu_混儿吊本混龙);

        zhuo5AndLong.add(hu_混儿吊捉五龙);
        zhuo5AndLong.add(hu_混儿吊捉五本混龙);

        zhuo5AndLong.add(hu_素本混龙);
        zhuo5AndLong.add(hu_素本混捉五龙);
    }


    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_混吊, 2);

        specialHuScore.put(hu_捉五, 3);
        specialHuScore.put(hu_混儿吊捉五, 6);

        specialHuScore.put(hu_龙, 4);
        specialHuScore.put(hu_本混龙, 8);

        specialHuScore.put(hu_捉五龙, 7);
        specialHuScore.put(hu_本混捉五龙, 14);

        specialHuScore.put(hu_混儿吊龙, 8);
        specialHuScore.put(hu_混儿吊本混龙, 16);

        specialHuScore.put(hu_混儿吊捉五龙, 14);
        specialHuScore.put(hu_混儿吊捉五本混龙, 50);

        specialHuScore.put(hu_素, 2);


        if (this.roomInfo.isHasMode(GameInfoTJ.mode_素本混龙)) {
            specialHuScore.put(hu_素本混龙, 16);
            specialHuScore.put(hu_素本混捉五龙, 28);
        }


    }

    @Override
    public boolean isCanHu_zimo(String card) {
        //小相公
        if (this.isPlayHun) {
            return false;
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();

        //是否是杠开
        boolean isGangKai = isGangKai();

        //是否是素胡
        boolean isSuHu = isSuHu();

        //是否是天胡 只有庄家能天胡
        boolean isTianHu = this.userId == this.gameInfo.getFirstTurn() && this.operateList.size() == 1 && this.operateList.get(0) == type_mopai && this.roomInfo.isHasMode(GameInfoTJ.mode_天胡);


        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        HuCardType maxHuType = getMaxScoreHuCardType(huList);

//        if(maxHuType.fan > 0 || isGangKai || isSuHu || isTianHu)
        if (huList.size() > 0) {
            List<Integer> huTypeList = new ArrayList<>(maxHuType.specialHuList);
            huTypeList.retainAll(zhuo5AndLong);
            return huTypeList.size() > 0 || isGangKai || isSuHu || isTianHu;
        } else {
            return false;
        }


    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        //算杠分
        gameInfo.computeAllGang();

        //铲
        computeChan();

        System.out.println("===========房间倍数============ " + room.getMultiple());
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);

        //是否是杠开
        boolean isGangKai = isGangKai();

        //是否是天胡
        boolean isTianHu = this.userId == this.gameInfo.getFirstTurn() && this.operateList.size() == 1 && this.operateList.get(0) == type_mopai && this.roomInfo.isHasMode(GameInfoTJ.mode_天胡);

        boolean isSuHu = isSuHu();

        HuCardType maxHuType = getMaxScoreHuCardType(huList);

        boolean isTiliu = maxHuType.fan == 0 && !isSuHu;

        //是否是素胡
        if (maxHuType.fan == 0) {
            maxHuType.fan = isTiliu ? 1 : 1;
        }

//        if (isGangKai && isTiliu) maxHuType.fan = 2;

        int score = maxHuType.fan;

        if (isGangKai) score *= 2;

        if (isSuHu) score *= 2;
        if (isTianHu) score *= 4;

        setWinTypeResult(maxHuType);
        if (isGangKai) this.winType.add(HuType.hu_杠开);
        if (isSuHu) this.winType.add(HuType.hu_素);
        if (isTianHu) this.winType.add(HuType.hu_天胡);

        // 拉龙五 翻倍
        if (this.roomInfo.isHasMode(GameInfoTJ.mode_拉龙五)) {
            List<Integer> huTypeList = new ArrayList<>(maxHuType.specialHuList);
            huTypeList.retainAll(zhuo5AndLong);
            if (huTypeList.size() > 0) {
                score *= 2;
            }
        }

        computeAddScore(score, this.getUserId(), false);
    }

    /**
     * 计算铲
     */
    public void computeChan() {
        GameInfoTJ gameInfoTJ = (GameInfoTJ) this.gameInfo;
        int size = gameInfoTJ.getChanNum();
        int score = size * 1;
        computeAddScore(-score, this.gameInfo.getFirstTurn(), true);
    }


    public void computeAddScore(int score, long userId, boolean isGang) {

        PlayerCardsInfoMj own = this.gameInfo.playerCardsInfos.get(userId);
        boolean isBankerWin = userId == this.gameInfo.getFirstTurn();
        //是庄家
        if (userId == this.gameInfo.getFirstTurn()) {
            score *= 2;
            //庄家拉庄

            if (this.roomInfo.laZhuang.get(userId) > 0) {
                score *= (1 << this.roomInfo.laZhuang.get(userId));
            }
        }

        int subScore = 0;
        //其他人赔付
        for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.playerCardsInfos.values()) {

            if (playerCardsInfo.getUserId() == userId) {
                continue;
            }

            int myScore = score;
            //是庄家
            if (playerCardsInfo.getUserId() == this.gameInfo.getFirstTurn()) {
                myScore *= 2;

            }

            //拉庄
            if ((playerCardsInfo.getUserId() == this.gameInfo.getFirstTurn() || isBankerWin) && this.roomInfo.laZhuang.get(playerCardsInfo.getUserId()) > 0) {
                myScore *= 1 << this.roomInfo.laZhuang.get(playerCardsInfo.getUserId());
            }

            //庄家输 再输 赢得人的拉庄倍数
            if(playerCardsInfo.getUserId() == this.gameInfo.getFirstTurn() && !isBankerWin){
                myScore *= 1<< this.roomInfo.laZhuang.get(userId);
            }

            //房间倍数
            myScore *= this.roomInfo.getMultiple();

            //减去杠分
            if (isGang) playerCardsInfo.addGangScore(-myScore);
            playerCardsInfo.addScore(-myScore);
            this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -myScore);
            subScore += myScore;
        }

        //自己加上杠分
        if (isGang) own.addGangScore(subScore);
        own.addScore(subScore);
        this.roomInfo.addUserSocre(this.getUserId(), subScore);


    }

    @Override
    public void computeALLGang() {
        int gangScore = 0;
        gangScore += this.mingGangType.size();
        for (int gangType : this.anGangType) {
            boolean isJinGang = this.gameInfo.hun.contains(gangType);
            if (isJinGang) {
                gangScore += 8;
            } else {
                gangScore += 2;
            }
        }
        computeAddScore(gangScore, this.userId, true);
    }

    /**
     * 是否是素和
     *
     * @return
     */
    protected boolean isSuHu() {
        boolean isHasHun = false;
        for (String card : this.cards) {
            int cardType = CardTypeUtil.getTypeByCard(card);
            if (this.gameInfo.hun.contains(cardType)) {
                isHasHun = true;
            }
        }
        return !isHasHun;
    }




    @Override
    public boolean isHasGang() {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set set = getHasGangList(temp);

        //是否带金杠
        if (!this.roomInfo.isHasMode(GameInfoTJ.mode_金杠)) {
            set.removeAll(this.gameInfo.hun);
        }
        return set.size() > 0;
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        //不能杠混
        return !this.gameInfo.hun.contains(CardTypeUtil.getTypeByCard(card)) && super.isCanGangAddThisCard(card);
    }


    @Override
    public boolean isCanPengAddThisCard(String card) {
        //不能碰混
        return !this.gameInfo.hun.contains(CardTypeUtil.getTypeByCard(card)) && super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isCanChiThisCard(String card, String one, String two) {
        return false;
    }

    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isCanChiTing(String card) {
        return false;
    }

    @Override
    public boolean isCanPengTing(String card) {
        return false;
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        return false;
    }


    @Override
    public boolean isCanTing(List<String> cards) {
        return false;
    }

    private static void change() {
        String s = "077, 080, 085, 017, 029, 014, 001, 090, 004, 009, 022, 026, 079, 030";
        String result = "";
        for (String ss : s.split(",")) {
            result = result + "\"" + ss.trim() + "\",";
        }
        System.out.println(result);
    }

    public static void main(String[] args) {
        PlayerCardsInfoTJ playerCardsInfo = new PlayerCardsInfoTJ();

        change();



        playerCardsInfo.isHasFengShun = true;


        String[] s = new String[]{"072", "076", "080", "092","093", "094", "096",    "100",  "104",    "004", "005", "016", "020","024"};
//        String[] s = new String[]{"112", "113", "114",   "024",   "028", "032",  "088", "092", "096",  "097",    "132", "133", "124", "120"};

        List<Integer> hun = new ArrayList<>();
        hun.add(0);
        hun.add(1);
        hun.add(8);


        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("1023");
        GameInfoTJ gameInfoTJ = new GameInfoTJ();
        gameInfoTJ.hun = hun;
        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.setGameInfo(gameInfoTJ);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.init(playerCardsInfo.cards);


//        playerCardsInfo.pengType.put(18,0L);
//        playerCardsInfo.pengType.put(30,0L);

        List<String> list = Arrays.asList(s);
        playerCardsInfo.cards.addAll(list);

        List<HuCardType> huList = HuUtil.isHu(playerCardsInfo,
                playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.cards),
                playerCardsInfo.getChiPengGangNum(), hun, 23);
        boolean isCanHu = playerCardsInfo.isCanHu_zimo("092");
        System.out.println("是否可以胡: " + isCanHu);
        huList.forEach(h -> System.out.println(h.specialHuList));
//        System.out.println(huList);


    }
}
