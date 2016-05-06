package rkkeep.keep.pojo;

import rkkeep.keep.adapter.viewholder.NoticeInfoBaseViewHolder;

/**
 * Created by Au61 on 2016/5/6.
 */
public class NoticeBaseInfo {
    public NoticeInfo info;

    public NoticeInfoBaseViewHolder holder;

    public int position;

    public NoticeBaseInfo(NoticeInfo info, NoticeInfoBaseViewHolder holder, int position) {
        this.info = info;
        this.holder = holder;
        this.position = position;
    }

}
