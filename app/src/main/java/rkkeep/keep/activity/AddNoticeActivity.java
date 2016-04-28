package rkkeep.keep.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.ImageButton;

import java.util.ArrayList;
import java.util.Date;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import rkkeep.keep.R;
import rkkeep.keep.adapter.NoticeAdapter;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.help.PictureChooseHelper;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.NoticeInfo;
import rkkeep.keep.util.NoticeTypeChooseWindow;

/**
 * Created by Au61 on 2016/4/27.
 */
public class AddNoticeActivity extends BaseActivity implements View.OnClickListener {

    private final int SHOWIMAGE_CODE = 88;

    /**
     * title使用
     **/
    private View titleView;
    private ImageButton ibNotice;

    /**
     * 上部分内容使用
     **/
    private RecyclerView rvContent;
    private LinearLayoutManager mLayoutManager;
    private NoticeAdapter mNoticeAdapter;
    private LinearLayout layoutTop;


    /**
     * 底部使用
     **/
    private ImageButton ibAddBox;
    private TextView tvNowTime;
    private ImageButton ibNoteAction;
    private RelativeLayout layoutBottom;

    /**
     * 每次进入的时候都会创建一个notice的实例
     **/
    private NoticeInfo mNoticeInfo;

    /**
     * 选择类型和颜色的弹窗
     **/
    private NoticeTypeChooseWindow mWindow;

    /**
     * 拍照选择图片
     **/
    private PictureChooseHelper mPictureChooseHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);
        //创建notice实例
        if (mNoticeInfo == null) {
            mNoticeInfo = new NoticeInfo();
            if (mNoticeInfo.infos == null) {
                mNoticeInfo.infos = new ArrayList<NoticeImgVoiceInfo>();
            }
            mNoticeInfo.editTime = System.currentTimeMillis();
        }
        initTitle();
        initTop();
        initBottom();
        initHelper();
        setBackColor();
    }

    private void initHelper() {
        mPictureChooseHelper = new PictureChooseHelper(this);
        mPictureChooseHelper.setOnPictureGetListener(new PictureChooseHelper.OnPictureGetListener() {
            @Override
            public void OnPic(String path) {
                mNoticeInfo.infos.add(0, new NoticeImgVoiceInfo(path));
                mNoticeAdapter.setContentSize();
                mNoticeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initTitle() {
        titleView = getLayoutInflater().inflate(R.layout.title_add_notice, null);
        ibNotice = (ImageButton) titleView.findViewById(R.id.ib_notice);
        ibNotice.setOnClickListener(this);

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

    private void initTop() {
        layoutTop = (LinearLayout) findViewById(R.id.layout_top);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        mLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        rvContent.setLayoutManager(mLayoutManager);
        rvContent.addItemDecoration(new SpacesItemDecoration(0, 0, 0, CommonUtil.dip2px(3)));
        mNoticeAdapter = new NoticeAdapter(mNoticeInfo.infos);
        rvContent.setAdapter(mNoticeAdapter);
        mNoticeAdapter.setOnNoticeItemClickListener(new OnNoticeItemClickListener() {
            @Override
            public void onClick(final NoticeImgVoiceInfo info) {
                //点击查看图片
                int num = 0;
                ArrayList<String> infos = new ArrayList<String>();
                NoticeImgVoiceInfo notice = null;
                for (int i = 0; i < mNoticeInfo.infos.size(); i++) {
                    notice = mNoticeInfo.infos.get(i);
                    if (notice.imagePic != null) {
                        infos.add(notice.imagePic);
                    }
                    if (info == notice) {
                        num = i;
                    }
                }
                Intent intent = new Intent(AddNoticeActivity.this, ShowImageActivity.class);
                intent.putExtra("num", num);
                intent.putExtra("data", infos);
                startActivityForResult(intent, SHOWIMAGE_CODE);
            }
        });
    }

    private void showWindowOrDismiss() {

        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        } else {
            mWindow = new NoticeTypeChooseWindow(mNoticeInfo.color, this, layoutBottom);
            mWindow.setOnWindowChooseListener(new NoticeTypeChooseWindow.OnWindowChooseListener() {
                @Override
                public void OnChooseColor(String color) {
                    mNoticeInfo.color = color;
                    setBackColor();
                }

                @Override
                public void OnChooseType(int type) {
                    switch (type) {
                        case NoticeTypeChooseWindow.CHOOSE_PICTURE://选择图片
                            mPictureChooseHelper.showDialog();
                            break;
                        case NoticeTypeChooseWindow.CHOOSE_DRAW://选择绘图

                            break;
                        case NoticeTypeChooseWindow.CHOOSE_VOICE://选择语音

                            break;
                    }
                }
            });
        }

    }

    //设置背景颜色
    private void setBackColor() {
        getTitlebar().setBackgroundColor(Color.parseColor(mNoticeInfo.color));
        layoutTop.setBackgroundColor(Color.parseColor(mNoticeInfo.color));
        layoutBottom.setBackgroundColor(Color.parseColor(mNoticeInfo.color));

        ibAddBox.setBackgroundResource(ColorHelper.getCheckColor(mNoticeInfo.color));
        ibNotice.setBackgroundResource(ColorHelper.getCheckColor(mNoticeInfo.color));
    }


    private void initBottom() {
        ibAddBox = (ImageButton) findViewById(R.id.ib_add_box);
        tvNowTime = (TextView) findViewById(R.id.tv_now_time);
        ibNoteAction = (ImageButton) findViewById(R.id.ib_note_action);
        layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        ibAddBox.setOnClickListener(this);
        ibNoteAction.setOnClickListener(this);

        Date date = new Date(mNoticeInfo.editTime);
        tvNowTime.setText(getString(R.string.addnotice_edit_time) + CommonUtil.changeOne2Two(date.getHours()) + ":" + CommonUtil.changeOne2Two(date.getMinutes()));
    }

    @Override
    public void onClick(View v) {
        if (v == ibNotice) {//一些小的操作



        } else if (v == ibAddBox) {//添加图片或者录音啥的
            showWindowOrDismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        } else {
            super.onBackPressed();
            //TODO 返回的同时保存记录
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RESULT_CANCELED) {
            if (requestCode == SHOWIMAGE_CODE) {
                int number = data.getExtras().getInt("num");
                mNoticeInfo.infos.remove(number);
                mNoticeAdapter.setContentSize();
                mNoticeAdapter.notifyDataSetChanged();
            } else {
                if (mPictureChooseHelper != null) {
                    mPictureChooseHelper.onActivityResult(this, requestCode, resultCode, data);
                }
            }

        }
    }

}
