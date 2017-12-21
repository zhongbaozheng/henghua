package com.hengdian.henghua.androidUtil;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import com.hengdian.henghua.model.AccountInfo;
import com.hengdian.henghua.model.PwdUpdateInfo;
import com.hengdian.henghua.utils.Constant;


/**
 * Created by Anderok on 2017/1/13.
 */
public class MyApplication extends Application {
    /**
     * 全局上下文环境
     */
    private static Context mContext;

    /**
     * App主线程
     */
    private static Thread mAppThread;

    /**
     * App主线程id
     */
    private static int mAppThreadId;

    /**
     * 全局的handler
     */
    public static Handler appHandler;

    /**
     * 主activity的handler
     */
    public Handler mainActHandler;

    /**
     * 传递的Handler
     */
    public Handler transmitHandler;

    /**
     * 应用是否已开启
     */
    public boolean isLaunched = false;

    /**
     * 账号信息
     */
    private static AccountInfo accountInfo;
    /**
     * 更改密码账号信息
     */
    private static PwdUpdateInfo pwdUpdateInfo;

    /**
     * 打开应用后是否登录过
     */
    public static boolean isLogInSuccess = false;

    /**
     * 上个界面标志
     */
    public static int preActivityFlag = Constant.ViewFlag.SPLASH_ACTIVITY;

    /**
     * 当前界面标志
     */
    public static int curActivityFlag = Constant.ViewFlag.SPLASH_ACTIVITY;

    /**
     * 左侧菜单是否已经关闭
     */
    public static boolean isLeftMenuClosed = true;


    //在此处去构建开发过程中需要用到的常见对象,并且提供方法用于获取
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        x.Ext.init(this);//Xutils初始化
        // x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能
        // 信任所有https域名
        /*HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });*/

        appHandler = new AppHandler(this);

        //MyApplication运行在主线程中,所以拿当前线程对象即可
        mAppThread = Thread.currentThread();

        //主线程id,就是MyApplication线程(主线程)id,获取当前线程id
        mAppThreadId = android.os.Process.myTid();
        accountInfo = new AccountInfo();
        pwdUpdateInfo = new PwdUpdateInfo();

    }

    public static Context getAppContext() {
        return mContext;
    }

    /**
     * 获取APP的Handler
     *
     * @return
     */
    public static Handler getAppHandler() {
        return appHandler;
    }

    /**
     * 获取主Activity的handler
     *
     * @return
     */
    public Handler getMainActHandler() {
        if (mainActHandler == null)
            mainActHandler = new Handler();
        return mainActHandler;
    }

    /**
     * 设置主Activity的handler
     *
     * @param mActivityHandler
     */
    public void setMainActHandler(Handler mActivityHandler, int curActivityFlag) {
        mainActHandler = mActivityHandler;
        this.curActivityFlag = curActivityFlag;
    }


    /**
     * 获取传递的Handler
     */
    public Handler getTransmitHandler() {
        if (transmitHandler == null)
            transmitHandler = new Handler();
        return transmitHandler;
    }


    /**
     * 设置要传递的Handler
     *
     * @param trsHandler
     */
    public void setTransmitHandler(Handler trsHandler) {
        transmitHandler = trsHandler;
    }

    /**
     * 获取App主线程
     *
     * @return
     */
    public static Thread getAppThread() {
        return mAppThread;
    }

    /**
     * 获取App主线程ID
     *
     * @return
     */
    public static int getAppThreadId() {
        return mAppThreadId;
    }

    /**
     * 更新账号信息对象
     *
     * @param info
     */
    public static void setAccountInfo(AccountInfo info) {
        accountInfo = accountInfo;
    }

    /**
     * 获取账号信息对象
     *
     * @return
     */
    public static AccountInfo getAccountInfo() {
        return accountInfo;
    }

    /**
     * 获取密码修改信息缓存对象
     *
     * @return
     */
    public static PwdUpdateInfo getPwdUpdateInfo() {
        if (pwdUpdateInfo == null)
            return new PwdUpdateInfo();

        return pwdUpdateInfo;
    }

    /**
     * 保存密码修改信息缓存对象
     *
     * @param info
     */
    public static void setPwdUpdateInfo(PwdUpdateInfo info) {
        pwdUpdateInfo = info;
    }


    /**
     * 在主线程执行延时任务的操作
     *
     * @param runnableTask 延时任务
     * @param delayedTime  延时时间
     */

    public static void postDelayed(Runnable runnableTask, int delayedTime) {
        MyApplication.appHandler.postDelayed(runnableTask, delayedTime);
    }

    /**
     * 通过handler移除主线程指定的任务
     *
     * @param runnableTask 需要移除的任务对象
     */
    public static void removeCallBack(Runnable runnableTask) {
        MyApplication.appHandler.removeCallbacks(runnableTask);
    }


}
