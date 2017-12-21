package com.hengdian.henghua.androidUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 本类需要的权限：
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 * <p>
 * 获取手机型号： android.os.Build.MODEL
 * 获取手机的SDK版本号：android.os.Build.VERSION.SDK
 * <p>
 * 网络类型的检查，特别是区分2G网络是为wap，还是net类型
 * wap类型，需要配置代理上网
 * <p>
 * <p>
 * 1） 移动的WAP名称是CMWAP，NET名称是CMNET；
 * 2） 联通的WAP名称是UNIWAP，NET名称是UNINET；联通3G的WAP名称是3GWAP，NET名称是3GNET；
 * 3） 电信的WAP名称是CTWAP，NET名称是CTNET；
 * <p>
 * Created by Anderok on 2017/1/15.
 */

public class NetUtil {

    /**
     * Android系统服务：
     * {@link android.view.WindowManager}, {@link android.view.LayoutInflater},
     * {@link android.app.ActivityManager}, {@link android.os.PowerManager},
     * {@link android.app.AlarmManager}, {@link android.app.NotificationManager},
     * {@link android.app.KeyguardManager}, {@link android.location.LocationManager},
     * {@link android.app.SearchManager}, {@link android.os.Vibrator},
     * {@link android.net.ConnectivityManager},
     * {@link android.net.wifi.WifiManager},
     * {@link android.media.AudioManager}, {@link android.media.MediaRouter},
     * {@link android.telephony.TelephonyManager}, {@link android.telephony.SubscriptionManager},
     * {@link android.view.inputmethod.InputMethodManager},
     * {@link android.app.UiModeManager}, {@link android.app.DownloadManager},
     * {@link android.os.BatteryManager}, {@link android.app.job.JobScheduler},
     * {@link android.app.usage.NetworkStatsManager}.
     */

    /*
     netWorkInfo:
        mNetworkType = source.mNetworkType;
        mSubtype = source.mSubtype;
        mTypeName = source.mTypeName;
        mSubtypeName = source.mSubtypeName;
        mState = source.mState;
        mDetailedState = source.mDetailedState;
        mReason = source.mReason;
        mExtraInfo = source.mExtraInfo;
        mIsFailover = source.mIsFailover;
        mIsAvailable = source.mIsAvailable;
        mIsRoaming = source.mIsRoaming;
        mIsMetered = source.mIsMetered;
    */

    /**
     * 检测网络（WLAN、3G/2G）是否活跃（已连接或正在连接）
     * <p>
     * 权限：
     * <p>
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * <uses-permission android:name="android.permission.INTERNET"/>
     * <p>
     * connect和available区分：
     * 1，显示连接已保存，但标题栏没有，即没有实质连接上   not connect， available
     * 2，显示连接已保存，标题栏也有已连接上的图标，          connect， available
     * 3，选择不保存后                                    not connect， available
     * 4，选择连接，在正在获取IP地址时                     not connect， not available
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkActive(Context context) {

        NetworkInfo networkInfo = getNetworkInfo(context, NETWORK_INFO_ACTIVE);
        // 当前所连接的网络可用
        // networkInfo.isConnected() 相当于networkInfo.getState() == NetworkInfo.State.CONNECTED
        if (networkInfo != null) {
//            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
//
//            for (int i = 0; i < info.length; i++) {
//                if (networkInfo[i].isAvailable()) {
//                    return true;
//                }
//            }

            return networkInfo.isConnectedOrConnecting();
        }


        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        NetworkInfo networkInfo = getNetworkInfo(context, NETWORK_INFO_ACTIVE);
        // 当前网络是否已连接
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }

        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {

        NetworkInfo networkInfo = getNetworkInfo(context, NETWORK_INFO_ACTIVE);
        // 当前网络是否已连接
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }

        return false;
    }

    /**
     * 判断wifi网络是否可用
     *
     * @param context
     * @return
     */

    public static boolean isWifiAvailable(Context context) {

        NetworkInfo wiFiNetworkInfo = getNetworkInfo(context, NETWORK_INFO_WIFI);
        if (wiFiNetworkInfo != null) {
            return wiFiNetworkInfo.isAvailable();
        }

        return false;
    }

    /**
     * 判断wifi网络是否已连接
     *
     * @param context
     * @return
     */

