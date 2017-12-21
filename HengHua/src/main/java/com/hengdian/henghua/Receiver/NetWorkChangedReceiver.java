package com.hengdian.henghua.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.utils.Constant;

/**
 * 注册一个广播接收者，接收网络连接状态改变广播
 * <p>
 * <p>
 * <p>
 * Created by Anderok on 2017/2/12.
 */

public class NetWorkChangedReceiver extends BroadcastReceiver {
    /*
     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     <receiver
     android:name="com.blackboard.androidtest.receiver.ConnectionChangeReceiver"
     android:label="NetworkConnection">
     <intent-filter>
     <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
     </intent-filter>
     </receiver>
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!NetUtil.isNetworkAvailable(context)) {
            ToastUtil.toastMsgShort("没有可用的网络");
            return;
        }
//        else {
//            ToastUtil.toastMsgShort("当前网络类型：" + NetUtil.getNetWorkType(context));
//        }

        if (NetUtil.isNetworkConnected(context)) {
            MyApplication.getAppHandler().sendEmptyMessage(Constant.HandlerFlag.NETWORK_AVAILABLE);
        }
    }

}
