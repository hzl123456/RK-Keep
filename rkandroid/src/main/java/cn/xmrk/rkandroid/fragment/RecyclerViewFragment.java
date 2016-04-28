package cn.xmrk.rkandroid.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import java.lang.reflect.Type;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.adapter.HeaderFooterRecyclerViewAdapter;
import cn.xmrk.rkandroid.controller.RecyclerViewController;
import cn.xmrk.rkandroid.utils.UnitUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * RecyclerView 的Activity
 */
public abstract class RecyclerViewFragment<D> extends BaseFragment {


    private RecyclerViewController<D> mController;

    /**
     * 保存头部
     **/
    private View headerView;

    /**
     * 数据存放的位置
     */
    protected RecyclerView rvContent;

    /**
     * 刷新控件
     */
    private PtrFrameLayout srlContent;

    /**
     * 加载更多容器
     */
    private ViewGroup containerLoadmore;

    /**
     * 是否需要下拉刷新
     **/
    private boolean isNeedPullToReflush = true;


    public void setIsNeedPullToReflush(boolean isNeedPullToReflush) {
        this.isNeedPullToReflush = isNeedPullToReflush;
    }

    private RecyclerViewController.Callback<D> mCallback = new RecyclerViewController.Callback<D>() {

        @Override
        public Activity getActivity() {
            return RecyclerViewFragment.this.getActivity();
        }

        @Override
        public Type getListType() {
            return RecyclerViewFragment.this.getListType();
        }

        @Override
        public int getViewType(int position) {
            return RecyclerViewFragment.this.getViewType(position);
        }

        @Override
        public Request onLoadData(int page, Response.Listener listener, Response.ErrorListener errorListener) {
            return RecyclerViewFragment.this.onLoadData(page, listener, errorListener);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return RecyclerViewFragment.this.onCreateViewHolder(parent, type);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, D info, int position) {
            RecyclerViewFragment.this.onBindViewHolder(viewHolder, info, position);
        }

        @Override
        public void onLoadFinish(boolean isRefresh) {
            if (isRefresh) {
                setSrlRefreshing(false);
            }
            if (mOnLoadDataFinishListener != null) {
                mOnLoadDataFinishListener.onFinish();
            }
        }
    };

    public HeaderFooterRecyclerViewAdapter getAdapter() {
        return mController.getAdapter();
    }

    private void setSrlRefreshing(boolean isRefreshing) {
        if (srlContent != null)
            if (isRefreshing)
                srlContent.autoRefresh();
            else
                srlContent.refreshComplete();
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
            if (mController == null) {
                mController = getRecyclerViewController(mCallback, rvContent);
            } else {
                mController.resetToView(rvContent);
            }
            if (headerView != null) {
                mController.setHeaderView(headerView);
            }
            initViews();
        }
        removeFromParent(containerLoadmore);
    }

    /**
     * 获取一个RecyclerViewController，因为一些逻辑是这个在做的，为了方便灵活配置，所以通过方法生成，子类可以覆盖掉
     *
     * @param callback
     * @param rvContent
     * @return
     */
    protected RecyclerViewController<D> getRecyclerViewController(RecyclerViewController.Callback callback, RecyclerView rvContent) {
        return new RecyclerViewController<>(callback, rvContent);
    }

    @Override
    public void onShow() {
        super.onShow();
        if (mController.getData() == null || mController.getData().isEmpty()) {
            mController.refresh();
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        if (srlContent != null && srlContent.isRefreshing()) {
            setSrlRefreshing(false);
            getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    private void findViews() {
        srlContent = (PtrFrameLayout) findViewById(R.id.srl_content);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);

    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false);
    }

    protected void initViews() {
        if (srlContent != null) {
            initPtr();
        }
        // 水平模式
        rvContent.setLayoutManager(getLayoutManager());
    }

    /**
     * 初始化Ultra-Pull-To-Refresh
     */
    protected void initPtr() {
        Context _context = getActivity();
        MaterialHeader _header = new MaterialHeader(_context);
        _header.setPtrFrameLayout(srlContent);
        _header.setColorSchemeColors(new int[]{_context.getResources().getColor(R.color.bg_title_bar)});
        int _padding = UnitUtil.dip2px(15, _context);
        _header.setPadding(0, _padding, 0, _padding);

        srlContent.addPtrUIHandler(_header);
        srlContent.setHeaderView(_header);
        srlContent.setPinContent(true);
        srlContent.setKeepHeaderWhenRefresh(true);
        srlContent.setLoadingMinTime(2000);
        srlContent.setPullToRefresh(false);
        srlContent.setRatioOfHeaderHeightToRefresh(1.7f);
        srlContent.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, content, header) && isNeedPullToReflush;
            }
        });
    }

    public void onRefresh() {
        mController.refresh();
    }

    public void setOnLoadDataFinishListener(OnLoadDataFinishListener mOnLoadDataFinishListener) {
        this.mOnLoadDataFinishListener = mOnLoadDataFinishListener;
    }

    private OnLoadDataFinishListener mOnLoadDataFinishListener;

    public interface OnLoadDataFinishListener {
        void onFinish();
    }

    private void removeFromParent(View v) {
        if (v != null && v.getParent() != null) {
            ViewGroup _parent = (ViewGroup) v.getParent();
            _parent.removeView(v);
        }
    }

    public void setHeaderView(View v) {
        this.headerView = v;
    }

    public void setEmptyString(String str) {
        mController.setEmptyString(str);
    }

    public void setEmptyString(int res) {
        mController.setEmptyString(res);
    }

    protected int getViewType(int position) {
        return 0;
    }

    public void refresh() {
        mController.setFooterType(0);
        srlContent.autoRefresh();
    }

    public RecyclerViewController getRecyclerViewController() {
        return mController;
    }

    /**
     * 获取数据对象通过Gson解析成列表用的Type
     *
     * @return
     */
    protected abstract Type getListType();

    /**
     * 实现数据的加载
     *
     * @param page          加载的页码
     * @param listener      加载完成的监听
     * @param errorListener 加载失败的监听
     */
    protected abstract Request onLoadData(int page, Response.Listener listener, Response.ErrorListener errorListener);

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
    protected abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, D info, int position);


}
