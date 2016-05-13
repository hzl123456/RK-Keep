package rkkeep.keep.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import rkkeep.keep.R;
import rkkeep.keep.widget.HandWritingView;

/**
 * Created by Au61 on 2016/5/13.
 */
public class HandWritingActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibBack;
    private ImageButton ibFront;
    private ImageButton ibShare;

    private HandWritingView mHandWritingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);
        initTitle();
        findViews();
        initView();
    }

    private void findViews() {
        mHandWritingView = (HandWritingView) findViewById(R.id.view);
    }

    private void initView() {
        mHandWritingView.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        mHandWritingView.setonDrawingListener(new HandWritingView.onDrawingListener() {
            @Override
            public void canBack(boolean canBack) {
                ibBack.setEnabled(canBack);
            }

            @Override
            public void canFront(boolean canFront) {
                ibFront.setEnabled(canFront);
            }
        });
    }

    private void initTitle() {
        View titleView = View.inflate(this, R.layout.title_handwrite, null);
        ibBack = (ImageButton) titleView.findViewById(R.id.ib_back);
        ibFront = (ImageButton) titleView.findViewById(R.id.ib_front);
        ibShare = (ImageButton) titleView.findViewById(R.id.ib_share);

        ibBack.setOnClickListener(this);
        ibFront.setOnClickListener(this);
        ibShare.setOnClickListener(this);

        CommonUtil.setLongClick(ibShare,getString(R.string.share_my_pic));

        ibBack.setEnabled(false);
        ibFront.setEnabled(false);

        getTitlebar().addView(titleView);
        getTitlebar().setBackgroundResource(R.color.bg_white);
        getTitlebar().setNavigationIcon(R.drawable.ic_material_arrow_left_dark);
        getTitlebar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ibBack) {
            mHandWritingView.drawBack();
        } else if (v == ibFront) {
            mHandWritingView.drawFront();
        } else if (v == ibShare) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
