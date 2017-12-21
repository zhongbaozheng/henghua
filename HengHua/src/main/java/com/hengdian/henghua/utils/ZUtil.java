package com.hengdian.henghua.utils;

import com.hengdian.henghua.androidUtil.LogUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * 小工具类
 * 
 * @author ander
 *
 */
public class ZUtil {

	/**
	 * 判断字符串是否没有东西
	 * 
	 * @param str
	 * @return str为null、"null"(忽略大小写)、""、" "均返回true
	 */
	public static boolean isNothing(String str) {
		if (str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str.trim()))
			return true;
		else
			return false;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return str为null、""、" "等均返回true
	 */
	public static boolean isEmpty(String str) {

		if (str == null || str.trim().isEmpty())
			return true;

		return false;
	}

	/**
	 * 判断是否为NUll或者忽略大小写的NUll字符串
	 * 
	 * @param str
	 * @return str为null,"null"(忽略大小写)均返回true
	 */
	public static boolean isNull(String str) {

		if (str == null || "null".equalsIgnoreCase(str.trim()))
			return true;

		return false;
	}

	/**
	 * 去除null,忽略大小写
	 * 
	 * @param str
	 * @return str为null、"null"、"NULL"、"Null"等均返回""
	 */
	public static String trimNull(String str) {

		if (isNull(str))
			return "";

		return str;
	}

	/**
	 * String数组拼接成字符串
	 * 
	 * @param strArr
	 * @param separator
	 *            元素分隔符，可用","、" "等任意字符，null或""即没有分隔符
	 * @return
	 */
	public static String toString(String[] strArr, String separator) {
		if (strArr == null)
			return "";

		if (separator == null) {
			separator = "";
		}

		StringBuilder sbd = new StringBuilder();
		int length = strArr.length;
		for (int i = 0; i < length; i++) {
			sbd.append(strArr[i]);

			if (i < length - 1)
				sbd.append(separator);
		}

		return sbd.toString();
	}

	/**
	 * 判断字符串长度是否符合要求
	 * 
	 * @param str
	 * @param minLength
	 *            最小长度，负数表示不设限制
	 * @param maxLength
	 *            最大长度，负数表示不设限制
	 * @return 长度符合返回1，过短返回-2，过长返回-1,输入异常返回0(最大值，最小值数值均有效但相反)
	 */
	public static int isLengthFit(String str, int minLength, int maxLength) {
		if (minLength >= 0 && maxLength >= 0 && minLength > maxLength)
			return 0;

		if (minLength < 0 && maxLength < 0)
			return 1;

		if (str == null) {
			if (minLength < 0)
				return 1;
			else
				return -2;
		}

		int strLength = str.length();

		if (minLength < 0) {
			if (strLength <= maxLength)
				return 1;
			else
				return -1;
		}

		if (maxLength < 0) {
			if (strLength >= minLength)
				return 1;
			else
				return -2;
		}

		if (str.length() >= minLength && str.length() <= maxLength)
			return 1;

		else if (str.length() < minLength)
			return -2;

		else
			return -1;

	}

	/**
	 * 拼接参数名值对。 如,keyValConnector="=",paramDivider="&"则name=zhang&age=20
	 * 
	 * @param paramMap
	 * @param keyValConnector
	 *            键值连接符
	 * @param paramDivider
	 *            参数分隔符
	 * @param charset
	 *            null则不编码
	 * @return
	 * @throws IOException
	 */
	public static String combine(Map<String, String> paramMap,
			String keyValConnector, String paramDivider, String charset)
			throws IOException {

		if (paramMap == null || paramMap.size() == 0) {
			return "";
		}

		StringBuilder sbd = new StringBuilder();

		// 推荐，尤其是容量大时
		// for (Map.Entry<String, String> entry : paramMap.entrySet()) {
		// sbd.append(URLEncoder.encode(entry.getKey(), charset));
		// sbd.append(keyValConnector);
		// sbd.append(URLEncoder.encode(entry.getValue(), charset));
		// sbd.append("&");
		//
		// }

		// jdk低版本通用
		Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			sbd.append(paramDivider);
			if (charset == null)
				sbd.append(entry.getKey());
			else
				sbd.append(URLEncoder.encode(entry.getKey(), charset));

			sbd.append(keyValConnector);

			if (charset == null)
				sbd.append(entry.getValue());
			else
				sbd.append(URLEncoder.encode(entry.getValue(), charset));



		}
		LogUtil.i("  pa ","参数:" + sbd.toString());
//		sbd.deleteCharAt(sbd.length() );

		return sbd.toString();
	}

	/**
	 * 获取map内值对应的Key
	 *
	 * @param map
	 *            要查找的map
	 * @param value
	 *            值
	 * @return 返回值对应的键的list集合
	 */
	public static List<Object> getKey(Map<?, ?> map, Object value) {
		if (map == null)
			return null;

		List<Object> keys = new ArrayList<Object>();

		for (Entry<?, ?> entry : map.entrySet()) {
			if (entry.getValue() == null && value == null)
				keys.add(entry.getKey());
			else if (entry.getValue() != null && entry.getValue().equals(value))
				keys.add(entry.getKey());
		}

		return keys;
	}

}
