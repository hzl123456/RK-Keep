package rkkeep.keep.pojo;

import java.util.List;

/**
 * Created by Au61 on 2016/4/27.
 */
public class NoticeInfo {

    /**
     * 颜色色值，默认为白色
     **/
    public String color = "#ffffffff";

    /**
     * 编辑时间
     **/
    public long editTime;

    /**
     * 存储图片语音信息
     **/
    public List<NoticeImgVoiceInfo> infos;
    public String infosString;


}
