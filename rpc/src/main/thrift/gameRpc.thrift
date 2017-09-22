namespace java com.code.server.rpc.idl
namespace py gameIdl

struct User {
  1: i64 id = 0,
  2: string username,
  7: double money,
  13:double gold,

}

enum ChargeType{
      money = 1,
      gold = 2
}
struct Order{
    1:i64 userId,
    2:double num,
    3:i32 type,//chargeType
    4:string token,
    5:i32 agentId,
    6:i64 id,
}

struct OnlineNum{
    1:i32 userNum,
    2:i32 roomNum,
}

service GameRPC{
    //充值
    i32 charge(1:Order order),

   //获得用户信息
   User getUserInfo(1:i64 userId),

   //交易库存斗
   i32 exchange(1:Order order),

    //修改公告
   i32 modifyMarquee(1:string str),
    //修改下载地址
   i32 modifyDownload(1:string str),
    //修改安卓版本
   i32 modifyAndroidVersion(1:string str),
    //修改ios版本
   i32 modifyIOSVersion(1:string str),
   //shutdown
   i32 shutdown(),
   //修改初始金钱
   i32 modifyInitMoney(1:i32 money),
   //是否苹果审查
   i32 modifyAppleCheck(1:i32 status),

   i32 modifyDownload2(1:string str),

   i32 addBlackList(1:i64 userId),

   i32 removeBlackList(1:i64 userId),

   set<i64> getBlackList();

   OnlineNum getOnlineUser();

   i32 bindReferee(1:i64 userId,2:i32 referee);
}