    public static boolean isWifiConnected(Context context) {

        NetworkInfo wiFiNetworkInfo = getNetworkInfo(context, NETWORK_INFO_WIFI);
        if (wiFiNetworkInfo != null) {
            return wiFiNetworkInfo.isConnected();
        }

        return false;
    }


    /**
     * 判断移动网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileNetworkAvailable(Context context) {
        NetworkInfo mobileNetworkInfo = getNetworkInfo(context, NETWORK_INFO_MOBILE);
        if (mobileNetworkInfo != null) {
            return mobileNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断移动网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileNetworkConnected(Context context) {
        NetworkInfo mobileNetworkInfo = getNetworkInfo(context, NETWORK_INFO_MOBILE);
        if (mobileNetworkInfo != null) {
            return mobileNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * 打开设置网络界面，如果不写在Activity里面则不需要参数
     *
     * @param context
     */
    public static void setNetworkTip(final Context context) {
        final String items[] = {"打开WIFI设置窗口", "打开移动网络设置窗口", "开启WIFI", "开启移动网络", "取消"};
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示")
                //.setMessage("网络连接不可用,是否进行设置?")
                //注意设置了列表显示就不要设置builder.setMessage()，否则列表不起作用
                .setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                wifiSetting(context);
                                break;
                            case 1:
                                mobileNetworkSetting(context);
                                break;
                            case 2:
                                toggleWiFi(context, true);
                                break;
                            case 3:
                                toggleMobileData(context, true);
                                break;
                            case 4:
                                dialog.cancel();
                                break;
                        }
                    }

                })
//                .setPositiveButton("打开设置", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = null;
//                                //判断手机系统的版本  即API大于10 就是3.0或以上版本
//                                if (android.os.Build.VERSION.SDK_INT > 10) {
//                                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
//                                } else {
//                                    intent = new Intent();
//                                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
//                                    intent.setComponent(component);
//                                    intent.setAction("android.intent.action.VIEW");
//                                }
//
//                                context.startActivity(intent);
//                                dialog.cancel();
//                            }
//                        }
//
//                )
//
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //如果在创建AlertDialog的时候调用了setOnCancelListener则mCancelMessage变量有作用，否则dismiss和cancel等同
//                                dialog.dismiss();
//                            }
//                        }
//
//                )
                .show();

    }

    public static void mobileNetworkSetting(Context context) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
//            intent = new Intent();
//            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
//            intent.setComponent(component);
//            intent.setAction("android.intent.action.VIEW");
            ToastUtil.toastMsgLong("请手动设置移动网络");
        }

        context.startActivity(intent);
    }

    public static void wifiSetting(Context context) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        } else {
//            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
//            intent.setComponent(component);
//            intent.setAction("android.intent.action.VIEW");
            ToastUtil.toastMsgLong("请手动设置WFI");
        }

        context.startActivity(intent);
    }


    /**
     * WIFI网络开关
     *
     * @param context
     * @param enable
     */
    public static void toggleWiFi(Context context, boolean enable) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wm.setWifiEnabled(enable);
        } catch (Exception e) {
            ToastUtil.toastMsgLong("请手动打开WIFI");
        }
    }


    /**
     * 移动网络开关
     * <p>
     * 没有直接的API可调用,但是在ConnectivityManager中有一隐藏的方法setMobileDataEnabled()
     * 源码如下:
     * public void setMobileDataEnabled(boolean enabled) {
     * try {
     * mService.setMobileDataEnabled(enabled);
     * } catch (RemoteException e) {
     * }
     * }
     * <p>
     * 这里的重点就是mService,查看其声明:
     * private IConnectivityManager mService;
     * 继续查看源码可知IConnectivityManager为了一个AIDL(接口interface IConnectivityManager)
     * <p>
     * <p>
     * 调用过程:
     * ConnectivityManager中有一隐藏的方法setMobileDataEnabled()
     * 在setMobileDataEnabled()中调用了IConnectivityManager中的setMobileDataEnabled(boolean)
     * <p>
     * 所以我们首先需要反射出ConnectivityManager类的成员变量mService(IConnectivityManager类型)
     */
    public static void toggleMobileData(Context context, boolean enabled) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //ConnectivityManager类
            Class<?> connectivityManagerClass = null;
            //ConnectivityManager类中的字段
            Field connectivityManagerField = null;


            //IConnectivityManager接口
            Class<?> iConnectivityManagerClass = null;
            //IConnectivityManager接口的对象
            Object iConnectivityManagerObject = null;
            //IConnectivityManager接口的对象的方法
            Method setMobileDataEnabledMethod = null;

            try {
                //取得ConnectivityManager类
                connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
                //取得ConnectivityManager类中的字段mService
                connectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
                //取消访问私有字段的合法性检查
                //该方法来自java.lang.reflect.AccessibleObject
                connectivityManagerField.setAccessible(true);


                //实例化mService
                //该get()方法来自java.lang.reflect.Field
                //一定要注意该get()方法的参数:
                //它是mService所属类的对象
                //完整例子请参见:
                //http://blog.csdn.net/lfdfhl/article/details/13509839
                iConnectivityManagerObject = connectivityManagerField.get(connectivityManager);
                //得到mService所属接口的Class
                iConnectivityManagerClass = Class.forName(iConnectivityManagerObject.getClass().getName());
                //取得IConnectivityManager接口中的setMobileDataEnabled(boolean)方法
                //该方法来自java.lang.Class.getDeclaredMethod
                setMobileDataEnabledMethod =
                        iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                //取消访问私有方法的合法性检查
                //该方法来自java.lang.reflect.AccessibleObject
                setMobileDataEnabledMethod.setAccessible(true);
                //调用setMobileDataEnabled方法
                setMobileDataEnabledMethod.invoke(iConnectivityManagerObject, enabled);
            } catch (Exception e) {
                ToastUtil.toastMsgLong("请手动打开移动网络");
            }
        } else {
            ToastUtil.toastMsgLong("请手动打开移动网络");
        }
    }

