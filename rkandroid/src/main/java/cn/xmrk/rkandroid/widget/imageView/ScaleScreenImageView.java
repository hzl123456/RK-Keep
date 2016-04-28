package cn.xmrk.rkandroid.widget.imageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.xmrk.rkandroid.utils.CommonUtil;

/**
 * 宽度与屏幕相等，高度根据图片拉伸后做适配
 *
 * @author 思落羽 2014年9月26日 下午5:20:32
 */
public class ScaleScreenImageView extends ImageView {

    public ScaleScreenImageView(Context context) {
        super(context);
    }

    public ScaleScreenImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleScreenImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void setSize(int imgW, int imgH) {
        int width = getWidth() > 0 ? getWidth() : CommonUtil.getScreenDisplay().getWidth();
        if (width > 0) {
            float es = (float) width / (float) imgW;
            int height = (int) (imgH * es);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = height;
            System.out.println("width-->" + width + "height-->" + height);
            setLayoutParams(params);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setSize(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setSize(bm.getWidth(), bm.getHeight());
    }

}
