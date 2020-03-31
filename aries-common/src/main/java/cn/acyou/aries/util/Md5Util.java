package cn.acyou.aries.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5算法
 */
public class Md5Util {

	public static String getMD5(String info) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++) {
				if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				} else {
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}
			return strBuf.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static String String2Md5(String plainText) {
		StringBuffer buf = null;
		String bufInfo = "";
		try {
			if (null != plainText) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(plainText.getBytes());
				byte b[] = md.digest();
				int i;
				buf = new StringBuffer("");
				for (int offset = 0; offset < b.length; offset++) {
					i = b[offset];
					if (i < 0) {
						i += 256;
					}
					if (i < 16) {
						buf.append("0");
					}
					buf.append(Integer.toHexString(i));
				}
			}
			if (null != buf) {
				bufInfo = buf.toString().substring(8, 24);// 16位的加密
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return bufInfo;
	}

	public static String String2Md52(String password) {
		return String2Md5((String2Md5(password) + "youfang"));
	}

	public static String String2Md5for32(String plainText) {
		StringBuffer buf = null;
		String bufInfo = "";
		try {
			if (null != plainText) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(plainText.getBytes());
				byte b[] = md.digest();
				int i;
				buf = new StringBuffer("");
				for (int offset = 0; offset < b.length; offset++) {
					i = b[offset];
					if (i < 0) {
						i += 256;
					}
					if (i < 16) {
						buf.append("0");
					}
					buf.append(Integer.toHexString(i));
				}
			}
			if (null != buf) {
				bufInfo = buf.toString().toUpperCase();// 32位的加密
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return bufInfo;
	}


	public static void main(String[] args) {
		System.out.println(String2Md5for32("123456789"));
	}
}
