package com.hengdian.henghua.utils;

import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Books;
import com.hengdian.henghua.model.contentDataModel.Rs_LogIn;
import com.hengdian.henghua.model.contentDataModel.Rs_UpdatePwd;

import java.util.logging.Handler;

/**
 * Created by Anderok on 2017/1/19.
 */

public class AsyncDataUtil {
    public static boolean isLogining = false;
    private static final String TAG = "AsyncDataUtil";

    /**
     * 登录
     */
    public static synchronized void login() {

        if (!isNetworkAvailable()) {
            ToastUtil.toastMsgShort("当前网络不可用");
            return;
        }

        final String account = MyApplication.getAccountInfo().getAccount();
        final String pwd = MyApplication.getAccountInfo().getPwd();
        String regexMatch = "^[a-zA-Z].*";

        if (account.isEmpty() || pwd.isEmpty()) {
            if (MyApplication.curActivityFlag != Constant.ViewFlag.LOGIN_ACTIVITY) {
                MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.OPEN_LOGIN_ACTIVITY);
            }
            ToastUtil.toastMsgShort("用户名或密码不能为空！");
            return;
        }else if(account.length()<6){
            ToastUtil.toastMsgShort("用户名长度不少于6位！");
            return;
        }else if(pwd.length()<6){
            ToastUtil.toastMsgShort("输入密码长度必须不少于6位");
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                isLogining = true;

                MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.LOGINING);

                Rs_LogIn rsLogin = null;
                try {
                    rsLogin = DataRequestUtil.logIn(account, EnDecodeUtil.MD5InLowerCase(pwd));
                } catch (Exception e) {
                    LogUtil.e(TAG, "logIn error", e);
                }

                isLogining = false;

                MyApplication.appHandler.obtainMessage(Constant.HandlerFlag.LOGIN_ON_RESULT, rsLogin).sendToTarget();


            }
        }).start();
    }

    /**
     * 修改密码
     */
    public static void updatePwd() {
        if (!isNetworkAvailable())
            return;

        final String userName = MyApplication.getAccountInfo().getAccount();
        String oldPwd = MyApplication.getPwdUpdateInfo().getOldPwd();
        String newPwd1 = MyApplication.getPwdUpdateInfo().getNewPwd1();
        String newPwd2 = MyApplication.getPwdUpdateInfo().getNewPwd2();

        if (oldPwd.isEmpty() || newPwd1.isEmpty() || newPwd2.isEmpty()) {
            ToastUtil.toastMsgShort("新旧密码不能为空！");
            return;
        }

        if(newPwd1.length()<6 || newPwd2.length()<6){
            ToastUtil.toastMsgShort("新密码长度必须不少于6位!");
            return;
        }

        if (newPwd1.equals(oldPwd)) {
            ToastUtil.toastMsgShort("新旧密码相同，忽略修改！");
            return;
        }

        if (!newPwd1.equals(newPwd2)) {
            ToastUtil.toastMsgShort("新密码两次输入不相同，请重新输入!");
            return;
        }

        if (userName.isEmpty()) {
            ToastUtil.toastMsgShort("请先登录！");
            return;
        }

        final String oldPwd2 = EnDecodeUtil.MD5InLowerCase(oldPwd);
        final String newPwd12 = EnDecodeUtil.MD5InLowerCase(newPwd1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Rs_UpdatePwd rsUpdatePwd = null;
                try {
                    rsUpdatePwd = DataRequestUtil.updatePassWord(userName, oldPwd2, newPwd12);
                } catch (Exception e) {
                    LogUtil.e("AsyncDataUtil", "修改密码异常");
                    e.printStackTrace();
                }
                MyApplication.appHandler.obtainMessage(Constant.HandlerFlag.UPDATE_PWD_ON_RESULT, rsUpdatePwd).sendToTarget();
            }
        }).start();
    }


    public static boolean isDataRequestAvailable() {
        return isAlreadyLogin() && isNetworkAvailable();
    }

    public static boolean isAlreadyLogin() {
        if (MyApplication.getAccountInfo().getTokenID().isEmpty() || !MyApplication.isLogInSuccess) {
            //MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.NOTICE_TO_LOGIN);
            ToastUtil.toastMsgShort("请先登录！");
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable() {

        if (!NetUtil.isNetworkActive(MyApplication.getAppContext())) {
            ToastUtil.toastMsgShort("请先连接网络！");
            return false;
        }

        return true;
    }

}