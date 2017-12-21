package com.hengdian.henghua.androidUtil;

import android.os.Handler;
import android.os.Message;

import com.hengdian.henghua.model.AccountInfo;
import com.hengdian.henghua.model.contentDataModel.Rs_LogIn;
import com.hengdian.henghua.model.contentDataModel.Rs_UpdatePwd;
import com.hengdian.henghua.utils.AsyncDataUtil;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataUtil;
import com.hengdian.henghua.utils.ZUtil;

/**
 * Created by Anderok on 2017/1/15.
 */


public class AppHandler extends Handler {

    private static MyApplication mApp;


    public AppHandler(MyApplication mApp) {
        this.mApp = mApp;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {

            case Constant.HandlerFlag.LOGIN_IN:
                if (AsyncDataUtil.isLogining) {
                    ToastUtil.toastMsgShort("登录中，请稍后...");
                    return;
                }

                AsyncDataUtil.login();
                break;

            case Constant.HandlerFlag.LOGINING:
                transSendEmptyMsg(Constant.HandlerFlag.LOGINING);
                break;

            case Constant.HandlerFlag.LOGIN_ON_RESULT:
                Object obj = msg.obj;

                //保存登录信息
                if (obj != null) {
                    Rs_LogIn rsLogin = (Rs_LogIn) obj;
                    AccountInfo actInfo = mApp.getAccountInfo();

                    actInfo.setFirstLogin(false); //设置密码不再显示
                    actInfo.setName(rsLogin.getRealName());
                    actInfo.setTokenID(rsLogin.getToken_id());

                    DataUtil.saveAccountInfo(actInfo);

                    //除了在主页面登录成功不提示，其他都要提示,登录失败都要提示

                    if (rsLogin.getStatus() != 200 || !(rsLogin.getStatus() == 200 && mApp.curActivityFlag == Constant.ViewFlag.MAIN_ACTIVITY)) {
                        ToastUtil.toastMsgShort(rsLogin.getStatusMsg());
                    }

                    if (rsLogin.getStatus() == 200) {
                        mApp.isLogInSuccess = true;

                        transSendEmptyMsg(Constant.HandlerFlag.LOGIN_SUCCESS);
                        //如果侧边栏没关闭，则关闭
                        if (!mApp.isLeftMenuClosed) {
                            mApp.mainActHandler.sendEmptyMessage(Constant.HandlerFlag.LOGIN_SUCCESS);
                        }
                    } else {
                        mApp.isLogInSuccess = false;
                        notifyToOenLoginActivity();
                    }
                } else {
                    mApp.isLogInSuccess = false;
                    ToastUtil.toastMsgShort("登录异常！");
                    notifyToOenLoginActivity();
                }

                if (mApp.curActivityFlag == Constant.ViewFlag.LOGIN_ACTIVITY) {
                    transSendEmptyMsg(Constant.HandlerFlag.LOGIN_ON_RESULT);
                }
                obj = null;
                break;

            case Constant.HandlerFlag.CLOSE_LEFT_MENU:

                mApp.mainActHandler.sendEmptyMessage(Constant.HandlerFlag.CLOSE_LEFT_MENU);

                break;
            case Constant.HandlerFlag.UPDATE_PWD_ON_RESULT:

                Object obj1 = msg.obj;
                if (obj1 != null) {
                    Rs_UpdatePwd rsUpdatePwd = (Rs_UpdatePwd) obj1;
                    if (rsUpdatePwd.getStatus() == 200) {
                        mApp.getTransmitHandler().sendEmptyMessage(Constant.HandlerFlag.UPDATE_PWD_SUCCESS);
                    } else {
                        ToastUtil.toastMsgShort("修改失败：" + rsUpdatePwd.getStatusMsg());
                    }
                } else {
                    ToastUtil.toastMsgShort("修改失败：未知异常");
                }
                obj1 = null;
                break;

            case Constant.HandlerFlag.NETWORK_AVAILABLE:
                AccountInfo acc = MyApplication.getAccountInfo();
                if (!mApp.isLogInSuccess && mApp.isLaunched && acc.canAutoLogIn()) {

                       AsyncDataUtil.login();

                }
                break;
            case Constant.HandlerFlag.NETWORK_INVALID:
                break;
            default:
                //TODO
        }
    }


    private void notifyToOenLoginActivity() {
        //如果不是登录界面，通知打开登录窗口
        if (mApp.curActivityFlag == Constant.ViewFlag.MAIN_ACTIVITY) {
            mApp.mainActHandler.sendEmptyMessage(Constant.HandlerFlag.OPEN_LOGIN_ACTIVITY);
        } else if (mApp.curActivityFlag != Constant.ViewFlag.LOGIN_ACTIVITY) {
            transSendEmptyMsg(Constant.HandlerFlag.OPEN_LOGIN_ACTIVITY);
        }
    }

    /**
     * app临时持有handler发送通信标记
     *
     * @param what
     */
    private void transSendEmptyMsg(int what){
        if(mApp.transmitHandler==null){
            return;
        }

        mApp.transmitHandler.sendEmptyMessage(what);
    }
}


