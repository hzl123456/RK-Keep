package cn.xmrk.rkandroid.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.log4j.Logger;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.config.RKConfigHelper;
import cn.xmrk.rkandroid.config.StatisticsConfig;
import cn.xmrk.rkandroid.net.OkHttpStack;
import cn.xmrk.rkandroid.utils.CommonUtil;

/**
 * @author 思落羽
 *         2014年11月4日 下午3:59:50
 */
public abstract class BaseFragment extends Fragment {
    private final Logger log = Logger.getLogger(getClass());

    private View contentView;

    private RequestQueue mRequestQueue;
//	private boolean isViewCreate = false;

    protected boolean isShow = false;

    /**
     * 为true时，需要调用isShow，此参数用于防止 onCreateView 跟 dispatch 重复调用了 onShow() 方法
     */
    protected transient boolean isShowDispatch = false;

    public View getContentView() {
        return contentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 内存泄露检测
        RKApplication.getInstance().getRefWatcher().watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dispatchHide();
        contentView = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dispatchShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            try {
                contentView = inflater.inflate(getContentViewId(), null);
                if (RKConfigHelper.getInstance().isDebug()) {
                    log.debug("[onCreateView] isShow == " + isShow + ", this == " + this);
                }
                initOnCreateView(true);
//	    		isViewCreate = true;
            } catch (InflateException e) {
                /* map is already there, just return view as it is */
                log.error("[onCreateView]", e);
            }
        } else {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null)
                parent.removeView(contentView);
            initOnCreateView(false);
        }
        if (isShowDispatch) {
            onShow();
        }
        return contentView;
    }

    /**
     * onCreateView中调用
     *
     * @param isCreate 表示为 onCreateView是首次打开
     */
    protected void initOnCreateView(boolean isCreate) {
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            try {
                mRequestQueue = ((BaseActivity) getActivity()).getRequestQueue();
            } catch (Exception e) {
                mRequestQueue = Volley.newRequestQueue(getActivity(),
                        new OkHttpStack(RKApplication.getInstance().getOkHttpClient()));
            }
        }
        return mRequestQueue;
    }

    public void startActivity(Class<? extends Activity> actCls) {
        Context context = getActivity();
        if (context == null) {
            context = RKApplication.getInstance();
        }
        startActivity(new Intent(context, actCls));
    }

    public void startActivityForResult(Class<? extends Activity> actCls, int requestCode) {
        try {
            Context context = getActivity();
            if (context == null) {
                context = RKApplication.getInstance();
            }
            startActivityForResult(new Intent(context, actCls), requestCode);
        } catch (Exception e) {
            log.error("[startActivityForResult]", e);
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * 隐藏输入法
     */
    protected void hideKeyboard() {
        CommonUtil.hideKeyboard(getActivity());
    }

    /**
     * 显示输入法
     *
     * @param v 接受输入文字的地方
     */
    protected void showKeyboard(View v) {
        CommonUtil.showKeyboard(getActivity(), v);
    }

    /**
     * @return 输入法为显示状态是返回 {@code true}
     */
    protected boolean isKeyboardShow() {
        return CommonUtil.isKeyboardShow(getActivity());
    }

//	protected boolean showProgress(int msg) {
//		if (getActivity() instanceof BaseActivity) {
//			((BaseActivity) getActivity()).getPDM().setMessageAndShow(msg);
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	protected boolean showProgress(String msg) {
//		if (getActivity() instanceof BaseActivity) {
//			((BaseActivity) getActivity()).getPDM().setMessageAndShow(msg);
//			return true;
//		} else {
//			return false;
//		}
//	}

//	protected boolean hideProgress() {
//		if (getActivity() instanceof BaseActivity) {
//			((BaseActivity) getActivity()).getPDM().dismiss();
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public DialogUtil getPDM() {
//		return ((BaseActivity)getActivity()).getPDM();
//	}

    public boolean isShow() {
        return isShow;
    }

    public void dispatchShow() {
        if (RKConfigHelper.getInstance().isDebug()) {
            log.debug("[dispatchShow] isViewCreate == " + (contentView != null) + ", this == " + this);
        }
        if (!isShow) {
            if (contentView == null) {
                isShowDispatch = true;
            } else {
                onShow();
            }
        }
    }

    public void dispatchHide() {
        if (contentView != null && isShow) {
            onHide();
        }
    }

    public void onShow() {
        isShowDispatch = false;
        isShow = true;
        StatisticsConfig _sc = RKApplication.getInstance().getStatisticsConfig();
        if (_sc != null) {
            _sc.onFragmentShow(getStatisTag());
        }
    }

    public void onHide() {
        isShow = false;
        StatisticsConfig _sc = RKApplication.getInstance().getStatisticsConfig();
        if (_sc != null) {
            _sc.onFragmentHide(getStatisTag());
        }
    }

    /**
     * 统计标签
     *
     * @return
     */
    protected String getStatisTag() {
        try {
            return getBaseActivity().getTitlebar().toString();
        } catch (Exception e) {
            return getClass().getName();
        }
    }

    protected void setTitle(int resId) {
        Activity _activity = getActivity();
        if (_activity != null && _activity instanceof BaseActivity) {
            ActionBar _actionBar = _activity.getActionBar();
            View _customView = _actionBar.getCustomView();
            TextView _tvTitle = (TextView) _customView.findViewById(R.id.tv_title);
            _tvTitle.setText(resId);
        }
    }

    protected void setTitle(CharSequence title) {
        Activity _activity = getActivity();
        if (_activity != null && _activity instanceof BaseActivity) {
            ActionBar _actionBar = _activity.getActionBar();
            View _customView = _actionBar.getCustomView();
            TextView _tvTitle = (TextView) _customView.findViewById(R.id.tv_title);
            _tvTitle.setText(title);
        }
    }

    /**
     * 设置Actionbar的显示
     *
     * @param show
     */
    protected void setActionbarShow(boolean show) {
        Activity _activity = getActivity();
        if (_activity != null) {
            ActionBar _ab = _activity.getActionBar();
            if (_ab != null && _ab.isShowing() && !show) {
                _ab.hide();
            } else if (_ab != null && !_ab.isShowing() && show) {
                _ab.show();
            }
        }
    }

    public View findViewById(int id) {
        if (contentView == null) {
            return null;
        } else {
            return contentView.findViewById(id);
        }
    }

    public BaseActivity getBaseActivity() {
        Activity _activity = getActivity();
        if (_activity != null && _activity instanceof BaseActivity) {
            return (BaseActivity) _activity;
        } else {
            return null;
        }
    }

    /**
     * 获取到ContentView的id
     *
     * @return
     */
    protected abstract int getContentViewId();

}
