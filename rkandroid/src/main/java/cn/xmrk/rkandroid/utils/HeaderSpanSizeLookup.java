package cn.xmrk.rkandroid.utils;

import android.support.v7.widget.GridLayoutManager;

import cn.xmrk.rkandroid.adapter.HeaderFooterRecyclerViewAdapter;

/**
 * Created by cundong on 2015/10/23.
 *
 * RecyclerView为GridLayoutManager时，设置了HeaderView，就会用到这个SpanSizeLookup
 */
public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private HeaderFooterRecyclerViewAdapter adapter;

    private int mSpanSize = 1;

    public HeaderSpanSizeLookup(HeaderFooterRecyclerViewAdapter adapter, int spanSize) {
        this.adapter = adapter;
        this.mSpanSize = spanSize;
    }
    @Override
    public int getSpanSize(int position) {
        boolean isHeaderOrFooter = adapter.isHeader(position) || adapter.isFooter(position);
        return isHeaderOrFooter ? mSpanSize : 1;
    }
}