//    /**
//     * <p>GPS开关
//     * <p>当前若关则打开
//     * <p>当前若开则关闭
//     */
//    private void toggleGPS() {
//        Intent gpsIntent = new Intent();
//        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
//        gpsIntent.setData(Uri.parse("custom:3"));
//        try {
//            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
//        } catch (CanceledException e) {
//            e.printStackTrace();
//        }


    public static final String NETWORK_INFO_WIFI = "WIFI";
    public static final String NETWORK_INFO_MOBILE = "MOBILE";
    public static final String NETWORK_INFO_ACTIVE = "ACTIVE";

    /**
     * 根据网络类型获取活跃的NetworkInfo
     *
     * @param context
     * @param networkType
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context, String networkType) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            switch (networkType) {
                case NETWORK_INFO_WIFI:
                    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                case NETWORK_INFO_MOBILE:
                    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                case NETWORK_INFO_ACTIVE:
                    return connectivityManager.getActiveNetworkInfo();//没有活跃的网络返回null
                default:
                    return null;
            }
        } else {
            ToastUtil.toastMsgShort("无法获取网络信息");
            return null;
        }
    }

    /**
     * 判断是否漫游网络
     */
    public static boolean isNetworkRoaming(Context context) {

        NetworkInfo networkInfo = getNetworkInfo(context, NETWORK_INFO_ACTIVE);
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null && tm.isNetworkRoaming()) {
                return true;
            }
        }

        return false;
    }

    //没有网络连接
    public static final String NETWORK_INVALID = "INVALID";
    //未知网络
    public static final String NETWORK_UNKNOWN = "UNKNOWN";

    //wifi连接
    public static final String NETWORK_WIFI = "WIFI";
    //移动网络类型
    public static final String NETWORK_WAP = "WAP";
    public static final String NETWORK_2G = "2G";
    public static final String NETWORK_3G = "3G"; //3G和3G以上网络可统称为快速网络
//    public static final String NETWORK_FAST = "快速网路"; //3G和3G以上网络可统称为快速网络

    /**
     * 获取网络类型，wifi,wap,2g,3g(快速网路)
     *
     * @param context context
     * @return String 网络类型:INVALID(没有可用的网络),UNKNOWN(未知网络),WIFI,WAP,2G,3G(快速网路)
     */
    public static String getNetWorkType(Context context) {
        String netWorkType = NETWORK_INVALID;

        NetworkInfo networkInfo = getNetworkInfo(context, NETWORK_INFO_ACTIVE);
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                netWorkType = NETWORK_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {

                String proxyHost = android.net.Proxy.getDefaultHost(); //代理host
                //需要设置代理的是wap
                if ( proxyHost!= null && !proxyHost.isEmpty()) {
                    netWorkType = NETWORK_WAP;
                } else {//否则是net
                    switch (isFastMobileNetwork(context)) {
                        case NETWORK_SPEED_SLOW:
                            netWorkType = NETWORK_2G;
                            break;
                        case NETWORK_SPEED_FAST:
                            netWorkType = NETWORK_3G;
//                            netWorkType = NETWORK_FAST;
                            break;
                        case NETWORK_SPEED_UNKNOWN:
                            netWorkType = NETWORK_UNKNOWN;
                            break;
                    }
                }
            }
        } else {
            netWorkType = NETWORK_INVALID;
        }

        return netWorkType;
    }







