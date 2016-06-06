package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.activity.BaseActivity;

/**
 * 菊花管理器，使用需要在Activity/Fragment生命周期对应的对应调用生命周期方法
 *
 * @author 思落羽
 *         2014年9月11日 下午3:34:35
 */
public class DialogUtil implements OnCancelListener, SweetAlertDialog.OnSweetClickListener {

    private Context mContext;

    private SweetAlertDialog mProgressDialog;

    private OnCancelListener mOnCancelListener;

    public static DialogUtil newInstance(Context context) {
        return new DialogUtil(context);
    }

    public DialogUtil(Context context) {
        setContext(context);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    /**
     * 提示错误信息
     *
     * @param msg
     */
    public void showError(String msg) {
        showDialog(msg);
    }

    /**
     * 提示错误信息
     *
     * @param msgId
     */
    public void showError(int msgId) {
        showError(mContext.getString(msgId));
    }

    /**
     * 显示消息
     *
     * @param msg
     */
    public void showAlertDialog(String msg, SweetAlertDialog.OnSweetClickListener confirmListener,
                                SweetAlertDialog.OnSweetClickListener cancelListener) {
        SweetAlertDialog _sad;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            // 如果存在 mProgressDialog，则重用它
            _sad = mProgressDialog;
            _sad.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
            _sad.findViewById(R.id.title_text).setVisibility(View.VISIBLE);
        } else {
            _sad = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            mProgressDialog = _sad;
        }
        final SweetAlertDialog.OnSweetClickListener _cancelListener = cancelListener;
        SweetAlertDialog.OnSweetClickListener _cl = new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                onCancel(sweetAlertDialog);
                if (_cancelListener != null) {
                    _cancelListener.onClick(sweetAlertDialog);
                }
            }
        };
        // 设置取消按钮监听
        _sad.setOnCancelListener(this);
        _sad.setConfirmClickListener(confirmListener);
        _sad.setCancelClickListener(_cl);
        _sad.setCancelable(true);
        _sad.setTitleText(msg);
        _sad.setCancelText(mContext.getString(R.string.cancel));
        _sad.showCancelButton(true);
        if (!_sad.isShowing()) {
            _sad.show();
        }
    }

    /**
     * 显示消息
     *
     * @param msg
     */
    public void showDialog(String msg) {
        SweetAlertDialog _sad;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            // 如果存在 mProgressDialog，则重用它
            _sad = mProgressDialog;
            _sad.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
            _sad.findViewById(R.id.title_text).setVisibility(View.VISIBLE);
        } else {
            _sad = new SweetAlertDialog(mContext);
            mProgressDialog = _sad;
        }
        _sad.setConfirmClickListener(this);
        _sad.setOnCancelListener(this);
        _sad.setCancelable(true);
        _sad.showCancelButton(false);
        _sad.setTitleText(msg);
        if (!_sad.isShowing()) {
            _sad.show();
        }
    }

    /**
     * 显示消息
     *
     * @param msgId
     */
    public void showDialog(int msgId) {
        showDialog(mContext.getString(msgId));
    }

    /**
     * 显示等待窗
     */
    public void showProgress(String msg, OnCancelListener cancelListener) {
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        } else {
            mProgressDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        }
        if (msg != null) {
            mProgressDialog.setTitleText(msg);
        }
        // 设置取消
        mOnCancelListener = cancelListener;
        mProgressDialog.setOnCancelListener(this);
        mProgressDialog.showCancelButton(false);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        if (cancelListener != null) {
            mProgressDialog.setCancelable(false);
        }

        if (msg != null) {
            mProgressDialog.findViewById(R.id.title_text).setVisibility(View.VISIBLE);
        } else {
            mProgressDialog.findViewById(R.id.title_text).setVisibility(View.GONE);
        }
        mProgressDialog.setCancelable(true);
    }

    /**
     * 显示等待窗
     */
    public void showProgress(int msg, BaseActivity activity) {
        showProgress(mContext.getString(msg), null);
    }

    /**
     * 显示等待窗
     */
    public void showProgress(String msg) {
        showProgress(msg, null);
    }

    /**
     * 显示等待窗
     */
    public void showProgress() {
        showProgress(null, null);
    }


    public void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            mOnCancelListener = null;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
        mOnCancelListener = null;
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        dismiss();
    }
}
