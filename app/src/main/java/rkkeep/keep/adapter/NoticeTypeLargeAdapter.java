package rkkeep.keep.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.help.ColorHelper;

/**
 * Created by Au61 on 2016/4/27.
 */
public class NoticeTypeLargeAdapter extends RecyclerView.Adapter<NoticeTypeLargeAdapter.ViewHolder> {

    private String checkColor;

    private List<String> colors;

    public NoticeTypeLargeAdapter(String checkColor) {
        colors = ColorHelper.getColors();
        this.checkColor = checkColor;
    }

    public void setCheckColor(String checkColor) {
        this.checkColor = checkColor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_color_large, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bitmap bitmap = Bitmap.createBitmap(CommonUtil.dip2px(50), CommonUtil.dip2px(50), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor(colors.get(position)));
        holder.ivColor.setImageBitmap(bitmap);

        if (colors.get(position).equals(checkColor)) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }
        holder.layoutColor.setBackgroundResource(ColorHelper.getCheckColor(checkColor));
        holder.layoutColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnColorChooseListener != null) {
                    mOnColorChooseListener.OnChoose(colors.get(position));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RoundImageView ivColor;
        private ImageView ivCheck;
        private RelativeLayout layoutColor;

        public ViewHolder(View itemView) {
            super(itemView);
            ivColor = (RoundImageView) itemView.findViewById(R.id.iv_color);
            layoutColor = (RelativeLayout) itemView.findViewById(R.id.layout_color);
            ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);
        }
    }

    private OnColorChooseListener mOnColorChooseListener;

    public void setOnColorChooseListener(OnColorChooseListener mOnColorChooseListener) {
        this.mOnColorChooseListener = mOnColorChooseListener;
    }

    public interface OnColorChooseListener {
        void OnChoose(String color);
    }
}