//    public static void check(Context context) {
//        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity != null) {
//            // 获取网络连接管理的对象
//            NetworkInfo info = connectivity.getActiveNetworkInfo();
//
//            if (info != null && info.isConnected()) {
//                // 判断当前网络是否已经连接
//                if (info.getState() == NetworkInfo.State.CONNECTED) {
//                    if (info.getTypeName().equals("WIFI")) {
//
//                        else{
//                            Uri uri = Uri.parse("content://telephony/carriers/preferapn");
//                            Cursor cr = context.getContentResolver().query(uri, null, null, null, null);
//                            while (cr != null && cr.moveToNext()) {
//                                // APN id
//                                @SuppressWarnings("unused")
//                                String id = cr.getString(cr.getColumnIndex("_id"));
//                                // APN name
//                                @SuppressWarnings("unused")
//                                String apn = cr.getString(cr.getColumnIndex("apn"));
//                                // do other things...
//                                String strProxy = cr.getString(cr.getColumnIndex("proxy"));
//                                String strPort = cr.getString(cr.getColumnIndex("port"));
//                                if (strProxy != null && !"".equals(strProxy)) {
//                                    Config.host = strProxy;
//                                    Config.port = Integer.valueOf(strPort);
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    public static final int NETWORK_SPEED_UNKNOWN = -1;
    public static final int NETWORK_SPEED_SLOW = 0;
    public static final int NETWORK_SPEED_FAST = 1;

    /**
     * 判断是否是快速网络
     *
     * @param context
     * @return 未知：-1，否：0，是：1
     */
    public static int isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return NETWORK_SPEED_SLOW; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return NETWORK_SPEED_SLOW; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NETWORK_SPEED_SLOW; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return NETWORK_SPEED_FAST; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return NETWORK_SPEED_FAST; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return NETWORK_SPEED_SLOW; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return NETWORK_SPEED_FAST; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return NETWORK_SPEED_FAST; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return NETWORK_SPEED_FAST; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return NETWORK_SPEED_FAST; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return NETWORK_SPEED_FAST; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return NETWORK_SPEED_FAST; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_SPEED_FAST; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_SPEED_SLOW; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_SPEED_FAST; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return NETWORK_SPEED_UNKNOWN;
            default:
                return NETWORK_SPEED_UNKNOWN;
        }

    }


}

//对话框

/*AlertDialog.Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络")
        .setMessage("是否对网络进行设置？");
        b.setPositiveButton("是", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int whichButton) {
        Intent mIntent = new Intent("/");
        ComponentName comp = new ComponentName(
        "com.android.settings",
        "com.android.settings.WirelessSettings");
        mIntent.setComponent(comp);
        mIntent.setAction("android.intent.action.VIEW");
        startActivityForResult(mIntent, 0);  // 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写
        }
        }).setNeutralButton("否", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int whichButton) {
        dialog.cancel();
        }
        }).show();

*/


