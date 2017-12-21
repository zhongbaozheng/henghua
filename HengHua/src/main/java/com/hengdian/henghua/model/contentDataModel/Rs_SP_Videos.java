package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 一个课程的视频集合
 *
 * @author Anderok
 */
public class Rs_SP_Videos extends Result {

    /**
     * 有视频的课程list集合
     */
    private List<Video> videoList;
    /**
     * 视频总时长
     */
    private int timeTotal;
    /**
     * 视频时间总进度
     */
    private int timeAchieved;


    public Rs_SP_Videos() {

    }

    public Rs_SP_Videos(int status, List<Video> videoList) {
        super(status);
        this.videoList = videoList;

        refreshCount();
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public int getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(int timeTotal) {
        this.timeTotal = timeTotal;
    }


    /**
     * 已看过的视频数量
     *
     * @return
     */
    public int getNumAchieved() {
        return numAchieved;
    }

    /**
     * 已看过的视频数量
     *
     * @param numAchieved
     */
    public void setNumAchieved(int numAchieved) {
        this.numAchieved = numAchieved;
    }

    public void refreshCount() {
        if(videoList==null){
            return;
        }

        timeTotal = 0;
        timeAchieved = 0;
        numTotal = 0;
        numAchieved = 0;

        if (videoList != null) {
            numTotal = videoList.size();
            for (Video video : videoList) {
                timeTotal += video.getTimeTotal();
                timeAchieved += video.getTimeAchieved();
                // 已看完的
                if (video.getState() >= Video.STATE2_HAS_WATCHED) {
                    numAchieved++;
                }
            }
        }
    }

    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "查询成功";
            case 0:
                return "请重新登录";
            default:
                return super.getStatusMsg();
        }
    }
}
