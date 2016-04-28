package cn.xmrk.rkandroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * 取消长按响应，支持手指上滑可以调用回调 主要是发送语音的按钮用到
 * 
 * @author 思落羽 2014年8月13日 上午10:37:49
 *
 */
public class LeaveButton extends TextView {

	private Runnable isClickRunnable = new Runnable() {

		@Override
		public void run() {
			isLongPressed = true;
			if (mOnLeaveListener != null) {
				mOnLeaveListener.onLongPress(LeaveButton.this);
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
			}
		}

	};

	private boolean isLongPressed;

	/**
	 * 允许长按
	 */
	private boolean isLongPressable = true;

	/**
	 * 当前手指位置在按钮之内
	 */
	private boolean inRegion;

	protected OnLeaveListener mOnLeaveListener;

	public LeaveButton(Context context) {
		super(context);
	}

	public LeaveButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LeaveButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnLeaveListener(OnLeaveListener listener) {
		this.mOnLeaveListener = listener;
	}

	public void setLongPressable(boolean longPressable) {
		this.isLongPressable = longPressable;
	}

	public boolean isLongPressable() {
		return isLongPressable;
	}

	/**
	 * 触摸点是否在控件内，x 和 y是相对于控件左上角的位置
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInView(float x, float y) {
		return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isEnabled()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_OUTSIDE:
				if (isLongPressable && isLongPressed && mOnLeaveListener != null) {
					mOnLeaveListener.onCancel(LeaveButton.this);
				}
				// 刷新背景图片状态
				getBackground().setState(new int[] { android.R.attr.state_empty });
				getBackground().invalidateSelf();
				break;
			case MotionEvent.ACTION_UP:
				// 刷新背景图片状态
				getBackground().setState(new int[] { android.R.attr.state_empty });
				getBackground().invalidateSelf();
				// 取消标识长按，并且检测事件
				removeCallbacks(isClickRunnable);
				if (isLongPressable && isLongPressed && mOnLeaveListener != null) {
					if (inRegion) {
						mOnLeaveListener.onRelease(LeaveButton.this);
					} else {
						mOnLeaveListener.onCancel(LeaveButton.this);
					}
				} else {
					performClick();
				}
				break;
			case MotionEvent.ACTION_DOWN:
				// 刷新背景图片状态
				getBackground().setState(new int[] { android.R.attr.state_pressed });
				getBackground().invalidateSelf();
				inRegion = true;
				isLongPressed = false;
				if (isLongPressable && mOnLeaveListener != null) {
					// 按下到ViewConfiguration.getLongPressTimeout()的时间，就算是长按
					postDelayed(isClickRunnable, ViewConfiguration.getLongPressTimeout());
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mOnLeaveListener != null) {
					if (isInView(event.getX(), event.getY()) != inRegion) {
						inRegion = !inRegion;
						mOnLeaveListener.onStateChange(LeaveButton.this, inRegion);
					}
				}
			default:
				super.onTouchEvent(event);
				break;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 按钮离开或长按监听
	 * 
	 * @author 思落羽 2014年8月13日 下午2:18:08
	 *
	 */
	public interface OnLeaveListener {

		/**
		 * 进入长按状态
		 * 
		 * @param lb
		 */
		void onLongPress(LeaveButton lb);

		/**
		 * 在按钮内抬起手指为松开
		 * 
		 * @param lb
		 */
		void onRelease(LeaveButton lb);

		/**
		 * 移动的时候，手指位置改变的时候，当移动到按钮之外或从按钮之外再移回来时触发
		 * 
		 * @param lb
		 * @param inRegion
		 *            {@code true} 当前为进入到按钮之内
		 */
		void onStateChange(LeaveButton lb, boolean inRegion);

		/**
		 * 往外滑视为取消
		 * 
		 * @param lb
		 */
		void onCancel(LeaveButton lb);
	}

}
