package rkkeep.keep.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.speex.encode.MediaUtil;
import com.rey.material.widget.ImageButton;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import rkkeep.keep.R;
import rkkeep.keep.adapter.NoticeAdapter;
import rkkeep.keep.adapter.listener.OnNoticeItemClickListener;
import rkkeep.keep.adapter.listener.OnVideoClickListener;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.help.NoticeChooseHelper;
import rkkeep.keep.help.PictureChooseHelper;
import rkkeep.keep.help.VideoChooseHelper;
import rkkeep.keep.pojo.AddressInfo;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.NoticeInfo;
import rkkeep.keep.pojo.VideoInfo;
import rkkeep.keep.util.NoticeTypeChooseWindow;
import rkkeep.keep.util.PlayCallback;
import rkkeep.keep.util.VoiceSetWindow;

/**
 * Created by Au61 on 2016/4/27.
 */
public class AddNoticeActivity extends BaseActivity implements View.OnClickListener {

    private final int SHOWIMAGE_CODE = 88;
    private final int DRAW_PIC_CODE = 512;

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
     * 录音
     **/
    private VoiceSetWindow mVoiceSetWindow;
    /**
     * 选择视频
     **/
    private VideoChooseHelper mVideoChooseHelper;

    /**
     * 播放器
     */
    public MediaUtil mMediaUtil;

    /**
     * 选择地点和时间
     **/
    private NoticeChooseHelper mNoticeChooseHelper;

    //其他相关
    private RelativeLayout layoutBaseTop;
    //播放视频相关
    private RelativeLayout layoutBaseVideo;
    private FrameLayout mVideoLayout;
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private ImageButton ibClose;

