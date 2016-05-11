package com.gauss.speex.encode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Gauss
 * ʹ��OGG��װ��д�ļ�
 *
 */
public class SpeexWriter extends FileWriter implements Runnable {

	private OutputStream out;


	private int mode = 0;

	// 8000; 16000; 32000; 8000;
	protected int sampleRate = 8000;

	/** Defines the number of channels of the audio input (1=mono, 2=stereo). */
	protected int channels = 1;

	/** Defines the number of frames per speex packet. */
	protected int nframes = 1;

	/** Defines whether or not to use VBR (Variable Bit Rate). */
	protected boolean vbr = false;
	/** Data buffer */
	private byte[] dataBuffer;
	/** Pointer within the Data buffer */
	private int dataBufferPtr;
	
	public int packetlength;
	/** Header buffer */
//	private byte[] headerBuffer;
	/** Pointer within the Header buffer */
//	private int headerBufferPtr;
	/** Speex packet count within an Ogg Page */
	private int packetCount;
	/**
	 * Absolute granule position (the number of audio samples from beginning of
	 * file to end of Ogg Packet).
	 */
//	private long granulepos;
//	private Logger log = LoggerFactory.getLogger(SpeexWriter.class);
	private final Object mutex = new Object();
	
	private volatile boolean isRecording;
	private processedData pData;
	private List<processedData> list;

	public static int write_packageSize = 1024;

	public SpeexWriter(String fileName) {
		super();
		
		
		list = Collections.synchronizedList(new LinkedList<processedData>());

		init(fileName);
	}
	
	public void init(String fileName){

		dataBuffer = new byte[65565];
		dataBufferPtr = 0;
//		headerBuffer = new byte[255];
//		headerBufferPtr = 0;
		packetCount = 0;
		packetlength = 0;
//		granulepos = 0;

		mode =0;
		sampleRate=8000;//people
		vbr=true;
		
		try {
			open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (this.isRecording() || list.size() > 0) {

			if (list.size() > 0) {
				pData = list.remove(0);
				try {
					writeTag(pData.processed, pData.size);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		stop();
	}

	public void putData(final byte[] buf, int size) {

		processedData data = new processedData();
		//data.ts = ts;
		data.size = size;
		System.arraycopy(buf, 0, data.processed, 0, size);
		list.add(data);
	}

	public void stop() {
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
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

	class processedData {
		private int size;
		private byte[] processed = new byte[write_packageSize];
	}

	public void writeTag(byte[] buf, int size) throws IOException {
		try {
			writePacket(buf, 0, size);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void flush(final boolean eos) throws IOException {
		byte[] header;
		header = buildSpeexHeader(sampleRate, mode, channels, vbr, packetCount,packetlength);
		out.write(header);
		out.write(dataBuffer, 0, dataBufferPtr);
		dataBufferPtr = 0;
		packetlength = 0;
//		headerBufferPtr = 0;
		packetCount = 0;
	}

	@Override
	public void close() throws IOException {
		flush(true);
		out.close();
	}

	@Override
	public void open(File file) throws IOException {
		file.delete();
		out = new FileOutputStream(file);
	}

	@Override
	public void open(String filename) throws IOException {
		open(new File(filename));
	}

	@Override
	public void writeHeader(String comment) throws IOException {
		
	}

	@Override
	public void writePacket(byte[] data, int offset, int len)
			throws IOException {
		if (len <= 0) { 
			return;
		}
		System.arraycopy(data, offset, dataBuffer, dataBufferPtr, len);
		dataBufferPtr += len;
//		headerBuffer[headerBufferPtr++] = (byte) len;
		packetCount++;
//		granulepos += nframes * (mode == 2 ? 640 : (mode == 1 ? 320 : 160));
		
	}

}
