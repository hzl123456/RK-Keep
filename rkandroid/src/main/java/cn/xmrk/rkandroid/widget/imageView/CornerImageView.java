package cn.xmrk.rkandroid.widget.imageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.utils.CommonUtil;

/**
 * 菜品用的ImageView 2015年3月13日 下午4:39:50
 */
public class CornerImageView extends RoundImageView {

    public int mCornerSize;

    public CornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CornerImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        //默认为5个像素点
        if (attrs == null) {
            mCornerSize = CommonUtil.dip2px(10);
        } else {
            TypedArray _ta = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView);
            mCornerSize = _ta.getDimensionPixelSize(R.styleable.CornerImageView_corner_size, mCornerSize);
            _ta.recycle();
        }
    }

    @Override
    public Bitmap getBitmap() {
        int width = getWidth();
        int height = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(new RectF(0.0f, 0.0f, width, height), mCornerSize, mCornerSize, paint);
        return bitmap;
    }

}
