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

public class HalfCornerImageView extends RoundImageView {

	public int mCornerSize;

	public HalfCornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public HalfCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HalfCornerImageView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs == null) {
			mCornerSize = CommonUtil.dip2px(5);
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
		canvas.drawRect(new RectF(0.0f, height-mCornerSize, width, height), paint);
		return bitmap;
	}

}
