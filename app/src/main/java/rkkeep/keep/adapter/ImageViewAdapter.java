package rkkeep.keep.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.xmrk.rkandroid.utils.RKUtil;
import cn.xmrk.rkandroid.widget.imageView.ScaleImageView;
import rkkeep.keep.R;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/4/27.
 */
public class ImageViewAdapter extends BaseAdapter {

    private boolean needClick;

    private List<NoticeImgVoiceInfo> mData;

    public ImageViewAdapter(List<NoticeImgVoiceInfo> mData) {
        this(mData, true);
    }

    public ImageViewAdapter(List<NoticeImgVoiceInfo> mData, boolean needClick) {
        this.mData = mData;
        this.needClick = needClick;
    }


    private OnNoticeItemClickListener mOnNoticeItemClickListener;

    public void setOnNoticeItemClickListener(OnNoticeItemClickListener mOnNoticeItemClickListener) {
        this.mOnNoticeItemClickListener = mOnNoticeItemClickListener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.image, null);
            convertView.setTag(R.id.glide_tag, new ImageViewHolder(convertView));
        }
        final ImageViewHolder holder = (ImageViewHolder) convertView.getTag(R.id.glide_tag);

        RKUtil.displayFileImage(mData.get(position).imagePic, holder.image, 0);
        if (needClick) {
            holder.image.setEnabled(true);
            holder.image.setClickable(true);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNoticeItemClickListener != null) {
                        mOnNoticeItemClickListener.onClick(mData.get(position), position);
                    }
                }
            });
        } else {
            holder.image.setEnabled(false);
            holder.image.setClickable(false);
        }
        return convertView;
    }

    class ImageViewHolder {

        private ScaleImageView image;

        public ImageViewHolder(View itemView) {
            image = (ScaleImageView) itemView.findViewById(R.id.image);
        }
    }
}
