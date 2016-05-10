package rkkeep.keep.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.utils.CommonUtil;
import rkkeep.keep.R;
import rkkeep.keep.activity.MainActivity;
import rkkeep.keep.adapter.MuilGridAdapter;
import rkkeep.keep.adapter.listener.OnNoticeBaseViewClickListener;
import rkkeep.keep.adapter.viewholder.NoticeInfoBaseViewHolder;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.pojo.NoticeBaseInfo;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/5/9.
 */
public class DustbinFragment extends RecyclerViewFragment implements View.OnClickListener {

    protected MainActivity activity;

    protected View titleView;
    protected ImageButton ibSearch;
    protected ImageButton ibLayout;
    protected TextView tvTitleJishi;

    /**
     * 保存所有的处于drag状态下的holder，当为null的时候才可以进行点击事件
     **/
    protected List<NoticeBaseInfo> dragHolder;


    @Override
    public int getInfoType() {
        return NoticeInfo.NOMAL_TYPE_DUSTBIN;
    }

    @Override
    public int getPageSize() {
        return 10;
    }

    @Override
    protected void initOnCreateView(boolean isCreate) {
        super.initOnCreateView(isCreate);
        if (isCreate) {
            activity = (MainActivity) getActivity();
            dragHolder = new ArrayList<>();
            initTitleView();
        }
        setTitle();
    }

    protected void initTitle() {
        activity.getTitleBar().removeAllViews();
        ibSearch.setVisibility(View.GONE);
        tvTitleJishi.setText(R.string.nav_huishouzhan);
        ibSearch.setBackgroundResource(R.drawable.btn_click_ripple_title_color_636363);
        ibLayout.setBackgroundResource(R.drawable.btn_click_ripple_title_color_636363);

        activity.getTitleBar().addView(titleView);
        activity.getTitleBar().setBackgroundResource(R.color.color_636363);
        activity.setViews();
    }

    private void initTitleView() {
        titleView = activity.getLayoutInflater().inflate(R.layout.title_jishi, null);
        ibSearch = (ImageButton) titleView.findViewById(R.id.ib_search);
        ibLayout = (ImageButton) titleView.findViewById(R.id.ib_layout);
        tvTitleJishi = (TextView) titleView.findViewById(R.id.tv_title);
        ibSearch.setOnClickListener(this);
        ibLayout.setOnClickListener(this);
        CommonUtil.setLongClick(ibSearch, activity.getString(R.string.remove_out));
        CommonUtil.setLongClick(ibLayout, activity.getString(R.string.delete_fornever));
        ibSearch.setImageResource(R.drawable.ic_material_untrash_light);
        ibLayout.setImageResource(R.drawable.ic_material_trash_white);
    }

