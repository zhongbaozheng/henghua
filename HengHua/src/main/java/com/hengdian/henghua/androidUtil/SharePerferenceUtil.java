package com.hengdian.henghua.androidUtil;

/**
 * Created by micro on 2017/12/25.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharePerferenceUtil {

    /**
     * Created by Jiangwen on 2016/10/22.
     * Email: hjiagnwen1@163.com
     */
        private static Context mContext = null;

        public static void register(Context context) {
            mContext = context;
        }

        private static final String TAG = SharePerferenceUtil.class.getSimpleName();

        public static final String CONFIG_HISTORY_SEARCH = "CONFIG_HISTORY_SEARCH";

        /**
         * 获取Preference设置
         */
        public static SharedPreferences getSharedPreferences() {
            if (mContext == null) {
                Log.e(TAG, "配置类没有注册上AppContext(下文环境)");
            }
            return PreferenceManager.getDefaultSharedPreferences(mContext);
        }

        /**
         * 写入配置信息，需要最后面进行 commit()
         *
         * @param key
         * @param value
         * @return
         */
        public static void putString(String key, String value) {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, value);
            editor.commit();
        }

        /**
         * 写入配置信息，需要最后面进行 commit()
         *
         * @param key
         * @param value
         * @return
         */
        public static void putInt(String key, int value) {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(key, value);
            editor.commit();
        }

        /**
         * 写入配置信息，需要最后面进行 commit()
         *
         * @param key
         * @param value
         * @return
         */
        public static void putLong(String key, long value) {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(key, value);
            editor.commit();
        }

        /**
         * 写入配置信息，需要最后面进行 commit()
         *
         * @param key
         * @param value
         * @return
         */
        public static void putBoolean(String key, boolean value) {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }

        /**
         * 读取配置信息
         *
         * @param key
         * @return
         */
        public static boolean getBoolean(String key, boolean def) {
            return getSharedPreferences().getBoolean(key, def);
        }

        /**
         * 读取配置信息
         *
         * @param key
         * @return
         */
        public static String getString(String key) {
            return getSharedPreferences().getString(key, null);
        }

        /**
         * 读取配置信息
         *
         * @param key
         * @return
         */
        public static int getInt(String key) {
            return getSharedPreferences().getInt(key, 0);
        }

        /**
         * 读取配置信息
         *
         * @param key
         * @return
         */
        public static long getLong(String key) {
            return getSharedPreferences().getLong(key, 0L);
        }

        /**
         * 删除配置信息，可以同时删除多个
         *
         * @param keys
         */
        public static void remove(String... keys) {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.commit();
        }

        /**
         * 清除所有配置文件
         */
        public static void clearAll() {
            SharedPreferences sharedPref = getSharedPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
        }

        public static String getHistorySearch() {
            return getString(CONFIG_HISTORY_SEARCH);
        }

        public static void putHistorySearch(String value) {
            putString(CONFIG_HISTORY_SEARCH, value);
        }
}
