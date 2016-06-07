package rkkeep.keep.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import cn.xmrk.rkandroid.activity.BackableBaseActivity;
import rkkeep.keep.R;

/**
 * Created by Au61 on 2016/5/9.
 */
public class HelpActivity extends BackableBaseActivity {

    private TextView tvUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initTitle();
        findViews();
    }


    private void initTitle() {
        getTitlebar().setTitleTextAppearance(this, R.style.TitleTextStyle);
        getTitlebar().setBackgroundResource(R.color.title_red);
        getSupportActionBar().setTitle(R.string.nav_help);
    }

    private void findViews() {
        tvUrl = (TextView) findViewById(R.id.tv_url);
        tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(tvUrl.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


}
