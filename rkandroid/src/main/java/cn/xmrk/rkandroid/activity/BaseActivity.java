package cn.xmrk.rkandroid.activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;


import java.lang.reflect.Field;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.config.StatisticsConfig;
import cn.xmrk.rkandroid.utils.DialogUtil;

/**
 * @author 思落羽 2014年10月28日 下午5:03:58
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 标题栏采用toolbar进行折叠
     */
    private Toolbar titlebar;


    private DialogUtil pdm;


    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.app_bar_main, null);
        //加载头部，头部使用toolbar进行显示
        titlebar = (Toolbar) contentView.findViewById(R.id.toolbar);
        //加载内容主体容器，并且将实际内容放进去
        RelativeLayout contentLayout = (RelativeLayout) contentView.findViewById(R.id.layout_containert);
        contentLayout.addView(view);
        super.setContentView(contentView);
        setSupportActionBar(titlebar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsConfig _sc = RKApplication.getInstance().getStatisticsConfig();
        if (_sc != null) {
            _sc.onActivityResume(getStatisTag());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatisticsConfig _sc = RKApplication.getInstance().getStatisticsConfig();
        if (_sc != null) {
            _sc.onActivityPause(getStatisTag());
        }
    }

    /**
     * 统计标签
     *
     * @return
     */
    protected String getStatisTag() {
        try {
            return titlebar.getTitle().toString();
        } catch (Exception e) {
            return getClass().getName();
        }
    }

    public Toolbar getTitlebar() {
        return titlebar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdm != null) {
            pdm.dismiss();
            pdm.setContext(null);
        }
        // 修复InputMethodManager导致的内存泄漏
        fixInputMethodManager(this);
        // 内存泄露检测
        RKApplication.getInstance().getRefWatcher().watch(this);
    }

    /**
     * 设置全屏显示
     *
     * @return
     */
    public void setFullScreen(boolean isFullScreen) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (!isFullScreen) {
            flag = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, flag);
    }

    /**
     * 设置背景颜色
     *
     * @param resId
     */
    public void setBackground(int resId) {
        getWindow().setBackgroundDrawableResource(resId);
    }

    /**
     * 设置背景图片
     *
     * @param img
     */
    public void setBackground(Bitmap img) {
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), img));
    }

    /**
     * 设置背景图片
     *
     * @param drawable
     */
    public void setBackground(Drawable drawable) {
        getWindow().setBackgroundDrawable(drawable);
    }


    protected void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    protected void startActivityForResult(Class<? extends Activity> cls, int requestCode) {
        startActivityForResult(new Intent(this, cls), requestCode);
    }

    public boolean hasPDM() {
        return pdm != null;
    }

    public DialogUtil getPDM() {
        if (pdm == null) {
            pdm = DialogUtil.newInstance(this);
        }
        return pdm;
    }

    /**
     * @param ctb 自定义的Titlebar
     */
    public void setCustomTitlebar(View ctb) {
        if (ctb != null) {
            // 移除当前的子view
            titlebar.removeAllViews();
            titlebar.addView(ctb);
        }
        showCustomTitlebar(true);
    }

    public final void showAlertMessage(String message, String title, String positiveBtnText, DialogInterface.OnClickListener listener) {
        if (positiveBtnText == null) {
            positiveBtnText = getResources().getString(R.string.ok);
        }
        new AlertDialog.Builder(this).setMessage(message).setTitle(title).setPositiveButton(positiveBtnText, listener).show();
    }

    public final void showDialogMessage(String message, String title, String positiveBtnText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        if (positiveBtnText == null) {
            positiveBtnText = getResources().getString(R.string.ok);
        }
        if (negativeText == null) {
            negativeText = getResources().getString(R.string.cancel);
        }
        new AlertDialog.Builder(this).setCancelable(true).setMessage(message).setTitle(title).setPositiveButton(positiveBtnText, positiveListener).setNegativeButton(negativeText, negativeListener).show();
    }

    /**
     * @param show 显示自定义的titlebar，如果自定义的titlebar存在
     */
    public void showCustomTitlebar(boolean show) {
        if (show) {
            getSupportActionBar().show();
        } else {
            getSupportActionBar().hide();
        }
    }

    /**
     * 修复 InputMethodManager 导致的内存溢出
     */
    private void fixInputMethodManager(Context context) {
        final Object imm = context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == context) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }
}