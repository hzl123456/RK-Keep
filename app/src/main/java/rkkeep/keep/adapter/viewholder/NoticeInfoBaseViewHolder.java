package rkkeep.keep.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.widget.MultiListView;
import rkkeep.keep.R;
import rkkeep.keep.adapter.listener.OnNoticeBaseViewClickListener;
import rkkeep.keep.pojo.NoticeBaseInfo;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/5/5.
 */
public class NoticeInfoBaseViewHolder extends RecyclerView.ViewHolder {

    private NoticeBaseInfo baseInfo;

    public void setBaseInfo(NoticeInfo info, int position) {
        baseInfo = new NoticeBaseInfo(info, this, position);
    }

    private OnNoticeBaseViewClickListener listener;

    public void setOnViewHolderClickListener(OnNoticeBaseViewClickListener listener) {
        this.listener = listener;
    }

    public MultiListView lvVoiceContent;
    public LinearLayout layoutTop;
    public TextView tvTitle;
    public TextView tvContent;
    public LinearLayout layoutNotice;
    public TextView tvNoticeTime;
    public TextView tvNoticeAddress;
    public LinearLayout layoutBae;
    public MultiListView rvContent;
    public LinearLayout layoutVoiceContent;


    public NoticeInfoBaseViewHolder(View itemView) {
        super(itemView);
        layoutTop = (LinearLayout) itemView.findViewById(R.id.layout_top);
        layoutBae = (LinearLayout) itemView.findViewById(R.id.layout_base);
        rvContent = (MultiListView) itemView.findViewById(R.id.rv_content);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        layoutNotice = (LinearLayout) itemView.findViewById(R.id.layout_notice);
        tvNoticeTime = (TextView) itemView.findViewById(R.id.tv_notice_time);
        tvNoticeAddress = (TextView) itemView.findViewById(R.id.tv_notice_address);
        lvVoiceContent = (MultiListView) itemView.findViewById(R.id.lv_voice_content);
        layoutVoiceContent = (LinearLayout) itemView.findViewById(R.id.layout_voice_content);


        layoutBae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnViewHolderClick(baseInfo);
                }
            }
        });

        layoutBae.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.OnViewHolderLongClick(baseInfo);
                }
                return true;
            }
        });
    }

    public void setLayoutTop() {
        if (baseInfo.info.isCheck) {
            layoutTop.setBackgroundResource(R.drawable.bg_layout_checked);
        } else {
            layoutTop.setBackgroundColor(0);
        }
    }

    /***********************************
     * 超级烦躁。。。语音的
     ****************************************/
    //语音的长度
    private int chatMaxWidh;
    private int chatDefWidth;
    private int chatOneSe;

    public View getView(NoticeImgVoiceInfo info) {
        View view = View.inflate(RKApplication.getInstance(), R.layout.item_voice, null);
        LinearLayout layoutVoice = (LinearLayout) view.findViewById(R.id.layout_voice);
        TextView tvVoiceLength = (TextView) view.findViewById(R.id.tv_voice_length);
        //设置语音长度
        long duration = (info.length / 1000) > 0 ? (info.length / 1000) : 1;
        tvVoiceLength.setText(duration + "'");
        //设置显示的长度
        long width = (chatDefWidth + chatOneSe * (duration - 1)) > chatMaxWidh ? chatMaxWidh : (chatDefWidth + chatOneSe * (duration - 1));
        layoutVoice.setLayoutParams(new LinearLayout.LayoutParams((int) width, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }

    public void setNoticeVoiceInfo(List<NoticeImgVoiceInfo> mData, boolean isVertical) {
        chatDefWidth = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_width);
        chatMaxWidh = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_max_width);
        chatOneSe = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_one_width);
        if (!isVertical) {
            chatDefWidth /= 2;
            chatMaxWidh /= 2;
            chatOneSe /= 2;
        }
        if (mData != null && mData.size() > 0) {
            layoutVoiceContent.removeAllViews();
            for (int i = 0; i < mData.size(); i++) {
                layoutVoiceContent.addView(getView(mData.get(i)));
            }
        }
    }
}
