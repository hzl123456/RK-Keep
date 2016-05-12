package rkkeep.keep.db;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;
import rkkeep.keep.pojo.AddressInfo;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.NoticeInfo;
import rkkeep.keep.util.UserInfoUtil;

/**
 * Created by Au61 on 2016/5/3.
 */
public class NoticeInfoDbHelper {

    private static final Logger log = Logger.getLogger("noticeInfo");

    public String dbKey;

    public int msgOwner;

    OpenHelper mOpenHelper;

    public NoticeInfoDbHelper() {
        this("default", UserInfoUtil.getUserInfo().userId);
    }

    public NoticeInfoDbHelper(String dbKey, int msgOwner) {
        this(RKApplication.getInstance(), dbKey, msgOwner);
    }

    public NoticeInfoDbHelper(Context context, String dbKey, int msgOwner) {
        this.dbKey = dbKey;
        this.msgOwner = msgOwner;
        open(context);
    }

    public void open(Context context) {
        if (mOpenHelper == null || !mOpenHelper.isOpen()) {
            mOpenHelper = OpenHelper.getInstance(context, dbKey);
        }
    }

    public void close() {
        if (mOpenHelper != null) {
            mOpenHelper.close();
        }

    }

    public int getMsgOwner() {
        return msgOwner;
    }

    public Dao getNoticeInfoDao() {
        return mOpenHelper.getNoticeInfoDao();
    }


    /**
     * 保存消息到数据库,如果要用这个方法来更新数据,NoticeInfo对象里不修改的数据应该以原来的数据进行保存,否则会丢失
     *
     * @param info
     */
    public Dao.CreateOrUpdateStatus saveNoticeInfo(NoticeInfo info) {
        info.ownerId = getMsgOwner();
        Dao _dao = getNoticeInfoDao();
        try {
            return _dao.createOrUpdate(info);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("消息列表更新失败", e);
            return new Dao.CreateOrUpdateStatus(false, false, 0);
        }
    }

    public int saveNoticeInfoList(List<NoticeInfo> list) {
        Dao _dao = getNoticeInfoDao();
        try {
            return _dao.create(list);
        } catch (SQLException e) {
            log.error("消息列表更新失败", e);
            return 0;
        }
    }

    /**
     * 修改一条消息的类型
     *
     * @param infoId
     * @param infoType
     * @return
     */
    public int updateNoticeInfoType(long infoId, int infoType) {
        int _uc = 0;
        Dao _dao = getNoticeInfoDao();
        try {
            UpdateBuilder _ub = _dao.updateBuilder();
            _ub.setWhere(_ub.where().eq("infoId", infoId));
            _uc += _ub.updateColumnValue("infoType", infoType).update();
            return _uc;
        } catch (SQLException e) {
            log.error("更新失败", e);
            return 0;
        }
    }

    /**
     * 将一条信息移至回收站
     *
     * @param infoId
     * @return
     */
    public int updateNoticeInfoTypeToDustbin(String infoId) {
        int _uc = 0;
        Dao _dao = getNoticeInfoDao();
        try {
            UpdateBuilder _ub = _dao.updateBuilder();
            _ub.setWhere(_ub.where().eq("infoId", infoId));
            _uc += _ub.updateColumnValue("infoType", NoticeInfo.NOMAL_TYPE_DUSTBIN).update();
            return _uc;
        } catch (SQLException e) {
            log.error("更新失败", e);
            return 0;
        }
    }

    /**
     * 删除一条信息
     **/
    public void deleteOneNoticeInfo(NoticeInfo info) {
        Dao _dao = getNoticeInfoDao();
        DeleteBuilder<NoticeInfo, Integer> _db = _dao.deleteBuilder();
        Where<NoticeInfo, Integer> _where = _db.where();
        try {
            _where.eq("infoId", info.infoId);
            _db.delete();
        } catch (SQLException e) {
            log.error("数据删除失败", e);
        }
    }

