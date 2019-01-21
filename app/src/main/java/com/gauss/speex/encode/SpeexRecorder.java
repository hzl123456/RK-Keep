package com.gauss.speex.encode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class SpeexRecorder implements Runnable {

	// private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	private String fileName = null;
	private SoundVolumeListener mVolumeListener;

	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setSoundVolumeListener(SoundVolumeListener volListener) {
		this.mVolumeListener = volListener;
	}

	public void run() {

		SpeexEncoder encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		short[] tempBuffer = new short[packagesize];

		AudioRecord recordInstance = new AudioRecord(AudioSource.MIC, frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);

		recordInstance.startRecording();

		while (this.isRecording) {
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				if (mVolumeListener != null) {
					mVolumeListener.onFail(bufferRead);
				}
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				if (mVolumeListener != null) {
					mVolumeListener.onFail(bufferRead);
				}
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				if (mVolumeListener != null) {
					mVolumeListener.onFail(bufferRead);
				}
			}
			encoder.putData(tempBuffer, bufferRead);
			// 如果存在音量监听，就计算音量且传到监听中
			if (mVolumeListener != null) {
				int v = 0;
				// 缓存内容取出，进行平方和运算
				for (int i = 0; i < bufferRead; i++) {
					v += tempBuffer[i] * tempBuffer[i];
				}
				mVolumeListener.volume(v / bufferRead);
			}
		}
		stop(recordInstance);
		encoder.setRecording(false);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}
		if (mVolumeListener != null) {
			mVolumeListener.onFinish(fileName);
		}
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };

	public AudioRecord findAudioRecord() {
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

						if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
							AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
								return recorder;
						}
					} catch (Exception e) {
						Log.e("bug:", rate + "Exception, keep.keystore trying.", e);
					}
				}
			}
		}
		return null;
	}

	public void stop(AudioRecord recordInstance) {

		recordInstance.stop();
		recordInstance.release();
	}

	/**
	 * 音量监听
	 * 
	 * @author 思落羽 2014年8月13日 下午5:25:59
	 *
	 */
	public interface SoundVolumeListener {
		void volume(int vol);

		void onFinish(String fileName);

		/** 失败，，权限没打开 **/
		void onFail(int bufferRead);
	}
}
