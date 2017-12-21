package com.hengdian.henghua.model.contentDataModel;

import android.util.Log;

import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.utils.DataRequestUtil;

/**
 * 请求结果基类
 *
 * @author Anderok
 */
public abstract class Result {
    /**
     * 请求结果状态码，注意：非http响应状态码
     */
    public int status;
    /**
     * 内容总数
     */
    public int numTotal = 0;
    /**
     * 处理过的内容总数
     */
    public int numAchieved = 0;
    /**
     * 位置标记
     */
    public int curIndex = 0;

    public Result() {
    }

    public Result(int status) {
        this.status = status;
    }

    /**
     * 获取结果状态码
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public int getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(int numTotal) {
        this.numTotal = numTotal;
    }


    public int getNumAchieved() {
        return numAchieved;
    }

    public void setNumAchieved(int numAchieved) {
        this.numAchieved = numAchieved;
        Log.e("numAchieved",numAchieved+"");
    }


    /**
     * 获取结果状态信息
     *
     * @return
     */
    public String getStatusMsg() {
        switch (status) {
//            case DataRequestUtil.REQUEST_NETWORK_UNAVAILABLE:
//                return "网络不可用";
            case DataRequestUtil.REQUEST_EXCEPTION:
                String msg = "请求异常";
                if (!NetUtil.isNetworkAvailable(MyApplication.getAppContext()))
                    msg += "，网络不可用";
                return msg;
            default:
                return "请求异常,状态码(" + status + ")";
        }
    }
}