    protected void initEditTitle() {
        activity.getTitleBar().removeAllViews();
        ibSearch.setVisibility(View.VISIBLE);
        ibSearch.setBackgroundResource(R.drawable.btn_click_ripple_title_color_9b9b9b);
        ibLayout.setBackgroundResource(R.drawable.btn_click_ripple_title_color_9b9b9b);
        activity.getTitleBar().addView(titleView);
        activity.getTitleBar().setBackgroundResource(R.color.color_9b9b9b);
        activity.getTitleBar().setNavigationIcon(R.drawable.ic_white_back);
        activity.getTitleBar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回记事页面，将所有选中的设置为未选中的状态
                setNullDragHolder();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void setTitle() {
        if (dragHolder.size() == 0) {
            initTitle();
        } else if (dragHolder.size() == 1) {
            initEditTitle();
        }
        if (dragHolder.size() > 0) {
            tvTitleJishi.setText(dragHolder.size() + "");
        }
        setNullDelete();
    }

    protected void setDragHolder(NoticeBaseInfo baseInfo) {
        baseInfo.info.isCheck = !baseInfo.info.isCheck;
        baseInfo.holder.setLayoutTop();
        if (baseInfo.info.isCheck) {
            dragHolder.add(baseInfo);
        } else {
            dragHolder.remove(baseInfo);
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoticeInfoBaseViewHolder(View.inflate(parent.getContext(), R.layout.item_noticeinfo_base, null));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, NoticeInfo info, int position) {
        final NoticeInfoBaseViewHolder holder = (NoticeInfoBaseViewHolder) viewHolder;
        holder.setBaseInfo(info, position);
        holder.setLayoutTop();
        //表示收到viewholder的点击事件
        holder.setOnViewHolderClickListener(new OnNoticeBaseViewClickListener() {
            @Override
            public void OnViewHolderClick(NoticeBaseInfo baseInfo) {
                if (dragHolder.size() == 0) {//当这个size为0的时候才可以是跳转
                    CommonUtil.showSnackToast(activity.getString(R.string.cannot_deit), rvContent);
                } else {
                    setDragHolder(baseInfo);
                }
                setTitle();
            }

            @Override
            public void OnViewHolderLongClick(NoticeBaseInfo baseInfo) {
                if (dragHolder.size() == 0) {
                    mItemTouchHelper.startDrag(holder);
                }
                setDragHolder(baseInfo);
                setTitle();
            }

            @Override
            public void OnViewHolderRemove(NoticeBaseInfo baseInfo) {

            }
        });
        //设置背景颜色
        holder.layoutBae.setBackgroundResource(ColorHelper.getCheckColorRound(info.color));
        //设置标题和内容
        holder.tvTitle.setText(info.title);
        holder.tvContent.setText(info.content);
        //设置布局形式
        holder.layoutNotice.setOrientation(LinearLayout.HORIZONTAL);
        //设置提醒时间
        if (info.remindTime == 0) {
            holder.tvNoticeTime.setVisibility(View.GONE);
        } else {
            holder.tvNoticeTime.setVisibility(View.VISIBLE);
            holder.tvNoticeTime.setText(CommonUtil.getAffineTimestampForGroupChat(info.remindTime));
        }
        //设置提醒地址
        if (info.addressInfo == null) {
            holder.tvNoticeAddress.setVisibility(View.GONE);
        } else {
            holder.tvNoticeAddress.setVisibility(View.VISIBLE);
            holder.tvNoticeAddress.setText(info.addressInfo.addressName);
        }
        holder.rvContent.setAdapter(new MuilGridAdapter(info.infos));
    }

    public boolean isLongPressDragEnabled() {
        return false;
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSuccess(List<NoticeInfo> infos) {
        if (isRefesh) {
            canBackActivity();
        }
        super.onSuccess(infos);
    }

    @Override
    public boolean canBackActivity() {
        if (dragHolder.size() > 0) {
            setNullDragHolder();
            return false;
        }
        return true;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return super.getLayoutManager();
    }

    //将dragholder设置为空
    public void setNullDragHolder() {
        for (int i = 0; i < dragHolder.size(); i++) {
            dragHolder.get(i).info.isCheck = false;
        }
        dragHolder.clear();
        setTitle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == ibSearch) {//这个表示的是移出回收站
            int size = mData.size();
            NoticeInfo info = null;
            int type = NoticeInfo.NOMAL_TYPE;
            for (int i = 0; i < dragHolder.size(); i++) {
                info = dragHolder.get(i).info;
                if (info.remindTime != 0 || info.addressInfo != null) {
                    type = NoticeInfo.TIXING_TYPE;
                }
                mNoticeInfoDbHelper.updateNoticeInfoType(info.infoId, type);
                mData.remove(info);
            }
            dragHolder.clear();
            mAdapter.notifyItemRangeRemoved(0, size);
            setTitle();
            setNullDelete();
            showDataOrEmpty();
            CommonUtil.showSnackToast(activity.getString(R.string.notice_ago), rvContent);
        } else if (v == ibLayout) {//这个表示的是永久删除
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            if (dragHolder.size() == 0) {
                dialog.setTitle(R.string.remove_all_dustbin);
                dialog.setMessage(R.string.delete_all_noticeinfo);
            } else {
                dialog.setMessage(R.string.delete_choose_noticeinfo);
            }
            dialog.setPositiveButton(cn.xmrk.rkandroid.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dragHolder.size() > 0) {
                        int size = mData.size();
                        NoticeInfo info = null;
                        for (int i = 0; i < dragHolder.size(); i++) {
                            //保存position，更新使用
                            info = dragHolder.get(i).info;
                            mNoticeInfoDbHelper.deleteOneNoticeInfo(info);
                            mData.remove(info);
                        }
                        dragHolder.clear();
                        mAdapter.notifyItemRangeRemoved(0, size);
                        setTitle();
                    } else {
                        for (int i = 0; i < mData.size(); i++) {
                            mNoticeInfoDbHelper.deleteOneNoticeInfo(mData.get(i));
                        }
                        if (mData != null) {
                            mData.clear();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    setNullDelete();
                    showDataOrEmpty();
                }
            });
            dialog.setNegativeButton(cn.xmrk.rkandroid.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void setNullDelete() {
        if (mNoticeInfoDbHelper.getMessageCount(getInfoType()) == 0) {
            ibLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getEmptyString() {
        return getActivity().getString(R.string.no__dustbin);
    }

    @Override
    protected int getEmptyResourse() {
        return R.drawable.ic_empty_trash;
    }
}
