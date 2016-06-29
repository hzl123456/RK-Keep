package rkkeep.keep.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.adapter.HeaderFooterRecyclerViewAdapter;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.utils.RKUtil;
import cn.xmrk.rkandroid.widget.MultiGridView;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import cn.xmrk.rkandroid.widget.imageView.ScaleImageView;
import rkkeep.keep.R;
import rkkeep.keep.activity.AddNoticeActivity;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.adapter.listener.OnVideoClickListener;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.VideoInfo;

/**
 * Created by Au61 on 2016/4/27.
 */
public class NoticeAdapter extends HeaderFooterRecyclerViewAdapter {

    private final int VIDEO_TYPE = 0;
    private final int VOICE_TYPE = 1;


    //语音的长度
    private int chatMaxWidh;
    private int chatDefWidth;
    private int chatOneSe;

    //视频的高度
    private int videoHeight;

    //正在播放的消息
    public NoticeImgVoiceInfo isPlayingInfo = null;

    private AnimationDrawable animationDrawable;

    //录音信息使用
    private List<NoticeImgVoiceInfo> voiceData;

    //图片信息使用
    private List<NoticeImgVoiceInfo> mData;

    //视频使用
    private List<VideoInfo> mVideoInfos;

    //列表总数
    private int contentSize;

    //每行的数目
    private int itemSize = 3;

    //多余的数目
    private int excessSize;

    /**
     * 需要外部操作的view
     ***/
    private View bottomView;

    private AddNoticeActivity activity;

