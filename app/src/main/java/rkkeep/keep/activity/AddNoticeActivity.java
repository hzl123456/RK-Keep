package rkkeep.keep.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.ImageButton;

import java.util.ArrayList;
import java.util.Date;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import rkkeep.keep.R;
import rkkeep.keep.adapter.NoticeAdapter;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.help.NoticeChooseHelper;
import rkkeep.keep.help.PictureChooseHelper;
import rkkeep.keep.pojo.AddressInfo;
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
    private NoticeInfoDbHelper dbHelper;


    /**
     * 底部使用
     **/
    private ImageButton ibAddBox;
    private TextView tvNowTime;
    private ImageButton ibNoteAction;
    private RelativeLayout layoutBottom;
    private ClearEditText etTitle;
    private ClearEditText etContent;
    private TextView tvNoticeTime;
    private TextView tvNoticeAddress;

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

    /**
     * 选择地点和时间
     **/
    private NoticeChooseHelper mNoticeChooseHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);
        initTitle();
        showDelete();
        initTop();
        initBottom();
        initHelper();
        setBackColor();
    }

    private void showDelete() {
        if (dbHelper.getNoticeInfoType(mNoticeInfo.infoId) == NoticeInfo.NOMAL_TYPE_DUSTBIN) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("该记事已经添加到回收站中，不得编辑，请返回上一级进行刷新");
            dialog.setCancelable(false);
            dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        }
    }

    private void initHelper() {
        mPictureChooseHelper = new PictureChooseHelper(this);
        mPictureChooseHelper.setOnPictureGetListener(new PictureChooseHelper.OnPictureGetListener() {
            @Override
            public void OnPic(String path) {
                if (mNoticeInfo.infos == null) {
                    mNoticeInfo.infos = new ArrayList<NoticeImgVoiceInfo>();
                }
                mNoticeInfo.infos.add(0, new NoticeImgVoiceInfo(path));
                mNoticeAdapter.setContentSize();
                mNoticeAdapter.notifyDataSetChanged();
            }
        });
        mNoticeChooseHelper = new NoticeChooseHelper(this);
        mNoticeChooseHelper.setOnNoticeChooseListener(new NoticeChooseHelper.OnNoticeChooseListener() {
            @Override
            public void OnTime(Date date) {
                if (date == null) {
                    mNoticeInfo.remindTime = 0;
                    tvNoticeTime.setVisibility(View.GONE);
                } else {
                    if (System.currentTimeMillis() >= date.getTime()) {
                        CommonUtil.showSnackToast(getString(R.string.error_time), getTitlebar());
                    } else {
                        mNoticeInfo.remindTime = date.getTime();
                        tvNoticeTime.setVisibility(View.VISIBLE);
                        tvNoticeTime.setText(CommonUtil.getAffineTimestampForGroupChat(mNoticeInfo.remindTime));
                    }
                }

            }

            @Override
            public void OnAddress(AddressInfo addressInfo) {
                if (addressInfo == null) {
                    mNoticeInfo.addressInfo = null;
                    tvNoticeAddress.setVisibility(View.GONE);
                } else {
                    mNoticeInfo.addressInfo = addressInfo;
                    tvNoticeAddress.setVisibility(View.VISIBLE);
                    tvNoticeAddress.setText(mNoticeInfo.addressInfo.addressName);
                }
            }
        });
    }

    private void initTitle() {
        dbHelper = new NoticeInfoDbHelper();
        mNoticeInfo = (NoticeInfo) getIntent().getExtras().get("data");
        titleView = getLayoutInflater().inflate(R.layout.title_add_notice, null);
        ibNotice = (ImageButton) titleView.findViewById(R.id.ib_notice);
        ibNotice.setOnClickListener(this);

        CommonUtil.setLongClick(ibNotice, "选择提醒的方式");
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
        mNoticeAdapter = new NoticeAdapter(mNoticeInfo.infos, this);
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

    public void initBottomView(View view) {
        etTitle = (ClearEditText) view.findViewById(R.id.et_title);
        etContent = (ClearEditText) view.findViewById(R.id.et_content);
        tvNoticeTime = (TextView) view.findViewById(R.id.tv_notice_time);
        tvNoticeAddress = (TextView) view.findViewById(R.id.tv_notice_address);

        tvNoticeTime.setOnClickListener(this);
        tvNoticeAddress.setOnClickListener(this);

        //设置标题
        if (!StringUtil.isEmptyString(mNoticeInfo.title)) {
            etTitle.setText(mNoticeInfo.title);
        }
        //设置内容
        if (!StringUtil.isEmptyString(mNoticeInfo.content)) {
            etContent.setText(mNoticeInfo.content);
        }
        //设置地址
        if (mNoticeInfo.addressInfo != null) {
            tvNoticeAddress.setVisibility(View.VISIBLE);
            tvNoticeAddress.setText(mNoticeInfo.addressInfo.addressName);
            Log.i("address->", CommonUtil.getGson().toJson(mNoticeInfo.addressInfo));
        }
        //设置时间
        if (mNoticeInfo.remindTime != 0) {
            tvNoticeTime.setVisibility(View.VISIBLE);
            tvNoticeTime.setText(CommonUtil.getAffineTimestampForGroupChat(mNoticeInfo.remindTime));
        }


    }

    private void showWindowOrDismiss() {

        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        } else {
            mWindow = new NoticeTypeChooseWindow(mNoticeInfo.color, this);
            mWindow.showPopuWindow(layoutBottom);
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
            mNoticeChooseHelper.showDialog();
        } else if (v == ibAddBox) {//添加图片或者录音啥的
            showWindowOrDismiss();
        } else if (v == tvNoticeTime) {//提醒的时间
            mNoticeChooseHelper.showTimeDialog();
        } else if (v == tvNoticeAddress) {//提醒的地点
            mNoticeChooseHelper.showAddressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        } else {
            saveNoticeInfoAndBack();
        }
    }

    private void saveNoticeInfoAndBack() {
        //新的消息需要设置消息id
        if (mNoticeInfo.infoId == 0) {
            mNoticeInfo.infoId = System.currentTimeMillis();
        }
        if (etTitle != null) {
            mNoticeInfo.title = etTitle.getText().toString();
        }
        if (etContent != null) {
            mNoticeInfo.content = etContent.getText().toString();
        }
        //判断信息的类型
        if (mNoticeInfo.remindTime != 0 && mNoticeInfo.addressInfo == null) {
            mNoticeInfo.infoType = NoticeInfo.TIXING_TYPE;
            mNoticeInfo.addressInfoString = null;
        } else if (mNoticeInfo.remindTime == 0 && mNoticeInfo.addressInfo != null) {
            mNoticeInfo.infoType = NoticeInfo.TIXING_TYPE;
            mNoticeInfo.addressInfoString = CommonUtil.getGson().toJson(mNoticeInfo.addressInfo);
        } else if (mNoticeInfo.remindTime != 0 && mNoticeInfo.addressInfo != null) {
            mNoticeInfo.infoType = NoticeInfo.TIXING_TYPE;
            mNoticeInfo.addressInfoString = CommonUtil.getGson().toJson(mNoticeInfo.addressInfo);
        }
        if (mNoticeInfo.infos != null && mNoticeInfo.infos.size() > 0) {
            for (int i = 0; i < mNoticeInfo.infos.size(); i++) {
                if (!StringUtil.isEmptyString(mNoticeInfo.infos.get(i).imagePic)) {
                    mNoticeInfo.hasPic = true;
                }
                if (!StringUtil.isEmptyString(mNoticeInfo.infos.get(i).voicePic)) {
                    mNoticeInfo.hasVoice = true;
                }
            }
            mNoticeInfo.noticeImgVoiceInfosString = CommonUtil.getGson().toJson(mNoticeInfo.infos);
        }
        dbHelper.saveNoticeInfo(mNoticeInfo);
        Intent intent = new Intent();
        intent.putExtra("data", mNoticeInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPictureChooseHelper != null) {
            mPictureChooseHelper.onActivityResult(this, requestCode, resultCode, data);
        }
        if (mNoticeChooseHelper != null) {
            mNoticeChooseHelper.onActivityResult(this, requestCode, resultCode, data);
        }
        if (resultCode != RESULT_CANCELED && requestCode == SHOWIMAGE_CODE) {
            int number = data.getExtras().getInt("num");
            mNoticeInfo.infos.remove(number);
            mNoticeAdapter.setContentSize();
            mNoticeAdapter.notifyDataSetChanged();
        }
    }

}
