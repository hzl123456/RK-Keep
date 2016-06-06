package rkkeep.keep.adapter.listener;

import rkkeep.keep.pojo.VideoInfo;

/**
 * Created by Au61 on 2016/5/18.
 */
public interface OnVideoClickListener {
    void onClick(VideoInfo info, int position);

    void onLongClick(VideoInfo info, int position);
}
