package cn.xmrk.rkandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 自带整个控件点击的ViewHolder
 */
public class ClickableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public OnViewHolderClickListener mListener;

    private View rootView;

    public ClickableViewHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        itemView.setClickable( true );
        itemView.setOnClickListener( this );
    }

    public ClickableViewHolder(View itemView, OnViewHolderClickListener listener) {
        this(itemView);
        this.mListener = listener;
    }

    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null && v == rootView) {
            mListener.OnViewHolderClick( this );
        }
    }
}
