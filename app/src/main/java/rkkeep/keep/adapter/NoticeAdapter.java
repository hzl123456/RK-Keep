package rkkeep.keep.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.adapter.HeaderFooterRecyclerViewAdapter;
import cn.xmrk.rkandroid.widget.MultiGridView;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import rkkeep.keep.R;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/4/27.
 */
public class NoticeAdapter extends HeaderFooterRecyclerViewAdapter {

    private List<NoticeImgVoiceInfo> mData;

    //列表总数
    private int contentSize;

    //每行的数目
    private int itemSize = 3;

    //多余的数目
    private int excessSize;


    public NoticeAdapter(List<NoticeImgVoiceInfo> mData) {
        this.mData = mData;
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

    public List<NoticeImgVoiceInfo> getMData() {
        return mData;
    }

    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected int getFooterItemCount() {
        return 1;
    }

    @Override
    protected int getContentItemCount() {
        return contentSize;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        return new BottomViewHolder(View.inflate(parent.getContext(), R.layout.activity_addnotice_top_content, null));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new ContentViewHolder(View.inflate(parent.getContext(), R.layout.item_addnotice_img_and_voice, null));
    }


    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {


    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        ContentViewHolder holder = (ContentViewHolder) contentViewHolder;
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
            public void onClick(NoticeImgVoiceInfo info) {
                if (mOnNoticeItemClickListener != null) {
                    mOnNoticeItemClickListener.onClick(info);
                }
            }
        });
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
     * 需要放在内容部分进行展示的viewHolder
     **/
    class ContentViewHolder extends RecyclerView.ViewHolder {
        public MultiGridView layoutContent;

        public ContentViewHolder(View itemView) {
            super(itemView);
            layoutContent = (MultiGridView) itemView.findViewById(R.id.layout_rv_content);
        }
    }


    private OnNoticeItemClickListener mOnNoticeItemClickListener;

    public void setOnNoticeItemClickListener(OnNoticeItemClickListener mOnNoticeItemClickListener) {
        this.mOnNoticeItemClickListener = mOnNoticeItemClickListener;
    }

}
