package rkkeep.keep.util;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.speex.encode.SpeexRecorder;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.widget.LeaveButton;
import rkkeep.keep.R;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;

/**
 * Created by Au61 on 2016/5/11.
 */
public class VoiceSetWindow extends PopupWindow implements View.OnClickListener {

    public static final int WHAT_SET_MIC_INDICATE = 4;
    public static final int WINDOW_DISS_MISS = 3;

    private RelativeLayout recordContainer;
    private ImageButton ibClose;
    private ImageView ivMic;
    private ImageView ivMicIndicate;
    private LeaveButton lbVoice;
    private TextView tvRecordHint;

    /**
     * 录音路径
     */
    protected String recordPath;
    /**
     * 录音工具
     */
    protected SpeexRecorder sRec = null;


    /**
     * 用来执行录音线程 注意：不可以将发送/读取的线程也放在这个里面，防止线程池被占满不能进行录音
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SET_MIC_INDICATE:
                    ivMicIndicate.getDrawable().setLevel(msg.arg1);
                    break;
                case WINDOW_DISS_MISS:
                    if (mOnVoiceFinishListener != null) {
                        mOnVoiceFinishListener.onFinish((NoticeImgVoiceInfo) msg.obj);
                    }
                    dismiss();
                    break;
            }
            return true;
        }
    });

    public VoiceSetWindow() {
        initPopuView();
        initRecordBtn();
    }

    public void showPopuwindow(View view) {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.PopupAnimation);
        setOutsideTouchable(true);
        showAtLocation(view, Gravity.TOP, 0, 0);
    }

    private void initPopuView() {
        View headerView = View.inflate(RKApplication.getInstance(), R.layout.layout_voiceset, null);
        ibClose = (ImageButton) headerView.findViewById(R.id.ib_close);
        ivMic = (ImageView) headerView.findViewById(R.id.iv_mic);
        ivMicIndicate = (ImageView) headerView.findViewById(R.id.iv_indicate);
        lbVoice = (LeaveButton) headerView.findViewById(R.id.lb_voice);
        recordContainer = (RelativeLayout) headerView.findViewById(R.id.recording_container);
        tvRecordHint = (TextView) headerView.findViewById(R.id.tv_recording_hint);

        ibClose.setOnClickListener(this);
        setContentView(headerView);
    }

    /**
     * 初始化录音按钮
     */
    private void initRecordBtn() {
        lbVoice.setOnLeaveListener(new RecordListener());
    }

    @Override
    public void onClick(View v) {
        if (v == ibClose) {
            dismiss();
        }
    }

    class RecordListener implements LeaveButton.OnLeaveListener {

        private long pressedTime;
        /**
         * 大于0表示需要发送，且数字为时长
         */
        private HashMap<String, Long> sendableList = new HashMap<String, Long>();

        /**
         * 手指在外
         */
        private void onOutter() {
            tvRecordHint.setText(R.string.activity_chat_release_to_cancel);
            // 改变字体颜色
            tvRecordHint.setSelected(true);
            // 改变展示图片
            ivMic.setSelected(true);
            ivMicIndicate.setVisibility(View.GONE);
            lbVoice.setText(R.string.activity_chat_release_to_cancel);
        }

        /**
         * 手指在内
         */
        private void onInner() {
            tvRecordHint.setText(R.string.activity_chat_move_to_cancel);
            // 改变字体颜色
            tvRecordHint.setSelected(false);
            // 改变展示图片
            ivMic.setSelected(false);
            ivMicIndicate.setVisibility(View.VISIBLE);
            lbVoice.setText(R.string.activity_chat_move_to_cancel);
        }

        @Override
        public void onLongPress(LeaveButton lb) {
            recordContainer.setVisibility(View.VISIBLE);
            onInner();
            // 拼接文件名
            recordPath = VoiceUtil.getVoicePath() + System.currentTimeMillis() + ".dmf";
            FileUtil.checkedFileReachable(recordPath);
            if (sRec == null) {
                sRec = new SpeexRecorder(recordPath);
                sRec.setSoundVolumeListener(new SpeexRecorder.SoundVolumeListener() {

                    @Override
                    public void volume(int vol) {
                        mHandler.sendMessage(mHandler.obtainMessage(WHAT_SET_MIC_INDICATE, ((vol + "").length() - 5), 0));
                    }

                    @Override
                    public void onFinish(final String fileName) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Long duration = sendableList.get(fileName);
                                duration = duration == null ? -1 : duration;
                                if (duration > 500) {//大于500毫秒才算录音成功
                                    mHandler.sendMessage(mHandler.obtainMessage(WINDOW_DISS_MISS, new NoticeImgVoiceInfo(recordPath, duration)));
                                } else {
                                    CommonUtil.showToast("录音时间太短");
                                    File df = new File(fileName);
                                    df.delete();
                                }
                                sendableList.remove(fileName);
                            }
                        }).start();
                    }

                    @Override
                    public void onFail(int bufferRead) {
                        if (mOnVoiceFinishListener != null) {
                            mOnVoiceFinishListener.onError("录音权限未打开");
                        }
                        dismiss();
                    }
                });
            } else {
                sRec.setFileName(recordPath);
            }
            sRec.setRecording(true);
            pressedTime = System.currentTimeMillis();
            mExecutor.execute(sRec);
        }

        @Override
        public void onRelease(LeaveButton lb) {
            // 松开结束录音，判断语音长度是否达到最小长度，如果达到最小长度就上传文件，然后发送出去
            recordContainer.setVisibility(View.GONE);
            lb.setText(R.string.activity_chat_press_record);
            sRec.setRecording(false);
            long duration = (System.currentTimeMillis() - pressedTime);
            if (duration < 1000) {
                CommonUtil.showToast(R.string.voice_duration_too_short);
                return;
            }
            sendableList.put(recordPath, duration);
        }

        @Override
        public void onStateChange(LeaveButton lb, boolean inRegion) {
            if (inRegion) {
                onInner();
            } else {
                onOutter();
            }
        }

        @Override
        public void onCancel(LeaveButton lb) {
            recordContainer.setVisibility(View.GONE);
            lb.setText(R.string.activity_chat_press_record);
            sRec.setRecording(false);
            sendableList.put(recordPath, -1l);
            // 取消发送则删除掉文件
            File df = new File(recordPath);
            df.delete();
        }
    }

    private OnVoiceFinishListener mOnVoiceFinishListener;

    public void setOnVoiceFinishListener(OnVoiceFinishListener mOnVoiceFinishListener) {
        this.mOnVoiceFinishListener = mOnVoiceFinishListener;
    }

    public interface OnVoiceFinishListener {
        void onFinish(NoticeImgVoiceInfo info);

        void onError(String errorText);
    }
}
