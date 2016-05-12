package rkkeep.keep.util;

import android.os.Handler;
import android.os.Looper;

import com.gauss.speex.encode.MediaUtil.OnPlayFinish;

import rkkeep.keep.adapter.NoticeAdapter;


public class PlayCallback implements OnPlayFinish {


    private NoticeAdapter mAdapter;

    public PlayCallback(NoticeAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void onFinish() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                mAdapter.isPlayingInfo = null;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}