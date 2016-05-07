package rkkeep.keep.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import rkkeep.keep.R;
import rkkeep.keep.activity.MainActivity;
import rkkeep.keep.adapter.MuilGridAdapter;
import rkkeep.keep.adapter.listener.OnNoticeBaseViewClickListener;
import rkkeep.keep.adapter.viewholder.NoticeInfoBaseViewHolder;
import rkkeep.keep.help.ChangeColorDialog;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.pojo.NoticeBaseInfo;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/4/26.
 */
public class JiShiFragment extends RecyclerViewFragment implements View.OnClickListener {

    private View titleView;
    private ImageButton ibSearch;
    private ImageButton ibLayout;
    private TextView tvTitleJishi;

    private MainActivity activity;
    private StaggeredGridLayoutManager layoutManager;


    private View titleEditView;
    private ImageButton ibColor;
    private ImageButton ibDelete;
    private TextView tvTitleEdit;


    private ChangeColorDialog mDialog;

    /**
     * 保存所有的处于drag状态下的holder，当为null的时候才可以进行点击事件
     **/
    private List<NoticeBaseInfo> dragHolder;

    @Override
    protected void initOnCreateView(boolean isCreate) {
        super.initOnCreateView(isCreate);
        if (isCreate) {
            dragHolder = new ArrayList<>();
            initTitle();
        }
    }

