package com.gauss.speex.encode;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.RecoverySystem.ProgressListener;

import com.gauss.speex.encode.MediaUtil.OnPlayFinish;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SpeexDecoder {
	protected Speex speexDecoder;

	protected boolean enhanced = false;

	private boolean paused = false;

	protected String srcFile;

	private List<ProgressListener> listenerList = new ArrayList<ProgressListener>();

	private File srcPath;
	private AudioTrack track;

	private int HEADERSIZE = 80;
	private int FRAMESIZE = 160;

	private OnMediaListener mOnMediaListener;

	public SpeexDecoder(File srcPath) throws Exception {
		this.srcPath = srcPath;
	}

	private void initializeAndroidAudio(int sampleRate) throws Exception {
		int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		if (minBufferSize < 0) {
			throw new Exception("Failed to get minimum buffer size: " + Integer.toString(minBufferSize));
		}

		track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

	}

	public void addOnMetadataListener(ProgressListener l) {
		listenerList.add(l);
	}

	public synchronized void setPaused(boolean paused) {
		this.paused = paused;
	}

	public synchronized boolean isPaused() {
		return paused;
	}

	public void decode() throws Exception {
		decode(null);
	}

	@SuppressWarnings("resource")
	public void decode(OnPlayFinish onFinish) throws Exception {

		byte[] header = new byte[2048];
		byte[] payload = new byte[65536];
		final int SEGOFFSET = 72;
		int curseg = 0;
		int decsize = 0;
		int packetNo = 0;
		// construct a new decoder
		speexDecoder = new Speex();
		speexDecoder.init();
		// open the input stream
		RandomAccessFile dis = new RandomAccessFile(srcPath, "r");

		try {
			// read until we get to EOF
			while (true) {
				if (Thread.interrupted()) {
					dis.close();
					track.stop();

					if (mOnMediaListener != null) {
						mOnMediaListener.onMedia(this);
					}
					return;
				}

				while (this.isPaused()) {
					track.stop();

					if (mOnMediaListener != null) {
						mOnMediaListener.onMedia(this);
					}
					Thread.sleep(100);
				}

				if (dis.length() <= 80) {
					return;
				}
				dis.readFully(header, 0, HEADERSIZE);
				/* decode each segment, writing output to wav */
				for (curseg = 0; curseg < ((int) dis.length() - HEADERSIZE) / readInt(header, SEGOFFSET); curseg++) {

					if (Thread.interrupted()) {
						dis.close();
						track.stop();

						if (mOnMediaListener != null) {
							mOnMediaListener.onMedia(this);
						}
						return;
					}

					while (this.isPaused()) {
						track.stop();

						if (mOnMediaListener != null) {
							mOnMediaListener.onMedia(this);
						}
						Thread.sleep(100);
					}

					dis.readFully(payload, 0, readInt(header, SEGOFFSET));
					if (packetNo == 0) {

						packetNo++;
						initializeAndroidAudio(8000);
					} else if (packetNo == 1) { // Ogg Comment packet
						packetNo++;
					} else {
						short[] decoded = new short[160];
						if ((decsize = speexDecoder.decode(payload, decoded, FRAMESIZE)) > 0) {
							track.write(decoded, 0, decsize);
							track.setStereoVolume(1f, 1f);
							track.play();
						}
					}
				}
			}
		} catch (EOFException eof) {
		} finally {
			track.stop();
			track.release();

			if (mOnMediaListener != null) {
				mOnMediaListener.onMedia(this);
			}
			if (onFinish != null) {
				onFinish.onFinish();
			}
			System.out.println("release............");
		}

		dis.close();
	}

	protected static int readInt(final byte[] data, final int offset) {
		/*
		 * no 0xff on the last one to keep the sign
		 */
		return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16) | (data[offset + 3] << 24);
	}

	protected static long readLong(final byte[] data, final int offset) {
		/*
		 * no 0xff on the last one to keep the sign
		 */
		return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16) | ((data[offset + 3] & 0xff) << 24) | ((data[offset + 4] & 0xff) << 32)
				| ((data[offset + 5] & 0xff) << 40) | ((data[offset + 6] & 0xff) << 48) | (data[offset + 7] << 56);
	}

	protected static int readShort(final byte[] data, final int offset) {
		/*
		 * no 0xff on the last one to keep the sign
		 */
		return (data[offset] & 0xff) | (data[offset + 1] << 8);
	}

	public void setOnMediaListener(OnMediaListener mediaListener) {
		mOnMediaListener = mediaListener;
	}

	public interface OnMediaListener {
		/**
		 * 停止/暂停播放
		 * 
		 * @param aDecoder
		 */
		void onMedia(SpeexDecoder aDecoder);
	}

}
