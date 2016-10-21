package com.wx.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.widget.Toast;

/*
 * @atyun_wx
 */
public class WNHexChange {

	// 判断用户是哪种类型泵
	public static String judgePumpStyle(String head, String style,
			String serialNumber) {
		String deviceName = "";
		if ("infusion_pump".equals(style)) {
			if (head.contains("inZB")) {
				deviceName = "inZB"
						+ serialNumber.substring(0, serialNumber.length());
			} else if (head.contains("inZA")) {
				deviceName = "inZA"
						+ serialNumber.substring(0, serialNumber.length());
			}
		}
		if ("insulin_pump".equals(style)) {
			deviceName = "inIB"
					+ serialNumber.substring(0, serialNumber.length());
		}
		if ("syringe_pump".equals(style)) {
			deviceName = "inTP"
					+ serialNumber.substring(0, serialNumber.length());
		}
		return deviceName;
	}

	// 获得泵返回信息
	public static List<String> getPumpData(String data) {

		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < data.length(); i = i + 2) {
			strings.add(data.substring(i, i + 2).toString());
		}
		return strings;
	}

	// 补充前面一位的0
	public static String addZero(String str) {
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

	// 传进去两个时间，返回相差时间
	public static double compareTime(String time1, String time2) {
		// String time1 = "2016-04-06 00:00:00";
		// String time2 = "2016-04-07 01:30:00";
		double hour = 0;
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = sd1.parse(time1);
			Date date2 = sd1.parse(time2);
			long s1 = date1.getTime() / 1000;
			long s2 = date2.getTime() / 1000;
			hour = (s2 - s1) / 3600.0;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return hour;
	}

	// 传进去两个时间，返回相差时间
	public static long compareTimeByMinite(String time1, String time2) {
		long minite = 0;
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date1 = sd1.parse(time1);
			Date date2 = sd1.parse(time2);
			long s1 = date1.getTime() / 1000;
			long s2 = date2.getTime() / 1000;
			minite = (long) ((s2 - s1) / 60.0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return minite;
	}

	// 十六进制转String
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	// String 转 int
	public static int stringToInt(String str) {
		int temp = 0;
		try {
			temp = Integer.parseInt(str);
		} catch (Exception e) {
			temp = 0;
		}
		return temp;
	}

	// 去掉字符中间的“,”
	public static List<String> clearComma(String data) {
		List<String> resultList = new ArrayList<String>();
		String[] array = new String[] {};
		array = data.split(",");
		for (int i = 0; i < array.length; i++) {
			resultList.add(array[i]);
		}
		return resultList;
	}

	// 16进制转10进制
	public static String intToString(String strHex) {
		String result = null;
		int i = HexToInt(strHex);
		result = i + "";
		return result;

	}

	static int sum;

	// 检验码最后两位
	public static String finalCode(String value) {
		if (value.length() % 2 != 0)
			return null;
		for (int i = 0; i < value.length(); i = i + 2) {
			sum = sum
					+ Integer.parseInt(intToString(value.substring(i, i + 2)));
		}
		String sumStr = Integer.toHexString(sum).toString();
		return sumStr.substring(sumStr.length() - 2, sumStr.length());
	}

	public static int HexToInt(String strHex) {
		int nResult = 0;
		if (!IsHex(strHex))
			return nResult;
		String str = strHex.toUpperCase();
		if (str.length() > 2) {
			if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
				str = str.substring(2);
			}
		}
		int nLen = str.length();
		for (int i = 0; i < nLen; ++i) {
			char ch = str.charAt(nLen - i - 1);
			try {
				nResult += (GetHex(ch) * GetPower(16, i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nResult;
	}

	// CGM传入编号获得历史数据
	public static String inputCodeToGetData(int code) {
		String codeHex = Integer.toHexString(code).toString();
		switch (codeHex.length()) {
		case 1:
			codeHex = "000" + codeHex;
			break;
		case 2:
			codeHex = "00" + codeHex;
			break;
		case 3:
			codeHex = "0" + codeHex;
			break;
		default:
			codeHex = "0000";
			break;
		}
		return "55" + codeHex + finalCode(codeHex);
	}

	// 判断是否是16进制数
	public static boolean IsHex(String strHex) {
		int i = 0;
		if (strHex.length() > 2) {
			if (strHex.charAt(0) == '0'
					&& (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
				i = 2;
			}
		}
		for (; i < strHex.length(); ++i) {
			char ch = strHex.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F')
					|| (ch >= 'a' && ch <= 'f'))
				continue;
			return false;
		}
		return true;
	}

	// 计算16进制对应的数值
	public static int GetHex(char ch) throws Exception {
		if (ch >= '0' && ch <= '9')
			return (int) (ch - '0');
		if (ch >= 'a' && ch <= 'f')
			return (int) (ch - 'a' + 10);
		if (ch >= 'A' && ch <= 'F')
			return (int) (ch - 'A' + 10);
		throw new Exception("error param");
	}

	// 计算幂
	public static int GetPower(int nValue, int nCount) throws Exception {
		if (nCount < 0)
			throw new Exception("nCount can't small than 1!");
		if (nCount == 0)
			return 1;
		int nSum = 1;
		for (int i = 0; i < nCount; ++i) {
			nSum = nSum * nValue;
		}
		return nSum;
	}
}
