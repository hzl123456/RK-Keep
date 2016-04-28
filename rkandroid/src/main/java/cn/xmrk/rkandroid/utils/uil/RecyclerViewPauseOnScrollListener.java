package cn.xmrk.rkandroid.utils.uil;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 创建日期： 2015/11/12.
 * RecyclerView版PauseOnScrollListener，直接照抄UIL PauseOnScrollListener的代码
 */
public class RecyclerViewPauseOnScrollListener extends RecyclerView.OnScrollListener {

    private ImageLoader imageLoader;
    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final RecyclerView.OnScrollListener externalListener;

    public RecyclerViewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        this(imageLoader, pauseOnScroll, pauseOnFling, null);
    }

    public RecyclerViewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, RecyclerView.OnScrollListener customListener) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        switch(scrollState) {
            case 0:
                this.imageLoader.resume();
                break;
            case 1:
                if(this.pauseOnScroll) {
                    this.imageLoader.pause();
                }
                break;
            case 2:
                if(this.pauseOnFling) {
                    this.imageLoader.pause();
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