    private void initEditTitle() {
        if (titleEditView == null) {
            titleEditView = activity.getLayoutInflater().inflate(R.layout.title_jishi_edit, null);
            ibColor = (ImageButton) titleEditView.findViewById(R.id.ib_color);
            ibDelete = (ImageButton) titleEditView.findViewById(R.id.ib_delete);
            tvTitleEdit = (TextView) titleEditView.findViewById(R.id.tv_title);
            ibColor.setOnClickListener(this);
            ibDelete.setOnClickListener(this);
            CommonUtil.setLongClick(ibColor, activity.getString(R.string.change_color));
            CommonUtil.setLongClick(ibDelete, activity.getString(R.string.to_dustbin));
        }

        if (titleView != null && titleView.getParent() == activity.getTitleBar()) {
            activity.getTitleBar().removeView(titleView);
        }
        if (titleEditView != null && titleEditView.getParent() == activity.getTitleBar()) {

        } else {
            activity.getTitleBar().addView(titleEditView);
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
    }

    private void initTitle() {
        activity = (MainActivity) getActivity();
        if (titleView == null) {
            titleView = activity.getLayoutInflater().inflate(R.layout.title_jishi, null);
            ibSearch = (ImageButton) titleView.findViewById(R.id.ib_search);
            ibLayout = (ImageButton) titleView.findViewById(R.id.ib_layout);
            tvTitleJishi = (TextView) titleView.findViewById(R.id.tv_title);
            ibSearch.setOnClickListener(this);
            ibLayout.setOnClickListener(this);
            CommonUtil.setLongClick(ibSearch, activity.getString(R.string.search));
            CommonUtil.setLongClick(ibLayout, activity.getString(R.string.layout));
            tvTitleJishi.setText(R.string.nav_jishi);
        }

        if (titleEditView != null && titleEditView.getParent() == activity.getTitleBar()) {
            activity.getTitleBar().removeView(titleEditView);
        }
        if (titleView != null && titleView.getParent() == activity.getTitleBar()) {

        } else {
            activity.getTitleBar().addView(titleView);
            activity.getTitleBar().setBackgroundResource(R.color.bg_title_bar);
            activity.setViews();
        }
    }


    @Override
    public int getInfoType() {
        return NoticeInfo.NO_DUSTBIN;
    }

    @Override
    public int getPageSize() {
        return 10;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoticeInfoBaseViewHolder(View.inflate(parent.getContext(), R.layout.item_noticeinfo_base, null));
    }

    /**
     * 设置属性动画
     **/
    public void setAnimation(final NoticeBaseInfo baseInfo) {
        final View view = baseInfo.holder.itemView;
        //区域高度
        int screenHeight = CommonUtil.getScreenDisplay().getHeight() - activity.getTitleBar().getHeight() - CommonUtil.getStateBarHeight();
        //计算要放大的倍数
        float size = ((float) screenHeight) / view.getHeight();
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "scaleY", 1f, size);
        anim.setDuration(800);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                view.setScaleY(cVal);
                activity.toAddNoticeInfoActivity(baseInfo.info);
            }
        });
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final NoticeInfo info, int position) {
        final NoticeInfoBaseViewHolder holder = (NoticeInfoBaseViewHolder) viewHolder;
        holder.setBaseInfo(info, position);
        holder.setLayoutTop();
        //表示收到viewholder的点击事件
        holder.setOnViewHolderClickListener(new OnNoticeBaseViewClickListener() {
            @Override
            public void OnViewHolderClick(NoticeBaseInfo baseInfo) {
                if (dragHolder.size() == 0) {//当这个size为0的时候才可以是跳转
                    activity.toAddNoticeInfoActivity(baseInfo.info);
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
        if (layoutManager.getSpanCount() == 1) {
            holder.layoutNotice.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            holder.layoutNotice.setOrientation(LinearLayout.VERTICAL);
        }
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

    private void setTitle() {
        if (dragHolder.size() == 0) {
            initTitle();
        } else if (dragHolder.size() == 1) {
            initEditTitle();
        }
        if (dragHolder.size() > 0) {
            tvTitleEdit.setText(dragHolder.size() + "");
        }
    }

    private void setDragHolder(NoticeBaseInfo baseInfo) {
        baseInfo.info.isCheck = !baseInfo.info.isCheck;
        baseInfo.holder.setLayoutTop();
        if (baseInfo.info.isCheck) {
            dragHolder.add(baseInfo);
        } else {
            dragHolder.remove(baseInfo);
        }
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        int spacesSize = CommonUtil.dip2px(3);
        layoutManager = new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
        rvContent.addItemDecoration(new SpacesItemDecoration(spacesSize, spacesSize, spacesSize, spacesSize));
        return layoutManager;
    }

    @Override
    public void onClick(View v) {
        if (v == ibSearch) {//搜索


        } else if (v == ibLayout) {//改变布局
            if (mData != null && mData.size() > 0) {
                if (layoutManager.getSpanCount() == 1) {//此时为线性，所以要变成网格
                    layoutManager.setSpanCount(2);
                    ibLayout.setImageResource(R.drawable.ic_view_grid);
                } else {//此时为网格，所以变成线性
                    layoutManager.setSpanCount(1);
                    ibLayout.setImageResource(R.drawable.ic_view_stream);
                }
                mAdapter.notifyDataSetChanged();
            }
        } else if (v == ibDelete) {//移至回收站
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(rkkeep.keep.R.string.move_to_dustbin);
            dialog.setPositiveButton(cn.xmrk.rkandroid.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //用来保存将要remove的position
                    int size = mData.size();
                    NoticeInfo info = null;
                    for (int i = 0; i < dragHolder.size(); i++) {
                        //保存position，更新使用
                        info = dragHolder.get(i).info;
                        mNoticeInfoDbHelper.updateNoticeInfoType(info.infoId, NoticeInfo.NOMAL_TYPE_DUSTBIN);
                        mData.remove(info);
                    }
                    mAdapter.notifyItemRangeRemoved(0, size);
                    dragHolder.clear();
                    setTitle();
                    CommonUtil.showSnackToast(getActivity().getString(rkkeep.keep.R.string.had_move_to_dustbin), rvContent);
                }
            });
            dialog.setNegativeButton(cn.xmrk.rkandroid.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } else if (v == ibColor) {//更换颜色
            mDialog = new ChangeColorDialog(getActivity());
            mDialog.show();
            mDialog.setOnColorChooseListener(new ChangeColorDialog.OnColorChooseListener() {
                @Override
                public void OnChoose(String color) {
                    NoticeInfo info = null;
                    for (int i = 0; i < dragHolder.size(); i++) {
                        info = dragHolder.get(i).info;
                        info.color = color;
                        info.isCheck = false;
                        mNoticeInfoDbHelper.saveNoticeInfo(info);
                    }
                    dragHolder.clear();
                    setTitle();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
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
    public boolean canBackActivity() {
        if (dragHolder.size() > 0) {
            setNullDragHolder();
            return false;
        }
        return true;
    }
}
