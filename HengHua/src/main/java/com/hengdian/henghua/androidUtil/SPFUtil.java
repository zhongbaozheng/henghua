package com.hengdian.henghua.androidUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Anderok on 2017/1/16.
 */

public class SPFUtil {

    private SharedPreferences spf;

    public SPFUtil(Context context, String spfFileName, int contextMode) {
        spf = context.getSharedPreferences(spfFileName, contextMode);
    }

    /**
     * 获取某一sharedPreferences文件中的所有键值对
     *
     * @param context
     * @param sharedPreferencesFileName
     * @param mode
     * @return
     */
    public static Map<String, String> getAll(Context context, String sharedPreferencesFileName, int mode) {
        SharedPreferences sp = context.getSharedPreferences(sharedPreferencesFileName, mode);
        return (Map<String, String>) sp.getAll();
    }

    /**
     * 获取boolean型的值
     *
     * @param key      键名
     * @param defValue 获取不到时，给定的默认的值
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return spf.getBoolean(key, defValue);
    }

    /**
     * 保存boolean变量
     *
     * @param key   键
     * @param value 值
     */
    public SPFUtil setBoolean(String key, boolean value) {
        spf.edit().putBoolean(key, value).commit();
        return this;
    }


    /**
     * 获取字符串型变量的值
     *
     * @param key
     * @param defValue 获取不到时，给定的默认的值
     * @return
     */
    public String getString(String key, String defValue) {
        return spf.getString(key, defValue);
    }

    /**
     * 保存字符串变量
     *
     * @param key   键
     * @param value 值
     */
    public SPFUtil setString(String key, String value) {
        spf.edit().putString(key, value).commit();
        return this;
    }

    /**
     * 获取int变量的值
     *
     * @param key
     * @param defValue 获取不到时，给定的默认的值
     * @return
     */
    public int getInt(String key, int defValue) {
        return spf.getInt(key, defValue);
    }

    /**
     * 保存int变量
     *
     * @param key   键
     * @param value 值
     */
    public SPFUtil setInt(String key, int value) {
        spf.edit().putInt(key, value).commit();
        return this;
    }

    /**
     * 获取float型变量的值
     *
     * @param key      键
     * @param defValue 获取不到时，给定的默认的值
     * @return
     */
    public float getFloat(String key, int defValue) {
        return spf.getFloat(key, defValue);
    }

    /**
     * 保存float变量
     *
     * @param key   键
     * @param value 值
     */
    public SharedPreferences setFloat(String key, int value) {
        spf.edit().putFloat(key, value).commit();
        return spf;
    }


    /**
     * 获取整个应用范围有效的SharedPreferences
     *
     * @param spName      SharedPreferences文件名
     * @param contextMode 权限模式(Context.MODE_...)
     * @return
     */
    public static SharedPreferences getSPOfApp(String spName, int contextMode) {
        return MyApplication.getAppContext().getSharedPreferences(spName, contextMode);
    }

    /**
     * 获取context的SharedPreferences
     *
     * @param spName      SharedPreferences文件名
     * @param contextMode 权限模式(Context.MODE_...)
     * @return
     */
    public static SharedPreferences getSPOfContext(Activity context, String spName, int contextMode) {
        return context.getSharedPreferences(spName, contextMode);
    }


    /**
     * 获取application的SharedPreferences的Editor
     *
     * @param spName      SharedPreferences文件名
     * @param contextMode 权限模式(Context.MODE_...)
     * @return
     */
    public static SharedPreferences.Editor getAPPSPEditor(String spName, int contextMode) {
        return getSPOfApp(spName, contextMode).edit();
    }

    /**
     * 获取Context的SharedPreferences的Editor
     *
     * @param spName      SharedPreferences文件名
     * @param contextMode 权限模式(Context.MODE_...)
     * @return
     */
    public static SharedPreferences.Editor getSPEditor(Context context, String spName, int contextMode) {
        return context.getSharedPreferences(spName, contextMode).edit();
    }

    public static void setBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static void setString(SharedPreferences sharedPreferences, String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static void setInt(SharedPreferences sharedPreferences, String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static void setFloat(SharedPreferences sharedPreferences, String key, int value) {
        sharedPreferences.edit().putFloat(key, value).commit();
    }


}