    //正在播放的videoInfo
    private VideoInfo playVideoInfo;
    private boolean isFullscreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);
        initTitle();
        showDelete();
        initTop();
        initVideo();
        initBottom();
        initHelper();
        setBackColor();
    }

    private void setVideoShow(VideoInfo info) {
        //显示全屏
        setFullScreen(true);
        //去掉toolbar
        showCustomTitlebar(false);
        //显示出video的layout
        layoutBaseVideo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        layoutBaseVideo.setVisibility(View.VISIBLE);
        layoutBaseTop.setVisibility(View.GONE);
        //不上次播放的就重新设置
        playVideoInfo = info;
        //重置播放器
        mVideoView.resume();
        //设置路径和播放名称并开始播放
        mVideoView.setVideoPath(playVideoInfo.videoPath);
        mMediaController.setTitle(playVideoInfo.videoName);
        mVideoView.seekTo(playVideoInfo.watchLength < mVideoView.getDuration() ? playVideoInfo.watchLength : 0);
        mVideoView.start();
    }

    private void showTopContent() {
        //暂停播放,关闭播放器
        mVideoView.pause();
        mVideoView.closePlayer();
        //隐藏video的layout
        layoutBaseVideo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        layoutBaseVideo.setVisibility(View.GONE);
        layoutBaseTop.setVisibility(View.VISIBLE);
        //显示全屏
        setFullScreen(false);
        //去掉toolbar
        showCustomTitlebar(true);
    }


    private void initVideo() {
        ibClose = (ImageButton) findViewById(R.id.ib_close);
        layoutBaseVideo = (RelativeLayout) findViewById(R.id.layout_base_video);
        mVideoLayout = (FrameLayout) findViewById(R.id.video_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        ibClose.setOnClickListener(this);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {
                AddNoticeActivity.this.isFullscreen = isFullscreen;
                if (isFullscreen) {
                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoLayout.setLayoutParams(layoutParams);
                } else {
                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = getResources().getDimensionPixelSize(R.dimen.video_height);
                    mVideoLayout.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) { // 视频暂停
                //保存当前的播放进度
                playVideoInfo.watchLength = mVideoView.getCurrentPosition();
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) { // 视频开始播放或恢复播放

            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {// 视频开始缓冲
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {// 视频结束缓冲
            }
        });
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
        //实例化选择图片
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
        //实例化选择语音
        mVoiceSetWindow = new VoiceSetWindow();
        mVoiceSetWindow.setOnVoiceFinishListener(new VoiceSetWindow.OnVoiceFinishListener() {
            @Override
            public void onFinish(NoticeImgVoiceInfo info) {
                if (mNoticeInfo.voiceInfos == null) {
                    mNoticeInfo.voiceInfos = new ArrayList<NoticeImgVoiceInfo>();
                }
                mNoticeInfo.voiceInfos.add(0, info);
                mNoticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorText) {
                CommonUtil.showSnackToast(errorText, getTitlebar());
            }
        });
        //实例化选择视频
        mVideoChooseHelper = new VideoChooseHelper(this, getTitlebar());
        mVideoChooseHelper.setOnVideoGetListener(new VideoChooseHelper.OnVideoGetListener() {
            @Override
            public void OnPic(String path, String imagePath) {
                if (mNoticeInfo.videoInfos == null) {
                    mNoticeInfo.videoInfos = new ArrayList<VideoInfo>();
                }
                mNoticeInfo.videoInfos.add(new VideoInfo(path, imagePath));
                mNoticeAdapter.notifyDataSetChanged();
            }
        });
        //实例化选择提醒
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
        mMediaUtil = new MediaUtil(this);
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
        layoutBaseTop = (RelativeLayout) findViewById(R.id.layout_base_top);
        layoutTop = (LinearLayout) findViewById(R.id.layout_top);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        mLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        rvContent.setLayoutManager(mLayoutManager);
        rvContent.addItemDecoration(new SpacesItemDecoration(0, 0, 0, CommonUtil.dip2px(3)));
        mNoticeAdapter = new NoticeAdapter(mNoticeInfo.infos, mNoticeInfo.voiceInfos, mNoticeInfo.videoInfos, this);
        rvContent.setAdapter(mNoticeAdapter);
        //图片录音点击
        mNoticeAdapter.setOnNoticeItemClickListener(new OnNoticeItemClickListener() {

            @Override
            public void onVoiceClick(NoticeImgVoiceInfo info, int position) {
                //点击了录音，需要进行播放
                mMediaUtil.stopPlay();
                if (mNoticeAdapter.isPlayingInfo == info) {
                    mNoticeAdapter.isPlayingInfo = null;
                    mNoticeAdapter.notifyDataSetChanged();
                    return;
                }
                playVoice(info);
            }

            @Override
            public void onVoiceLongClick(final NoticeImgVoiceInfo info, final int position) {
                showDialogMessage("确定删除该录音文件？", "删除文件", getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoticeInfo.voiceInfos.remove(info);
                        mNoticeAdapter.notifyDataSetChanged();
                    }
                }, null);
            }

            @Override
            public void onClick(final NoticeImgVoiceInfo info, int position) {
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
        //视频点击
        mNoticeAdapter.setOnVideoClickListener(new OnVideoClickListener() {
            @Override
            public void onClick(VideoInfo info, int position) {
                setVideoShow(info);
            }

            @Override
            public void onLongClick(final VideoInfo info, int position) {
                showDialogMessage("确定删除该视频文件？", "删除文件", getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoticeInfo.videoInfos.remove(info);
                        mNoticeAdapter.notifyDataSetChanged();
                    }
                }, null);
            }
        });

    }

    private void playVoice(final NoticeImgVoiceInfo info) {
        //判断文件是否存在，不存在就进行下载，然后播放
        final File file = new File(info.voicePic);//录音保存位置
        if (file.isFile()) {//文件存在就直接播放
            mNoticeAdapter.isPlayingInfo = info;
            mNoticeAdapter.notifyDataSetChanged();
            mMediaUtil.playVoice(file.getAbsolutePath(), new PlayCallback(mNoticeAdapter));
        } else {//文件不存在就需要重新下载
            CommonUtil.showSnackToast("该录音文件已经不存在了", getTitlebar());
        }
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
                            startActivityForResult(HandWritingActivity.class, DRAW_PIC_CODE);
                            break;
                        case NoticeTypeChooseWindow.CHOOSE_VOICE://选择语音
                            mVoiceSetWindow.showPopuwindow(getTitlebar());
                            break;
                        case NoticeTypeChooseWindow.CHOOSE_VIDEO://选择视频
                            mVideoChooseHelper.showDialog();
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
        } else if (v == ibAddBox) {//添加图片或者录音或者视频
            showWindowOrDismiss();
        } else if (v == tvNoticeTime) {//提醒的时间
            mNoticeChooseHelper.showTimeDialog();
        } else if (v == tvNoticeAddress) {//提醒的地点
            mNoticeChooseHelper.showAddressDialog();
        } else if (v == ibClose) {
            showTopContent();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        } else if (mVoiceSetWindow != null && mVoiceSetWindow.isShowing()) {
            mVoiceSetWindow.dismiss();
        } else if (isFullscreen) {
            mVideoView.setFullscreen(false);
        } else if (layoutBaseVideo.getVisibility() == View.VISIBLE) {
            showTopContent();
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
        //设置图片消息
        if (mNoticeInfo.infos != null && mNoticeInfo.infos.size() > 0) {
            mNoticeInfo.hasPic = true;
            mNoticeInfo.noticeImgVoiceInfosString = CommonUtil.getGson().toJson(mNoticeInfo.infos);
        } else {
            mNoticeInfo.noticeImgVoiceInfosString = null;
            mNoticeInfo.hasPic = false;
        }
        //设置语音消息
        if (mNoticeInfo.voiceInfos != null && mNoticeInfo.voiceInfos.size() > 0) {
            mNoticeInfo.hasVoice = true;
            mNoticeInfo.noticeVoiceInfosString = CommonUtil.getGson().toJson(mNoticeInfo.voiceInfos);
        } else {
            mNoticeInfo.noticeVoiceInfosString = null;
            mNoticeInfo.hasVoice = false;
        }
        //设置视频信息
        if (mNoticeInfo.videoInfos != null && mNoticeInfo.videoInfos.size() > 0) {
            mNoticeInfo.hasVideo = true;
            mNoticeInfo.videoInfosString = CommonUtil.getGson().toJson(mNoticeInfo.videoInfos);
        } else {
            mNoticeInfo.videoInfosString = null;
            mNoticeInfo.hasVideo = false;
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
        if (mVideoChooseHelper != null) {
            mVideoChooseHelper.onActivityResult(this, requestCode, resultCode, data);
        }
        if (resultCode != RESULT_CANCELED && requestCode == SHOWIMAGE_CODE) {
            int number = data.getExtras().getInt("num");
            HashMap<Integer, String> changeMap = (HashMap<Integer, String>) data.getExtras().get("change");
            //先改路径，再判断是否有删除
            for (Integer key : changeMap.keySet()) {
                mNoticeInfo.infos.get(key).imagePic = changeMap.get(key);
            }
            if (number != -1) {//不为-1，表示有删除的
                mNoticeInfo.infos.remove(number);
                mNoticeAdapter.setContentSize();
            }
            mNoticeAdapter.notifyDataSetChanged();
        }
        if (resultCode == RESULT_OK && requestCode == DRAW_PIC_CODE) {
            String path = data.getExtras().getString("data");
            mNoticeInfo.infos.add(0, new NoticeImgVoiceInfo(path));
            mNoticeAdapter.setContentSize();
            mNoticeAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaUtil.stopPlay();
    }
}
