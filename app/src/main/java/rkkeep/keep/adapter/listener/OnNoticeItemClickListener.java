package rkkeep.keep.adapter.listener;

import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/4/27.
 */
public interface OnNoticeItemClickListener {
    void onClick(NoticeImgVoiceInfo info,int position);

    void onVoiceClick(NoticeImgVoiceInfo info,int position);

    void onVoiceLongClick(NoticeImgVoiceInfo info,int position);
}