    /**
     * 交换两个的infoId
     **/
    public void changeTwoNoticeInfoId(NoticeInfo info1, NoticeInfo info2) {
        //先删除这两条消息，然后将infoId+1进行保存
        deleteOneNoticeInfo(info1);
        deleteOneNoticeInfo(info2);
        //交换infoId，并且加1
        long infoId = info1.infoId + 1;
        info1.infoId = info2.infoId + 1;
        info2.infoId = infoId;
        //从新保存下
        saveNoticeInfo(info1);
        saveNoticeInfo(info2);
    }

    /**
     * 统计当前账号的信息的数量
     *
     * @param infoType 为-1的时候，取的是0和1，，
     * @return
     */
    public long getMessageCount(int infoType) {
        Dao _dao = getNoticeInfoDao();
        try {
            Where _where = _dao.queryBuilder().setCountOf(true).where()
                    .eq("ownerId", getMsgOwner());

            if (infoType == NoticeInfo.NO_DUSTBIN) {
                _where.and().eq("infoType", NoticeInfo.NOMAL_TYPE).or().eq("infoType", NoticeInfo.TIXING_TYPE);
            } else {
                _where.and().eq("infoType", infoType);
            }
            return _dao.countOf(_where.prepare());
        } catch (SQLException e) {
            log.error("获取失败", e);
            return 0;
        }
    }

    /**
     * 根据infoid获取infotype
     */
    public int getNoticeInfoType(long infoId) {
        Dao _dao = getNoticeInfoDao();
        try {
            QueryBuilder<NoticeInfo, Integer> _qb = _dao.queryBuilder();
            Where _where = _qb.where()
                    .eq("ownerId", getMsgOwner()).and().eq("infoId", infoId);
            return _qb.query().get(0).infoType;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取失败", e);
            return 0;
        }
    }

