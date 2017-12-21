package com.hengdian.henghua.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hengdian.henghua.androidUtil.NetUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Anderok on 2017/1/15.
 * <p>
 * Service+Broadcast+Timer+ui【通过绑定服务、自定义回调接口判断网络连接】
 * 自定义一个Service，
 * 在该Service中设置一个定时器Timer,
 * 通过TimerTask的策略来检查当前应用的网络连接状态，
 * 关键:在该Service需要自定义一个回调接口用于向Activity发送网络状态，
 * <p>
 * 然后通过Bind来绑定当前的Service,在绑定成功之后得到回调信息
 */

public class NetService extends Service {
    Context context;
    private NetBind netBind = new NetBind();
    //网络连接状态
    boolean isNetworkAvailable = false;
    //定时器
    Timer timer = new Timer();

    private NetworkStateMonitor networkStateMonitor;

    //网络检查广播接受者
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 启动定时任务
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //立即启动，每隔5秒执行一次Task
                Log.i("tag", "Net state service" + Thread.currentThread().getId());
                timer.schedule(new NetTask(context), 0, 5 * 1000);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return netBind;
    }

    //定义一个Bind类
    class NetBind extends Binder {
        public NetService getNetService() {
            return NetService.this;
        }
    }

    @Override
    public void onCreate() {
        // 注册广播   检查网络状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        Log.i("tag", "Net state service：" + Thread.currentThread().getId());
        super.onCreate();
    }


    //具体的TimerTask实现类
    class NetTask extends TimerTask {
        public NetTask(Context context1) {
            context = context1;
        }

        @Override
        public void run() {

            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (NetUtil.isNetworkActive(context)) {
                isNetworkAvailable = true;
            } else {
                isNetworkAvailable = false;
            }
            Log.i("tag", "un Net state serviceR：" + Thread.currentThread().getId());
            if (networkStateMonitor != null) {
                networkStateMonitor.GetNetworkState(isNetworkAvailable); // 通知网络状态改变
            }
        }

    }

    // 网络状态改变之后，通过此接口的实例通知当前网络的状态，此接口在Activity中注入实例对象
    public interface NetworkStateMonitor {
        public void GetNetworkState(boolean isConnected);
    }

    public void setNetworkStateMonitor(NetworkStateMonitor networkStateMonitor) {
        this.networkStateMonitor = networkStateMonitor;
    }

    //销毁广播、停止定时轮询
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unregisterReceiver(receiver);
    }

}
