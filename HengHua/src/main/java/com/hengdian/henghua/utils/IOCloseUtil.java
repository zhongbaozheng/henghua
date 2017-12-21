package com.hengdian.henghua.utils;


import com.hengdian.henghua.androidUtil.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOCloseUtil {
	private static final String TAG = "IOCloseUtil";
	
	/**
	 * 关闭IO Writer
	 * 
	 * @param writer
	 * @return
	 */
	public static boolean close(Writer writer) {
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
			return true;
		} catch (IOException e) {
			LogUtil.e(TAG,"close Writer failed",e);
			return false;
		}
	}

	/**
	 * 关闭IO Reader
	 * 
	 * @param reader
	 * @return
	 */
	public static boolean close(Reader reader) {
		try {
			if (reader != null)
				reader.close();
			return true;
		} catch (IOException e) {
			LogUtil.e(TAG,"close Reader failed",e);
			return false;
		}
	}

	/**
	 * 关闭IO OutputStream
	 * 
	 * @param os
	 * @return
	 */
	public static boolean close(OutputStream os) {
		try {
			if (os != null) {
				os.flush();
				os.close();
			}
			return true;
		} catch (IOException e) {
			LogUtil.e(TAG,"close OutputStream failed",e);
			return false;
		}
	}

	/**
	 * 关闭IO InputStream
	 * 
	 * @param is
	 * @return
	 */
	public static boolean close(InputStream is) {
		try {
			if (is != null)
				is.close();
			return true;
		} catch (IOException e) {
			LogUtil.e(TAG,"close InputStream failed",e);
			return false;
		}
	}
}

//Jdk提供的流继承了四大类：
//InputStream(字节输入流)，
//OutputStream（字节输出流），
//Reader（字符输入流），
//Writer（字符输出流）。

