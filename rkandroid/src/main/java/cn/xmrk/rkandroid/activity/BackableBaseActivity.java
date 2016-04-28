package cn.xmrk.rkandroid.activity;

import android.view.View;

import cn.xmrk.rkandroid.R;


/**
 * 左上角按钮为“返回”的Activity
 * 2015年6月17日 下午3:31:07
 */
public class BackableBaseActivity extends BaseActivity {

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getTitlebar().setNavigationIcon(R.drawable.ic_white_back);
        getTitlebar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
