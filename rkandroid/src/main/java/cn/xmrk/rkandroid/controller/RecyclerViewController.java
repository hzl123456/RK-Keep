package cn.xmrk.rkandroid.controller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.adapter.HeaderFooterRecyclerViewAdapter;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.net.CommonListener;
import cn.xmrk.rkandroid.net.ErrorToastListener;
import cn.xmrk.rkandroid.pojo.ResultInfo;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;

/**
 * 创建日期： 2015/11/11.
 * 抽出RecyclerViewFragment和RecyclerViewActivtiy
 */
public class RecyclerViewController<D> {

    /**
     * 数据存放的位置
     */
    protected RecyclerView rvContent;

    /**
     * 头View
     */
    private View mHeaderView;

    /**
     * 加载更多容器
     */
    private ViewGroup containerLoadmore;

    /**
     * 为 {@code true} 表示为可以加载更多
     */
    private volatile boolean isLoadmoreEnable;

    /**
     * 为 {@code ture} 表示当前正在加载更多中
     */
    private volatile boolean isLoading;

    /**
     * 当前页码，从0开始
     */
    private int currentPage;

    /**
     * 加载更多的FooterView的显示类型<br/>
     * 0为不需要显示<br/>
     * 1为加载更多多<br/>
     * 2为没有数据<br/>
     */
    private int mFooterType = 1;

    /**
     * 数据为空的时候显示的文字
     */
    private String emptyStr;

    private ArrayList<D> mData;

    private RecyclerViewAdapter mAdapter;

    private Request mCurrentRequest;

    private RecyclerViewController.Callback mCallback;

    private ErrorToastListener mErrorListener = new ErrorToastListener() {
        @Override
        public void setMsg(String msg) {
            if (!RecyclerViewController.this.onErrorMsgShow(msg)) {
                super.setMsg(msg);
            }
        }
    };

    public RecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    private CommonListener mListener = new CommonListener() {

        @Override
        protected boolean isSuccess(ResultInfo result) {
            return isRequestSuccess(result);
        }

        @Override
        public void onSuccess(ResultInfo resultInfo) {
            // 加载成功
            RecyclerViewController.this.onSuccess(resultInfo);
        }

        @Override
        public void onOtherFlag(ResultInfo resultInfo) {
            super.onOtherFlag(resultInfo);
            RecyclerViewController.this.onOtherFlag(resultInfo);
        }
    };

    public RecyclerViewController(@NonNull Callback callback, @NonNull RecyclerView recyclerView) {
        this.mCallback = callback;
        this.rvContent = recyclerView;

        if (mAdapter == null) {
            mAdapter = new RecyclerViewAdapter();
        }
        rvContent.setAdapter(mAdapter);
    }

    public void resetToView(@NonNull RecyclerView recyclerView) {
        this.rvContent = recyclerView;
        rvContent.setAdapter(mAdapter);
    }

    public void onSuccess(ResultInfo resultInfo) {
        // 加载成功
        isLoading = false;
        handlerDataResult(resultInfo);
        mAdapter.notifyDataSetChanged();
        mCallback.onLoadFinish(isRefresh());
    }

    public void onOtherFlag(ResultInfo resultInfo) {
        isLoading = false;
        // 加载失败
        setBtnLoadmoreShow();
    }

    /**
     * @param msg
     * @return 为true拦截下toast
     */
    public boolean onErrorMsgShow(String msg) {
        mCallback.onLoadFinish(isRefresh());
        isLoading = false;
        setBtnLoadmoreShow();
        return false;
    }

    protected boolean isRequestSuccess(ResultInfo result) {
        mCallback.onLoadFinish(isRefresh());
        return StringUtil.isEqualsString("success", result.flag);
    }

    /**
     * 让加载更多的按钮显示出来
     */
    private void setBtnLoadmoreShow() {
        if (containerLoadmore != null) {
            ViewSwitcher _vsLoadmore = (ViewSwitcher) containerLoadmore.findViewById(R.id.vs_loadmore);
            _vsLoadmore.setDisplayedChild(1);
        }
    }

    public void addAll(int pos, List<D> data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(pos, data);
    }

    /**
     * 处理加载完成以后的数据
     *
     * @param resultInfo
     */
    private void handlerDataResult(ResultInfo resultInfo) {
        // 确保不为空，并且是一个数组
        if (isRefresh() && mData != null) {
            mData.clear();
        }
        if (resultInfo.data != null) {
            List<D> _newData = parseList(resultInfo);
            if (_newData != null) {
                if (mData == null) {
                    mData = new ArrayList<D>();
                }
                if (_newData != null) {
                    mData.addAll(_newData);
                }
                if (hasMore(resultInfo)) {
                    // 还有更多
                    isLoadmoreEnable = true;
                    setFooterType(1);
                } else {
                    isLoadmoreEnable = false;
                    setFooterType(0);
                }
            }
        }
        // 判断数据是否为空
        if ((mData == null || mData.isEmpty()) && mFooterType != 0) {
            // 显示没有数据
            setFooterType(2);
        }
    }

    /**
     * 解析列表数据
     *
     * @param resultInfo
     * @return
     */
    protected List<D> parseList(ResultInfo resultInfo) {
        JsonElement _dataJson = resultInfo.getDataData();
        List<D> _newData = CommonUtil.getGson().fromJson(_dataJson, getListType());
        return _newData;
    }

    public boolean hasMore(ResultInfo resultInfo) {
        return mData.size() < resultInfo.getDataCount();
    }

    public D getItem(int position) {
        if (mData == null || position < 0 || position >= mData.size()) {
            return null;
        } else {
            return mData.get(position);
        }
    }

    public boolean removeItem(D info) {
        if (mData != null) {
            return mData.remove(info);
        } else {
            return false;
        }
    }

