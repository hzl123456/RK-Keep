package cn.xmrk.rkandroid.utils.uil;


import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.xmrk.rkandroid.application.RKApplication;


/**
 * 创建日期： 2015/11/12.
 * RecyclerView版PauseOnScrollListener，直接照抄UIL PauseOnScrollListener的代码
 */
public class RecyclerViewPauseOnScrollListener extends RecyclerView.OnScrollListener {

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final RecyclerView.OnScrollListener externalListener;

    public RecyclerViewPauseOnScrollListener( boolean pauseOnScroll, boolean pauseOnFling) {
        this(pauseOnScroll, pauseOnFling, null);
    }

    public RecyclerViewPauseOnScrollListener( boolean pauseOnScroll, boolean pauseOnFling, RecyclerView.OnScrollListener customListener) {
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        switch(scrollState) {
            case 0:
                Glide.with(RKApplication.getInstance()).resumeRequests();
                break;
            case 1:
                if(this.pauseOnScroll) {
                    Glide.with(RKApplication.getInstance()).pauseRequests();
                }
                break;
            case 2:
                if(this.pauseOnFling) {
                    Glide.with(RKApplication.getInstance()).pauseRequests();
                }
        }

        if(this.externalListener != null) {
            this.externalListener.onScrollStateChanged(view, scrollState);
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (this.externalListener != null) {
            this.externalListener.onScrolled(recyclerView, dx, dy);
        }
    }

}
