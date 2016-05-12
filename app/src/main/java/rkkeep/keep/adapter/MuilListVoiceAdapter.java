package rkkeep.keep.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import rkkeep.keep.R;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/5/12.
 */
public class MuilListVoiceAdapter extends BaseAdapter {

    //语音的长度
    private int chatMaxWidh;
    private int chatDefWidth;
    private int chatOneSe;

    private List<NoticeImgVoiceInfo> mData;

    private boolean isVertical;

    public MuilListVoiceAdapter(List<NoticeImgVoiceInfo> mData, boolean isVertical) {
        this.mData = mData;
        this.isVertical = isVertical;
        chatDefWidth = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_width);
        chatMaxWidh = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_max_width);
        chatOneSe = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_one_width);
        if (!isVertical) {
            chatDefWidth = chatDefWidth / 2;
            chatMaxWidh = chatMaxWidh / 2;
            chatOneSe = chatOneSe / 2;
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_voice, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        NoticeImgVoiceInfo info = mData.get(position);
        //设置语音长度
        long duration = (info.length / 1000) > 0 ? (info.length / 1000) : 1;
        holder.tvVoiceLength.setText(duration + "'");
        //设置显示的长度
        long width = (chatDefWidth + chatOneSe * (duration - 1)) > chatMaxWidh ? chatMaxWidh : (chatDefWidth + chatOneSe * (duration - 1));
        holder.layoutVoice.setLayoutParams(new LinearLayout.LayoutParams((int) width, LinearLayout.LayoutParams.WRAP_CONTENT));
        return convertView;
    }

    class ViewHolder {
        private LinearLayout layoutVoice;
        private TextView tvVoiceLength;

        public ViewHolder(View itemView) {
            layoutVoice = (LinearLayout) itemView.findViewById(R.id.layout_voice);
            tvVoiceLength = (TextView) itemView.findViewById(R.id.tv_voice_length);
        }
    }
}
