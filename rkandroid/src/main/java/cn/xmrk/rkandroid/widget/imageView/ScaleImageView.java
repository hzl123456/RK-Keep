package cn.xmrk.rkandroid.widget.imageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.reflect.Field;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.utils.CommonUtil;

/**
 * 宽度与父控件相等，高度根据图片拉伸后做适配
 * 设置ratio_width，ratio_height可以设置它的宽高比，强制比例
 *
 * @author 思落羽
 *         2014年9月26日 下午5:20:32
 */
public class ScaleImageView extends ImageView {

    /**
     * 宽度比率
     */
    private int ratioWidth;

    /**
     * 高度比率
     */
    private int ratioHeight;

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        ratioWidth = ta.getInt(R.styleable.CustomImageView_ratio_width, 1);
        ratioHeight = ta.getInt(R.styleable.CustomImageView_ratio_height, 1);
        ta.recycle();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int width = getWidth() > 0 ? getWidth() : MeasureSpec.getSize(widthMeasureSpec);//PhoneUtil.getScreenDisplay().x;
            // 与最大的 宽进行比较
            try {
                int _maxWidth = Integer.MAX_VALUE;
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    _maxWidth = getMaxWidth();
                } else {
                    Field _field = ImageView.class.getDeclaredField("mMaxWidth");
                    _maxWidth = _field.getInt(this);
                }
                width = width > _maxWidth ? _maxWidth : width;
            } catch (Exception e) {
            }
            if (width == 0) {
                width = CommonUtil.getScreenDisplay().getWidth();
            }
            if (width > 0) {
                float es = (float) width / (float) drawable.getIntrinsicWidth();
                int height;
                if (ratioWidth > 0 && ratioHeight > 0) {
                    // 比率不为0且不相等的时候，高度不取图片高度，而是按比例根据宽度计算高度
                    height = (int) ((float) width / ratioWidth * ratioHeight);
                } else {
                    height = (int) (drawable.getIntrinsicHeight() * es);
                }
                setMeasuredDimension(width, height);
            }
        }
    }


}
