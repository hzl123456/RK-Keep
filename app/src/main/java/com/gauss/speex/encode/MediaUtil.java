package com.gauss.speex.encode;

import android.content.Context;

import com.gauss.speex.encode.SpeexDecoder.OnMediaListener;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * 
 * @ClassName: MediaUtil 
 * @Description:媒体服务帮助类
 * @author wangbo 
 * @date 2014-6-11 上午11:17:07 
 *
 */
public class MediaUtil {
	
	private Logger log = Logger.getLogger(MediaUtil.class);
	
	protected Context context;    //播放文件的上下文
	protected SpeexRecorder recorderInstance = null;
	protected SpeexDecoder speexdec = null;
	public MediaUtil(Context context) {
		this.context=context;
	}
	
    /**
     * 
     * @Title: playVoice 
     * @Description:播放语音的声音
     * @param @param aVoiceStr 
     * @return void 
     * @throws
     */
	public void playVoice(String path){
		playVoice(path, null);
	}
	
	/**
	 * 
	 * @param path
	 * @param playFinish 播放结束回调
	 */
	public void playVoice(String path, OnPlayFinish playFinish) {
		try {
			speexdec = new SpeexDecoder(new File(path));
		} catch (Exception e) {
			log.error(e);
		}
		RecordPlayThread rpt = new RecordPlayThread(playFinish);
		new Thread(rpt).start();
	}
	
	public void stopPlay() {
		if (speexdec != null) {
			try {
				speexdec.setPaused(true);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 
	 * @ClassName: RecordPlayThread 
	 * @Description:播放线程
	 * @author wangbo 
	 * @date 2014-6-11 上午11:43:51 
	 *
	 */
	class RecordPlayThread extends Thread {
		
		private OnPlayFinish mOnPlayFinish;
		public RecordPlayThread() {
			this(null);
		}
		
		public RecordPlayThread(OnPlayFinish onPlayFinish) {
			mOnPlayFinish = onPlayFinish;
		}
		
		public void run() {
			try {
				if (speexdec != null)
					speexdec.setOnMediaListener(new MediaPlayVoiceLister());
					if (mOnPlayFinish != null) {
						speexdec.decode(mOnPlayFinish);
					} else {
						speexdec.decode();	// 解码其实是播放
					}
			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	}

	public interface OnPlayFinish {
		void onFinish();
	}
	
    /**
     * 
     * @ClassName: mediaPlayVoiceLister 
     * @Description:媒体播放的监听器
     * @author wangbo 
     * @date 2014-6-11 上午11:30:48 
     *
     */
	private class MediaPlayVoiceLister implements OnMediaListener{
		public void onMedia(SpeexDecoder aDecoder) {
		}	
	}
}
