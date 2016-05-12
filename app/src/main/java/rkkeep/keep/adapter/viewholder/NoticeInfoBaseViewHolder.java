package rkkeep.keep.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.xmrk.rkandroid.widget.MultiListView;
import rkkeep.keep.R;
import rkkeep.keep.adapter.listener.OnNoticeBaseViewClickListener;
import rkkeep.keep.pojo.NoticeBaseInfo;
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
}
