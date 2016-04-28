package cn.xmrk.rkandroid.utils;

import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.xmrk.rkandroid.application.RKApplication;

public class StringUtil {
	
	/**
	 * 把输入流转为字符串
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String InputStream2String(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ( (line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * 检查字符串是否符合email规则
	 * @param str
	 * @return 字符串为Email，返回 true
	 */
	public boolean isEmail(String str) {
		if (str == null) {
			return false;
		}
		str = str.trim();
		/* 假设最短的email为 a@b.c，它的长度为5，不可能再小 */
		if (str.length() < 5) {
			return false;
		}
		String matcher = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		return str.matches(matcher);
	}
	
	/**
	 * 密码明文需要经过这个方法进行简单的加密再传到服务端
	 * @param password
	 * @return
	 */
	public final static String simpleEncode(String password) {
		String md5Str1 = calcMD5(password);
		String tail = md5Str1.substring(20);
		return calcMD5(md5Str1 + tail);
	}
	
	/**
	 * 对数据进行简单的加密，防止本地数据被第三方获取
	 * @param str
	 * @return
	 */
	public final static String localEncryption(String str) {
		if (str == null) {
			return null;
		}
		byte[] strByteArray = str.getBytes(Charset.defaultCharset());
		byte[] mba = new byte[strByteArray.length];
		int mbaIndex = 0;
		for (int i = 0 ; i < strByteArray.length; i += 2, mbaIndex ++) {
			mba[mbaIndex] = (byte) (strByteArray[i] - 64);
		}
		for (int j = 1; j < strByteArray.length; j += 2, mbaIndex ++) {
			mba[mbaIndex] = (byte) (strByteArray[j] - 24);
		}
		return Base64.encodeToString(mba, Base64.DEFAULT);
	}
	
	/**
	 * 对经过localEncryption进行过简单加密的数据进行解密
	 * @param str
	 * @return
	 */
	public final static String localDecryption(String str) {
		if (str == null) {
			return null;
		}
		byte[]  mba = Base64.decode(str, Base64.DEFAULT);
		byte[] strByteArray = new byte[mba.length];
		int mbaIndex = 0;
		for (int i = 0; i < strByteArray.length; i += 2, mbaIndex ++) {
			strByteArray[i] = (byte) (mba[mbaIndex] + 64);
		}
		for (int j = 1; j < strByteArray.length; j += 2, mbaIndex ++) {
			strByteArray[j] = (byte) (mba[mbaIndex] + 24);
		}
		return new String(strByteArray, Charset.defaultCharset());
	}
	
	/**
	 * 计算字符串的 md5 值
	 * @param aStr
	 * @return
	 */
	public final static String calcMD5(String aStr)
	{
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.reset(); 
			md5.update(aStr.getBytes("UTF-8"));// 计算md5
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
		
        byte[] m = md5.digest();// 获取结果

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < m.length; i++)  {  
        	if (Integer.toHexString(0xFF & m[i]).length() == 1)
        		md5StrBuff.append("0").append(Integer.toHexString(0xFF & m[i]));
        	else  
        		md5StrBuff.append(Integer.toHexString(0xFF & m[i]));
        }  

		return md5StrBuff.toString().toUpperCase(Locale.getDefault());
	}
	
	/**
	 * 返回url中的文件名
	 * @param url
	 * @return
	 */
	public final static String getFileName(String url) {
		String[] splitUrl = url.split("/");
		String fileName = splitUrl[splitUrl.length - 1]; //url.substring(url.lastIndexOf('/') + 1);
		String[] split = fileName.split("\\?");
		if (split.length > 1 && fileName.contains("time")) {
			int timeIndex = fileName.indexOf("time=");
			if (timeIndex > -1) {
				int andIndex = fileName.indexOf('&', timeIndex);
				fileName = fileName.subSequence(timeIndex + 5, andIndex > 0 ? andIndex : fileName.length()) + "-" + split[0];
			}
		}
		if (splitUrl.length > 2)
			return splitUrl[splitUrl.length - 2] + '-' + fileName;
		else 
			return fileName;
	}
	
	/**
	 * 判断一个字符串是否为空(对象为空或字符串去掉空格后长度为0即为空)
	 * @param str
	 * @return true 表示字符串为空字符串
	 */
	public final static boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	public final static boolean isEqualsString(String str1, String str2) {
		if (!StringUtil.isEmptyString(str1) && !StringUtil.isEmptyString(str2)) {
			return str1.equals(str2);
		} else // 两个均为空
// 仅有一个为空
			return StringUtil.isEmptyString(str1) && StringUtil.isEmptyString(str2);
	}
	
	/**
	 * 把 error 加工成错误信息
	 * @param resId
	 * @return
	 */
	public final static Spanned getError( int resId) {
		return getError(getString(resId));
	}
	
	/**
	 * 把 error 加工成错误信息
	 * @param error
	 * @return
	 */
	public final static Spanned getError(String error) {
		String format = "<font color=#E10979>%s</font>";
		return Html.fromHtml(String.format(format, error));
	}

	/**
	 * 数字不足2位就在前面加0
	 * @param num
	 * @return
	 */
	public static final String getBit(int num, int bit) {
		StringBuilder _sb = new StringBuilder();
		_sb.append(num);
		while (_sb.length() < bit) {
			_sb.insert(0, 0);
		}
		return _sb.toString();
	}

	/**
	 * 使用某种Mac算法 (HmacSHA1) 进行签名转码，以Base64返回
	 *
	 * @param key
	 *            签名密钥
	 * @param algorithm
	 *            算法名称 (如 HmacSHA1)
	 * @param baseType
	 *            Base64的类型
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String secretString(String data, String key, String algorithm, int baseType) throws NoSuchAlgorithmException {
		byte[] keyByte;
		try {
			keyByte = key.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		// 构造密钥
		SecretKey secretKey = new SecretKeySpec(keyByte, algorithm);
		// 生成Mac算法对象
		Mac mac = Mac.getInstance(algorithm);
		try {
			mac.init(secretKey);
		} catch (InvalidKeyException e) {
			return null;
		}
		byte[] dataByte;
		try {
			dataByte = data.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		// 完成转码
		return Base64.encodeToString(mac.doFinal(dataByte), baseType);
	}

	/**
	 * 产生一个随机的字符串
	 *
	 * @param maxLen
	 * @param minLen
	 * @return
	 */
	public static String getRandomString(int maxLen, int minLen) {
		Random random = new Random();
		int len = maxLen > minLen ? minLen + random.nextInt(maxLen - minLen) : maxLen;
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(random.nextInt(74) + 48);
		}
		return sb.toString();
	}

	public static String getString(int resId) {
		return RKApplication.getInstance().getString(resId);
	}
	
}
