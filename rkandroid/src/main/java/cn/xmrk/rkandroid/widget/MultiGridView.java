package cn.xmrk.rkandroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可以在ScrollView中使用的GridView
 * @author 思落羽
 * 2014年9月15日 下午8:22:22
 *
 */
public class MultiGridView extends GridView {
    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public MultiGridView(Context context) {
        super(context);
    }
 
    public MultiGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
 
}