/**
 * 一、ConnectivityManager详解：
 * ConnectivityManager是网络连接相关的管理器，它主要用于查询网络状态并在网络发生改变时发出状态变化通知。这个类主要负责的下列四个方面：
 * <p>
 * 1.  监控网络状态（包括WiFi, GPRS, UMTS等）。
 * 2.  当网络连接改变时发送广播Intent。
 * 3.  当一种网络断开时，试图连接到另一种网络进行故障处理。
 * 4.  提供一系列接口让应用程序查询可获得的网络的粗粒度和细粒度状态。
 * <p>
 * 比较重要的几个类常量
 * <p>
 * int	TYPE_BLUETOOTH	                The Bluetooth data connection. 蓝牙数据连接
 * int	TYPE_ETHERNET	                The Ethernet data connection. 以太网数据连接
 * int	TYPE_MOBILE	                    The Mobile data connection. 移动数据链接
 * int	TYPE_WIFI	                    The WIFI data connection. wifi链接
 * String	CONNECTIVITY_ACTION	            网络连接发生改变
 * int	DEFAULT_NETWORK_PREFERENCE      默认网络连接偏好，建议在config.xml中进行配置.并通过调用 getNetworkPreference() 获取应用的当前设置值。
 * String	EXTRA_EXTRA_INFO	The lookup key for a string that provides optionally supplied extra information about the network state.
 * 查询关键字，提供关于网络状态的信息
 * String	EXTRA_NETWORK_INFO	 建议使用getActiveNetworkInfo() or getAllNetworkInfo()获取网络连接信息
 * String	EXTRA_NETWORK_TYPE	触发 CONNECTIVITY_ACTION广播的网络类型
 * <p>
 * 比较重要的方法
 * <p>
 * NetworkInfo                 getActiveNetworkInfo() 获取当前连接可用的网络
 * NetworkInfo[]               getAllNetworkInfo() 获取设备支持的所有网络类型的链接状态信息。
 * NetworkInfo                 getNetworkInfo(int networkType)  获取特定网络类型的链接状态信息
 * int                         getNetworkPreference()  获取当前偏好的网络类型。
 * boolean                     isActiveNetworkMetered()  Returns if the currently active data network is metered.
 * static boolean              isNetworkTypeValid(int networkType)   判断给定的数值是否表示一种网络
 * boolean                     requestRouteToHost(int networkType, int hostAddress)
 * Ensure that a network route exists to deliver traffic to the specified host via the specified network interface.
 * void                        setNetworkPreference(int preference)   Specifies the preferred network type.
 * int                         startUsingNetworkFeature(int networkType, String feature)
 * Tells the underlying networking system that the caller wants to begin using the named feature.
 * int                         stopUsingNetworkFeature(int networkType, String feature)
 * Tells the underlying networking system that the caller is finished using the named feature.
 * <p>
 * <p>
 * <p>
 * 二. NetworkInfo详解
 * <p>
 * NetworkInfo是一个描述网络状态的接口，可通过ConnectivityManager调用getActiveNetworkInfo()获得当前连接的网络类型。
 * <p>
 * NetworkInfo有两个枚举类型的成员变量NetworkInfo.DetailedState和NetworkInfo.State，用于查看当前网络的状态。其中NetworkInfo.State的值包括：
 * <p>
 * NetworkInfo.State 	CONNECTED
 * 已连接
 * NetworkInfo.State 	CONNECTING
 * 正在连接
 * NetworkInfo.State 	DISCONNECTED
 * <p>
 * NetworkInfo.State 	DISCONNECTING
 * <p>
 * NetworkInfo.State 	SUSPENDED
 * <p>
 * NetworkInfo.State 	UNKNOWN
 * <p>
 * NetworkInfo.DetailedState则状态描述更为详细。
 * <p>
 * <p>
 * NetworkInfo还包括一系列可用的方法用于判断当前网络是否可用，如下：
 * <p>
 * <p>
 * getDetailedState()
 * <p>
 * getExtraInfo()
 * <p>
 * getReason()  如果数据网络连接可用，但是连接失败，则通过此方法可获得尝试链接失败的原因
 * <p>
 * getState()  获取网络连接的粗粒度状态
 * <p>
 * getSubtype()
 * <p>
 * getSubtypeName()
 * <p>
 * getType()报告当前网络从属的网络类型
 * <p>
 * getTypeName()报告当前网络从属的网络类型，更明确的方式如wifi,和mobile等。
 * <p>
 * isAvailable()  判断当前网络是否可用
 * <p>
 * isConnected()  判断当前网络是否存在，并可用于数据传输
 * <p>
 * isConnectedOrConnecting()
 * <p>
 * isFailover()
 * Indicates whether the current attempt to connect to the network resulted from the ConnectivityManager trying to fail over to this network following a disconnect from another network.
 * <p>
 * isRoaming()  判断设备当前是否在网络上漫游
 * <p>
 * toString()  返回一个包含该网络的简单的易懂的字符串描述。
 * Returns a string containing a concise, human-readable description of this object.
 */