package com.hengdian.henghua.androidUtil;

import android.os.DeadSystemException;
import android.util.Log;

import java.net.UnknownHostException;

/**
 * Created by admin on 2017-01-22.
 */

public class LogUtil {
    private static final int LEVEL_ERR = 0x0;
    private static final int LEVEL_WARN = 0x1;
    private static final int LEVEL_INFO = 0x2;
    private static final int LEVEL_DEBUG = 0x3;
    private static final int LEVEL_VERBOSE = 0x4;

    public static boolean hideLog = false;//是否隐藏日志
    public static int lowestLevel = LEVEL_DEBUG;//最低输出的级别
    public static boolean logAtCommonLevel = false;//是否统一输出级别
    public static int commonLevel = LEVEL_INFO;//统一输出的级别
    public static boolean addErrMsgTag = false;//是否在错误日志中追加tr.getMessage()信息

    /**
     * 错误日志
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    /**
     * 错误日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (hideLog) {
            return;
        }

        msg = msgCombine(msg, tr);

        if (logAtCommonLevel) {
            logAtCommonLevel(commonLevel, tag, msg, tr);
        } else {
            Log.e(tag, msg, tr);
        }
    }


    /**
     * 警告日志
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    /**
     * 警告日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (hideLog || lowestLevel < LEVEL_WARN) {
            return;
        }

        msg = msgCombine(msg, tr);

        if (logAtCommonLevel) {
            logAtCommonLevel(commonLevel, tag, msg, tr);
        } else {
            Log.w(tag, msg, tr);
        }
    }

    /**
     * 正常日志
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }


    /**
     * 正常日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (hideLog || lowestLevel < LEVEL_INFO) {
            return;
        }

        msg = msgCombine(msg, tr);

        if (logAtCommonLevel) {
            logAtCommonLevel(commonLevel, tag, msg, tr);
        } else {
            Log.i(tag, msg, tr);
        }
    }


    /**
     * 调试日志
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }


    /**
     * 调试日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (hideLog || lowestLevel < LEVEL_DEBUG) {
            return;
        }

        msg = msgCombine(msg, tr);

        if (logAtCommonLevel) {
            logAtCommonLevel(commonLevel, tag, msg, tr);
        } else {
            Log.d(tag, msg, tr);
        }
    }

    /**
     * 冗余日志
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }


    /**
     * 冗余日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (hideLog || lowestLevel < LEVEL_VERBOSE) {
            return;
        }

        msg = msgCombine(msg, tr);

        if (logAtCommonLevel) {
            logAtCommonLevel(commonLevel, tag, msg, tr);
        } else {
            Log.v(tag, msg, tr);
        }
    }

    /**
     * 统一日志输出
     *
     * @param level
     * @param tag
     * @param msg
     * @param tr
     */
    private static void logAtCommonLevel(int level, String tag, String msg, Throwable tr) {
        switch (level) {
            case LEVEL_ERR:
                Log.e(tag, msg, tr);
                break;

            case LEVEL_WARN:
                Log.w(tag, msg, tr);
                break;
            case LEVEL_INFO:
                Log.i(tag, msg, tr);
                break;

            case LEVEL_DEBUG:
                Log.d(tag, msg, tr);
                break;
            case LEVEL_VERBOSE:
                Log.v(tag, msg, tr);
                break;

        }
    }

    /**
     * 在msg中追加tr.getMessage()信息msg + ", [" + tr.getMessage() + "]";
     *
     * @param msg
     * @param tr
     * @return
     */
    private static String msgCombine(String msg, Throwable tr) {
        if (addErrMsgTag && tr != null) {
            msg = msg + ", [" + tr.getMessage() + "]";
        }

        return msg;
    }


    /**
     * 获取异常消息
     *
     * @param tr
     * @return
     */
    public static String msg(Throwable tr, String... msg) {

        StringBuilder sbd = new StringBuilder();
        if (msg != null) {
            for (String s : msg) {
                sbd.append(s);
            }

            if (tr != null) {
                sbd.append(", details: ");
            }
        }

        if (tr != null)
            for (StackTraceElement ele : tr.getStackTrace()) {
                sbd.append("\n\tat " + ele);
            }

        return sbd.toString();
    }
}
