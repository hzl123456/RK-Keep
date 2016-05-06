package rkkeep.keep.adapter.listener;

import rkkeep.keep.pojo.NoticeBaseInfo;

/**
 * Created by Au61 on 2016/5/6.
 */
public interface OnNoticeBaseViewClickListener {
    void OnViewHolderClick(NoticeBaseInfo baseInfo);

    void OnViewHolderLongClick(NoticeBaseInfo baseInfo);

    void OnViewHolderRemove(NoticeBaseInfo baseInfo);
}