    public NoticeAdapter(List<NoticeImgVoiceInfo> mData, List<NoticeImgVoiceInfo> voiceData, List<VideoInfo> mVideoInfos, AddNoticeActivity activity) {
        this.mData = mData;
        this.voiceData = voiceData;
        this.activity = activity;
        this.mVideoInfos = mVideoInfos;
        chatDefWidth = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_width);
        chatMaxWidh = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_max_width);
        chatOneSe = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.voice_def_one_width);
        videoHeight = RKApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.video_height);
        setContentSize();
    }


    public void setContentSize() {
        if (mData != null && mData.size() != 0) {
            if ((mData.size() % itemSize) == 0) {
                contentSize = mData.size() / itemSize;
            } else {
                contentSize = mData.size() / itemSize + 1;
                excessSize = mData.size() % itemSize;
            }
        } else {
            contentSize = 0;
            excessSize = 0;
        }
    }

    public void addDatas(NoticeImgVoiceInfo info) {
        mData.add(0, info);
        setContentSize();
        notifyDataSetChanged();
    }

    public int getContentSize() {
        return contentSize;
    }

    public List<NoticeImgVoiceInfo> getMData() {
        return mData;
    }

    @Override
    protected int getHeaderItemCount() {
        return contentSize;
    }

    @Override
    protected int getFooterItemCount() {
        return 1;
    }

    @Override
    protected int getContentItemCount() {
        return (mVideoInfos == null ? 0 : mVideoInfos.size()) + (voiceData == null ? 0 : voiceData.size());
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return new HeaderViewHolder(View.inflate(parent.getContext(), R.layout.item_addnotice_img_and_voice, null));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        bottomView = View.inflate(parent.getContext(), R.layout.activity_addnotice_top_content, null);
        activity.initBottomView(bottomView);
        return new BottomViewHolder(bottomView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        if (contentViewType == VIDEO_TYPE) {
            return new ContentVideoViewHolder(View.inflate(parent.getContext(), R.layout.item_video, null));
        } else {
            return new ContentViewHolder(View.inflate(parent.getContext(), R.layout.item_voice, null));
        }
    }

    @Override
    protected int getContentItemViewType(int position) {
        //视频的大小
        int videoSize = mVideoInfos == null ? 0 : mVideoInfos.size();
        if (position < videoSize) {
            return VIDEO_TYPE;
        } else {
            return VOICE_TYPE;
        }
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {
        //处理图片数据
        HeaderViewHolder holder = (HeaderViewHolder) headerViewHolder;
        List<NoticeImgVoiceInfo> infos = null;
        ImageViewAdapter adapter = null;
        if ((mData.size() % itemSize) == 0) {//如果是整除的话就都是每行itemsize个
            holder.layoutContent.setNumColumns(itemSize);
            infos = new ArrayList<>();
            for (int i = 0; i < itemSize; i++) {
                infos.add(mData.get((position) * itemSize + i));
            }
        } else {//非整除状态下，position=0的时候是excessSize，其余是itemSize个
            if (position == 0) {//表示的是剩余的
                holder.layoutContent.setNumColumns(excessSize);
                infos = new ArrayList<>();
                for (int i = 0; i < excessSize; i++) {
                    infos.add(mData.get(i));
                }
            } else {
                holder.layoutContent.setNumColumns(itemSize);
                infos = new ArrayList<>();
                for (int i = 0; i < itemSize; i++) {
                    infos.add(mData.get((position - 1) * itemSize + i + excessSize));
                }
            }
        }
        adapter = new ImageViewAdapter(infos);
        holder.layoutContent.setAdapter(adapter);

        adapter.setOnNoticeItemClickListener(new OnNoticeItemClickListener() {
            @Override
            public void onClick(NoticeImgVoiceInfo info, int position) {
                if (mOnNoticeItemClickListener != null) {
                    mOnNoticeItemClickListener.onClick(info, position);
                }
            }

            @Override
            public void onVoiceClick(NoticeImgVoiceInfo info, int position) {

            }

            @Override
            public void onVoiceLongClick(NoticeImgVoiceInfo info, int position) {

            }
        });
    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {


    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, final int position) {
        if (getContentItemViewType(position) == VIDEO_TYPE) {//视频的
            final VideoInfo info = mVideoInfos.get(position);
            final ContentVideoViewHolder holder = (ContentVideoViewHolder) contentViewHolder;
            holder.tvVideoName.setText(info.videoName);

            RKUtil.displayFileImage(info.videoPath, holder.ivImage, 0);

            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnVideoClickListener != null) {
                        mOnVideoClickListener.onClick(info, position);
                    }

                }
            });
            holder.ivImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnVideoClickListener != null) {
                        mOnVideoClickListener.onLongClick(info, position);
                    }
                    return true;
                }
            });
        } else {
            //处理语音数据
            ContentViewHolder holder = (ContentViewHolder) contentViewHolder;
            NoticeImgVoiceInfo info = voiceData.get(position - (mVideoInfos==null?0:mVideoInfos.size()));
            holder.info = info;
            holder.position = position;
            //设置语音长度
            long duration = (info.length / 1000) > 0 ? (info.length / 1000) : 1;
            holder.tvVoiceLength.setText(duration + "'");
            //设置显示的长度
            long width = (chatDefWidth + chatOneSe * (duration - 1)) > chatMaxWidh ? chatMaxWidh : (chatDefWidth + chatOneSe * (duration - 1));
            holder.layoutVoice.setLayoutParams(new LinearLayout.LayoutParams((int) width, LinearLayout.LayoutParams.WRAP_CONTENT));
            //是否处于播放状态
            if (info == isPlayingInfo) {
                holder.ivVoice.setBackgroundResource(R.drawable.show_voice);
                animationDrawable = (AnimationDrawable) holder.ivVoice.getBackground();
                animationDrawable.setOneShot(false);
                animationDrawable.start();
            } else {
                holder.ivVoice.setBackgroundResource(R.drawable.ease_chatfrom_voice_playing);
            }
        }
    }


    /**
     * 需要放在recycle底部进行展示的viewHolder
     **/
    class BottomViewHolder extends RecyclerView.ViewHolder {

        public ClearEditText etTitle;
        public ClearEditText etContent;

        public BottomViewHolder(View itemView) {
            super(itemView);
            etTitle = (ClearEditText) itemView.findViewById(R.id.et_title);
            etContent = (ClearEditText) itemView.findViewById(R.id.et_content);
        }

    }

    /**
     * 需要放在头部部分进行展示的viewHolder，给图片进行使用的
     **/
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public MultiGridView layoutContent;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            layoutContent = (MultiGridView) itemView.findViewById(R.id.layout_rv_content);
        }
    }

    /**
     * 显示视频信息的viewHolder
     **/
    class ContentVideoViewHolder extends RecyclerView.ViewHolder {

        public ScaleImageView ivImage;
        public TextView tvVideoName;

        public ContentVideoViewHolder(View itemView) {
            super(itemView);
            ivImage = (ScaleImageView) itemView.findViewById(R.id.image);
            tvVideoName = (TextView) itemView.findViewById(R.id.tv_video_name);
        }
    }

    /**
     * 需要在中间部分进行显示的viewHolder，给录音进行使用的
     **/
    class ContentViewHolder extends RecyclerView.ViewHolder {

        public int position;
        public NoticeImgVoiceInfo info;

        private LinearLayout layoutVoice;
        private ImageButton ivVoice;
        private TextView tvVoiceLength;

        public ContentViewHolder(View itemView) {
            super(itemView);
            layoutVoice = (LinearLayout) itemView.findViewById(R.id.layout_voice);
            ivVoice = (ImageButton) itemView.findViewById(R.id.iv_voice);
            tvVoiceLength = (TextView) itemView.findViewById(R.id.tv_voice_length);

            layoutVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNoticeItemClickListener != null) {
                        mOnNoticeItemClickListener.onVoiceClick(info, position);
                    }
                }
            });
            layoutVoice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnNoticeItemClickListener != null) {
                        mOnNoticeItemClickListener.onVoiceLongClick(info, position);
                    }
                    return true;
                }
            });
        }
    }


    private OnNoticeItemClickListener mOnNoticeItemClickListener;

    public void setOnNoticeItemClickListener(OnNoticeItemClickListener mOnNoticeItemClickListener) {
        this.mOnNoticeItemClickListener = mOnNoticeItemClickListener;
    }

    private OnVideoClickListener mOnVideoClickListener;

    public void setOnVideoClickListener(OnVideoClickListener mOnVideoClickListener) {
        this.mOnVideoClickListener = mOnVideoClickListener;
    }

}