    /**
     * 获取当前账号的一定数量的信息
     *
     * @param infoType
     * @param infoId   最早的时间，传0表示最近的，因为消息的id就是时间毫秒值，其实也是一个排序的顺序
     * @param limit    为0不做限制不为0就做限制
     * @return
     */
    public List<NoticeInfo> getNoticeInfoList(int infoType, long infoId, long limit) {
        //当infoType为-1的时候表示的是非垃圾箱里面的
        Log.i("info-->", infoType + "_" + infoId + "_" + limit);
        Dao _dao = getNoticeInfoDao();
        try {
            long _count = 0;
            //先实例化一个查询器
            QueryBuilder<NoticeInfo, Integer> _qb = _dao.queryBuilder();
            //先求出满足要求的总的数量
            Where<NoticeInfo, Integer> _where = _qb.where();
            _where.eq("ownerId", getMsgOwner());
            //如果infoId>0的话就是取比他早的消息
            if (infoId > 0) {
                _where.and().lt("infoId", infoId);
            }
            if (infoType == NoticeInfo.NO_DUSTBIN) {
                _count = _where.and().not().eq("infoType", NoticeInfo.NOMAL_TYPE_DUSTBIN).countOf();
            } else {
                _count = _where.and().eq("infoType", infoType).countOf();
            }
            //计算将要取出的数量
            limit = limit > _count ? _count : limit;
            _qb.limit(limit);
            _qb.setWhere(_where);
            //进行从大到小的排序
            _qb.orderBy("infoId", false);

            List<NoticeInfo> _result = _qb.query();
            Log.i("infos-->", CommonUtil.getGson().toJson(_result));
            //如果查询结果数量不为0，需要对一些信息进行转换
            if (_result != null && _result.size() > 0) {
                for (NoticeInfo info : _result) {
                    if (!StringUtil.isEmptyString(info.noticeImgVoiceInfosString)) {
                        info.infos = CommonUtil.getGson().fromJson(info.noticeImgVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                    if (!StringUtil.isEmptyString(info.addressInfoString)) {
                        info.addressInfo = CommonUtil.getGson().fromJson(info.addressInfoString, AddressInfo.class);
                    }
                    if (!StringUtil.isEmptyString(info.noticeVoiceInfosString)) {
                        info.voiceInfos = CommonUtil.getGson().fromJson(info.noticeVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                }
            }
            return _result;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询失败", e);
            return null;
        }
    }

    /**
     * 获取需要提醒的消息，并且已经提醒过了
     **/
    public List<NoticeInfo> getTiXingNoticeInfoList() {
        Dao _dao = getNoticeInfoDao();
        try {
            QueryBuilder<NoticeInfo, Integer> _qb = _dao.queryBuilder();
            Where _where = _qb.where()
                    .eq("ownerId", getMsgOwner()).and().eq("infoType", NoticeInfo.TIXING_TYPE).and().eq("noticeTimes", 0);
            //进行从大到小的排序
            _qb.orderBy("infoId", false);
            List<NoticeInfo> _result = _qb.query();
            //如果查询结果数量不为0，需要对一些信息进行转换
            if (_result != null && _result.size() > 0) {
                for (NoticeInfo info : _result) {
                    if (!StringUtil.isEmptyString(info.noticeImgVoiceInfosString)) {
                        info.infos = CommonUtil.getGson().fromJson(info.noticeImgVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                    if (!StringUtil.isEmptyString(info.addressInfoString)) {
                        info.addressInfo = CommonUtil.getGson().fromJson(info.addressInfoString, AddressInfo.class);
                    }
                    if (!StringUtil.isEmptyString(info.noticeVoiceInfosString)) {
                        info.voiceInfos = CommonUtil.getGson().fromJson(info.noticeVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                }
            }
            return _result;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("获取失败", e);
            return null;
        }
    }

    /**
     * 根据内容获取信息
     *
     * @param etInfo,（查找的内容，这边用标题和内容去匹配他）
     * @param hasVoice,（是否包含语音）
     * @param hasPic,（是否包含图片）
     * @param color,（信息的颜色,当color为null的时候表示所有颜色都可以）
     * @param isAllType,                            (按type值进行判断)
     **/
    public List<NoticeInfo> getNoticeInfoList(String etInfo, boolean hasVoice, boolean hasPic, String color, boolean isAllType) {
        Dao _dao = getNoticeInfoDao();
        try {
            QueryBuilder<NoticeInfo, Integer> _qb = _dao.queryBuilder();
            Where _where = _qb.where()
                    .eq("ownerId", getMsgOwner());
            if (hasVoice) {
                _where.and().eq("hasVoice", hasVoice);
            }
            if (hasPic) {
                _where.and().eq("hasPic", hasPic);
            }
            if (isAllType) {
                _where.and().not().eq("infoType", NoticeInfo.NOMAL_TYPE_DUSTBIN);
            } else {
                _where.and().eq("infoType", NoticeInfo.TIXING_TYPE);
            }
            //当颜色不为空的时候需要判断颜色
            if (!StringUtil.isEmptyString(color)) {
                _where.and().eq("color", color);
            }
            //当etInfo不为空的时候需要判断etInfo
            if (!StringUtil.isEmptyString(etInfo)) {
                _where.and().like("title", etInfo).or().like("content", etInfo);
            }
            _qb.setWhere(_where);
            //进行从大到小的排序
            _qb.orderBy("infoId", false);
            List<NoticeInfo> _result = _qb.query();
            //如果查询结果数量不为0，需要对一些信息进行转换
            if (_result != null && _result.size() > 0) {
                for (NoticeInfo info : _result) {
                    if (!StringUtil.isEmptyString(info.noticeImgVoiceInfosString)) {
                        info.infos = CommonUtil.getGson().fromJson(info.noticeImgVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                    if (!StringUtil.isEmptyString(info.addressInfoString)) {
                        info.addressInfo = CommonUtil.getGson().fromJson(info.addressInfoString, AddressInfo.class);
                    }
                    if (!StringUtil.isEmptyString(info.noticeVoiceInfosString)) {
                        info.voiceInfos = CommonUtil.getGson().fromJson(info.noticeVoiceInfosString, NoticeImgVoiceInfo.getListType());
                    }
                }
            }
            return _result;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("获取失败", e);
            return null;
        }
    }

}