    public List<D> getData() {
        return mData;
    }

    public void notifyDataChange() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mCallback.getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    private LayoutInflater getLayoutInflater() {
        return mCallback.getActivity().getLayoutInflater();
    }

    public void refresh() {
        // 执行刷新操作
        currentPage = 0;
        loadData();
    }

    public void setEmptyString(int resId) {
        this.emptyStr = RKApplication.getInstance().getString(resId);
    }

    public void setEmptyString(String str) {
        this.emptyStr = str;
    }

    public String getEmptyString() {
        return this.emptyStr;
    }

    /**
     * 设置头部
     *
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 加载数据
     */
    protected Request loadData() {
        isLoading = true;
        return onLoadData(currentPage++, mListener, mErrorListener);
    }

    protected int getViewType(int position) {
        return mCallback.getViewType(position);
    }

    protected boolean isRefresh() {
        return currentPage < 2;
    }

    /**
     * 获取数据对象通过Gson解析成列表用的Type
     *
     * @return
     */
    protected Type getListType() {
        return mCallback.getListType();
    }

    /**
     * 头部被显示出来
     */
    protected void onHeaderShow() {
    }

    /**
     * 实现数据的加载
     *
     * @param page          加载的页码
     * @param listener      加载完成的监听
     * @param errorListener 加载失败的监听
     */
    protected Request onLoadData(int page, Response.Listener listener, Response.ErrorListener errorListener) {
        return mCallback.onLoadData(page, listener, errorListener);
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mCallback.onCreateViewHolder(parent, viewType);
    }

    /**
     * 绑定数据到界面上
     *
     * @param viewHolder
     * @param position
     */
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, D info, int position) {
        mCallback.onBindViewHolder(viewHolder, info, position);
    }

    /**
     * 修改底部的type，需要调用notifyDataChange生效
     * 加载更多的FooterView的显示类型<br/>
     * 0为不需要显示<br/>
     * 1为加载更多多<br/>
     * 2为没有数据
     *
     * @param footerType
     */
    public void setFooterType(int footerType) {
        mFooterType = footerType;
    }

    public void clearData() {
        if (mData != null)
            mData.clear();
    }

    private class RecyclerViewAdapter extends HeaderFooterRecyclerViewAdapter {

        @Override
        protected int getHeaderItemCount() {
            return mHeaderView == null ? 0 : 1;
        }

        @Override
        protected int getFooterItemCount() {
            if (mFooterType == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        protected int getContentItemCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
            return new HeaderViewHolder(mHeaderView);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
            if (footerViewType == 1) {
                // 加载更多
                if (containerLoadmore == null) {
                    containerLoadmore = (ViewGroup) RecyclerViewController.this.getLayoutInflater().inflate(R.layout.layout_loadmore, parent, false);
                }
                return new LoadmoreViewHolder(containerLoadmore);
            } else {
                // 数据为空
                return new EmptyViewHolder((RecyclerViewController.this.getLayoutInflater().inflate(R.layout.layout_empty, parent, false)));
            }
        }

        @Override
        protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
            return RecyclerViewController.this.onCreateViewHolder(parent, contentViewType);
        }

        @Override
        protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {
            // 绑定头部控件的数据
            RecyclerViewController.this.onHeaderShow();
        }

        @Override
        protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
            try {
                if (mFooterType == 2 && emptyStr != null) {
                    // 设置数据为空的显示
                    EmptyViewHolder _evHolder = (EmptyViewHolder) footerViewHolder;
                    _evHolder.tvEmpty.setText(emptyStr);
                } else {
                    LoadmoreViewHolder _lHolder = (LoadmoreViewHolder) footerViewHolder;
                    // 显示加载更多时，当前没有在进行网络加载，那就去加载新的数据
                    if (isLoadmoreEnable && !isLoading) {
                        loadData();
                    }
                    // 显示的为加载中的
                    if (isLoading) {
                        _lHolder.vsLoadmore.setDisplayedChild(0);
                    } else {
                        _lHolder.vsLoadmore.setDisplayedChild(1);
                    }
                }
            } catch (Exception e) {
                // 强制失败
            }
        }

        @Override
        protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
            RecyclerViewController.this.onBindViewHolder(contentViewHolder, getItem(position), position);
        }

        @Override
        protected int getContentItemViewType(int position) {
            return getViewType(position);
        }

        @Override
        protected int getFooterItemViewType(int position) {
            return mFooterType;
        }
    }

    private void removeFromParent(View v) {
        if (v != null && v.getParent() != null) {
            ViewGroup _parent = (ViewGroup) v.getParent();
            _parent.removeView(v);
        }
    }

    /**
     * 加载更多
     */
    private class LoadmoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ViewSwitcher vsLoadmore;
        public final Button btnLoadmore;

        public LoadmoreViewHolder(View itemView) {
            super(itemView);

            vsLoadmore = (ViewSwitcher) itemView.findViewById(R.id.vs_loadmore);
            btnLoadmore = (Button) itemView.findViewById(R.id.btn_loadmore);

            btnLoadmore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == btnLoadmore) {
                // 加载更多
                mCurrentRequest = loadData();
            }
        }

    }

    /**
     * 没有数据时的ViewHolder
     */
    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEmpty;

        public EmptyViewHolder(View itemView) {
            super(itemView);

            tvEmpty = (TextView) itemView.findViewById(R.id.tv_empty);
        }

    }

    /**
     * 头部
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private View headerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
        }

    }

    public interface Callback<D> {
        Activity getActivity();

        Type getListType();

        int getViewType(int position);

        Request onLoadData(int page, Response.Listener listener, Response.ErrorListener errorListener);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type);

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, D info, int position);

        void onLoadFinish(boolean isRefresh);
    }

}
