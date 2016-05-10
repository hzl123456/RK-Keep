package rkkeep.keep.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.xmrk.rkandroid.activity.BackableBaseActivity;
import rkkeep.keep.R;

/**
 * Created by Au61 on 2016/5/9.
 */
public class HelpActivity extends BackableBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initTitle();
    }


    private void initTitle() {
        getTitlebar().setTitleTextAppearance(this, R.style.TitleTextStyle);
        getTitlebar().setBackgroundResource(R.color.title_red);
        getSupportActionBar().setTitle(R.string.nav_help);
    }


}
