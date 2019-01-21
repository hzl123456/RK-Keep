package rkkeep.keep.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.fragment.BaseFragment;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.uil.RecyclerViewPauseOnScrollListener;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import rkkeep.keep.adapter.HeaderFooterRecyclerViewDragSwioAdapter;
import rkkeep.keep.adapter.helper.SimpleItemTouchHelperCallback;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.pojo.NoticeInfo;


/**
 * RecyclerView 的Activity
 */
public abstract class RecyclerViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 下拉刷新使用
     **/
    private SwipeRefreshLayout swRefresh;

    /**
     * 是否处于刷新状态
     **/
    protected boolean isRefesh;

    /**
     * 是否有提示过
     **/
    private boolean hasNotices;
    /**
     * 进行drag和swip使用
     **/
    protected ItemTouchHelper mItemTouchHelper;
    /**
     * 显示在recycleView上的数据
     **/
    protected List<NoticeInfo> mData;

    private boolean isLoading;

    private boolean isLoadmoreEnable = true;

    protected HeaderFooterRecyclerViewDragSwioAdapter mAdapter;

    /**
     * 加载更多的FooterView的显示类型<br/>
     * 0为不需要显示<br/>
     * 1为加载更多<br/>
     * 2为没有数据<br/>
     */
    protected int mFooterType = 1;

    /**
     * 数据存放的位置
     */
    protected RecyclerView rvContent;

    /**
     * 数据为空的时候显示
     **/
    protected TextView tvEmpty;

    /**
     * 数据为空的时候显示
     **/
    protected ScrollView rvScrollow;

    /**
     * 加载数据库使用
     **/
    protected NoticeInfoDbHelper mNoticeInfoDbHelper;


    /**
     * 加载更多容器
     */
    private ViewSwitcher containerLoadmore;


    public long getInfoId() {
        if (isRefesh) {
            return 0;
        }
        if (mData != null && mData.size() > 0) {
            return mData.get(mData.size() - 1).infoId;
        }
        return 0;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.layout_recyclerview;
    }

    @Override
    protected void initOnCreateView(boolean isCreate) {
        super.initOnCreateView(isCreate);
        if (isCreate) {
            findViews();
            initViews();
        }
        removeFromParent(containerLoadmore);
        showDataOrEmpty();
    }

    public void onSuccess(List<NoticeInfo> infos) {
        if (isRefesh && mData != null) {
            mData.clear();
            isRefesh = false;
        }
        // 加载成功
        isLoading = false;
        handlerDataResult(infos);
        if (swRefresh.isRefreshing()) {
            swRefresh.setRefreshing(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void setRefeshEnable(boolean isEnable) {
        swRefresh.setEnabled(isEnable);
    }

    public boolean isLongPressDragEnabled() {
        return false;
    }

    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public void onError(String errorToast) {
        isLoading = false;
        //弹窗告知失败原因
        CommonUtil.showToast(errorToast);
        setBtnLoadmoreShow();
        showDataOrEmpty();
    }

    /**
     * 让加载更多的按钮显示出来
     */
    private void setBtnLoadmoreShow() {
        if (containerLoadmore != null) {
            containerLoadmore.setDisplayedChild(1);
        }
    }

    /**
     * 处理加载完成以后的数据
     *
     * @param _newData
     */
    private void handlerDataResult(List<NoticeInfo> _newData) {
        if (_newData != null && _newData.size() > 0) {
            if (mData == null) {
                mData = new ArrayList<NoticeInfo>();
            }
            if (_newData != null) {
                mData.addAll(_newData);
            }
            if (hasMore()) {
                isLoadmoreEnable = true;
                mFooterType = 1;
            } else {
                isLoadmoreEnable = false;
                mFooterType = 0;
            }
        }
        // 判断数据是否为空
        if ((mData == null || mData.isEmpty()) && mFooterType != 0) {
            // 显示没有数据
            mFooterType = 2;
        }
        showDataOrEmpty();
    }

    public boolean hasMore() {
        return (mData == null ? 0 : mData.size()) < mNoticeInfoDbHelper.getMessageCount(getInfoType());
    }

    private void findViews() {
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        swRefresh = (SwipeRefreshLayout) findViewById(R.id.sf_refresh);
        rvScrollow = (ScrollView) findViewById(R.id.rv_scrollow);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        tvEmpty.setText(getEmptyString());
        tvEmpty.setCompoundDrawablesWithIntrinsicBounds(0, getEmptyResourse(), 0, 0);
        swRefresh.setOnRefreshListener(this);
        //设置滚动不加载图片
        rvContent.setOnScrollListener(new RecyclerViewPauseOnScrollListener(true, true));
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        int spacesSize = CommonUtil.dip2px(3);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
        rvContent.addItemDecoration(new SpacesItemDecoration(spacesSize, spacesSize, spacesSize, spacesSize));
        return layoutManager;
    }

    protected void initViews() {
        // 水平模式
        rvContent.setLayoutManager(getLayoutManager());
        mNoticeInfoDbHelper = new NoticeInfoDbHelper();
        mAdapter = new HeaderFooterRecyclerViewDragSwioAdapter() {

            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                //更换位置
                mNoticeInfoDbHelper.changeTwoNoticeInfoId(mData.get(fromPosition), mData.get(toPosition));
                Collections.swap(mData, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(final int position) {
                //将数据移动到回收站
                if (hasNotices) {
                    mNoticeInfoDbHelper.updateNoticeInfoType(mData.get(position).infoId, NoticeInfo.NOMAL_TYPE_DUSTBIN);
                    mData.remove(position);
                    notifyItemRemoved(position);
                    showDataOrEmpty();
                    CommonUtil.showSnackToast(getActivity().getString(rkkeep.keep.R.string.had_move_to_dustbin), rvContent);

                } else {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(rkkeep.keep.R.string.move_to_dustbin);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hasNotices = true;
                            mNoticeInfoDbHelper.updateNoticeInfoType(mData.get(position).infoId, NoticeInfo.NOMAL_TYPE_DUSTBIN);
                            mData.remove(position);
                            notifyItemRemoved(position);
                            showDataOrEmpty();
                            CommonUtil.showSnackToast(getActivity().getString(rkkeep.keep.R.string.had_move_to_dustbin), rvContent);
                        }
                    });
                    dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            protected int getHeaderItemCount() {
                return 0;
            }

            @Override
            protected int getFooterItemCount() {
                return mFooterType == 1 ? 1 : 0;
            }

            @Override
            protected int getContentItemCount() {
                return mData == null ? 0 : mData.size();
            }

            @Override
            protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
                return null;
            }

            @Override
            protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
                return new LoadmoreViewHolder(View.inflate(parent.getContext(), R.layout.layout_loadmore, null));
            }

            @Override
            protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
                return RecyclerViewFragment.this.onCreateViewHolder(parent, contentViewType);
            }

            @Override
            protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

            }

            @Override
            protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
                LoadmoreViewHolder _lHolder = (LoadmoreViewHolder) footerViewHolder;
                if (isLoadmoreEnable && !isLoading) {
                    loadData();
                }
                containerLoadmore = _lHolder.vsLoadmore;
                // 显示的为加载中的
                if (isLoading) {
                    _lHolder.vsLoadmore.setDisplayedChild(0);
                } else {
                    _lHolder.vsLoadmore.setDisplayedChild(1);
                }
                prepareHeaderFooter(footerViewHolder);
            }

            @Override
            protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder,
                                                       int position) {
                RecyclerViewFragment.this.onBindViewHolder(contentViewHolder, mData.get(position), position);
            }
        };
        //添加拖拽的接口
        mItemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(mAdapter, isLongPressDragEnabled(), isItemViewSwipeEnabled()));
        mItemTouchHelper.attachToRecyclerView(rvContent);
        rvContent.setAdapter(mAdapter);
    }

    private void prepareHeaderFooter(RecyclerView.ViewHolder vh) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setFullSpan(true);
        vh.itemView.setLayoutParams(layoutParams);
    }

    private void removeFromParent(View v) {
        if (v != null && v.getParent() != null) {
            ViewGroup _parent = (ViewGroup) v.getParent();
            _parent.removeView(v);
        }
    }


    /**
     * 实现数据的加载
     */
    protected void onLoadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    List<NoticeInfo> infos = mNoticeInfoDbHelper.getNoticeInfoList(getInfoType(), getInfoId(), getPageSize());
                    Log.i("size-->", infos.size() + "");
                    Log.i("totalSize-->", mNoticeInfoDbHelper.getMessageCount(getInfoType()) + "");
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = infos;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "加载失败";
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 0) {
                onSuccess((List<NoticeInfo>) msg.obj);
            } else if (msg.what == 1) {
                onError((String) msg.obj);
            }
        }
    };

    /**
     * 加载的信息类型
     **/
    public abstract int getInfoType();

    /**
     * 加载的每页数量
     **/
    public abstract int getPageSize();


    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定数据到界面上
     *
     * @param viewHolder
     * @param position
     */
    protected abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, NoticeInfo info, int position);


    /**
     * 加载更多
     */
    private class LoadmoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ViewSwitcher vsLoadmore;
        public final TextView btnLoadmore;

        public LoadmoreViewHolder(View itemView) {
            super(itemView);

            vsLoadmore = itemView.findViewById(R.id.vs_loadmore);
            btnLoadmore = itemView.findViewById(R.id.btn_loadmore);

            btnLoadmore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == btnLoadmore) {
                // 加载更多
                loadData();
            }
        }
    }

    /**
     * 加载数据
     */
    protected void loadData() {
        isLoading = true;
        onLoadData();
    }


    /**
     * 添加信息的
     **/
    public void addNoticeInfo(NoticeInfo info) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (info.infoType != getInfoType()) {
            return;
        }
        mFooterType = 0;
        mData.add(0, info);
        mAdapter.notifyFooterItemInserted(0);
        rvContent.getLayoutManager().scrollToPosition(0);
        showDataOrEmpty();
    }

    /**
     * 更新信息
     **/
    public void updateNoticeInfo(NoticeInfo info) {
        for (int i = 0; i < mData.size(); i++) {
            if (info.infoId == mData.get(i).infoId) {
                mData.remove(i);
                mData.add(i, info);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        isRefesh = true;
        onLoadData();
    }

    protected int getEmptyResourse() {
        return R.drawable.ic_launcher;
    }

    protected String getEmptyString() {
        return getActivity().getString(rkkeep.keep.R.string.enyty_data);
    }

    protected void showDataOrEmpty() {
        if ((mData == null || mData.size() == 0) && mFooterType != 1) {
            rvContent.setVisibility(View.GONE);
            rvScrollow.setVisibility(View.VISIBLE);
        } else {
            rvContent.setVisibility(View.VISIBLE);
            rvScrollow.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    public abstract void setNullDragHolder();
}
