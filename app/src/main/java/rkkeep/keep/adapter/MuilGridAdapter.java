package rkkeep.keep.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.widget.MultiGridView;
import rkkeep.keep.R;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/5/5.
 */
public class MuilGridAdapter extends BaseAdapter {

    private List<NoticeImgVoiceInfo> mData;

    //列表总数
    private int contentSize;

    //每行的数目
    private int itemSize = 3;

    //多余的数目
    private int excessSize;

    public MuilGridAdapter(List<NoticeImgVoiceInfo> mData) {
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

    @Override
    public int getCount() {
        return contentSize;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_addnotice_img_and_voice, null);
            convertView.setTag(new ContentViewHolder(convertView));
        }
        ContentViewHolder holder = (ContentViewHolder) convertView.getTag();
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
        holder.layoutContent.setAdapter(new ImageViewAdapter(infos, false));
        return convertView;
    }


    class ContentViewHolder {
        public MultiGridView layoutContent;

        public ContentViewHolder(View itemView) {
            layoutContent = (MultiGridView) itemView.findViewById(R.id.layout_rv_content);
        }
    }
}
