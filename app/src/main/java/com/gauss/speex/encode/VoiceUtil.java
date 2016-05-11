package com.gauss.speex.encode;

import java.io.File;

import cn.xmrk.rkandroid.utils.CommonUtil;


public class VoiceUtil {

	public static String getVoicePath() {
		String path = CommonUtil.getDir() + File.separator + "RKVoice";
		File file = new File(path);
		if (!file.exists()) { // 如果目录不存在，则创建一个名为"sddVoice"的目录
			file.mkdir();
		}
		return path + File.separator;
	}

	public static String getImagePath() {
		String path = CommonUtil.getDir() + File.separator + "sddImage";
		File file = new File(path);
		if (!file.exists()) { // 如果目录不存在，则创建一个名为"sddImage"的目录
			file.mkdir();
		}
		return path + File.separator;
	}

}
