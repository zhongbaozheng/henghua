package com.hengdian.henghua.model.contentDataModel;

/**
 * 视频
 *
 * @author Anderok
 */
public class Video {
    // 本对象状态标签
    public static final int STATE0_DEFAULT = 0x0000; // 默认（没动过状态）
    public static final int STATE1_IS_WATCHING = 0x0001; // 还没看完
    public static final int STATE2_HAS_WATCHED = 0x0002; // 已看完

    /**
     * 视频ID
     */
    private int videoID;
    /**
     * 视频名称
     */
    private String videoName;
    /**
     * 视频截图URL
     */
    private String imageUrl;
    /**
     * 视频URL
     */
    private String videoUrl;
    /**
     * 视频总时长
     */
    private int timeTotal = 0;
    /**
     * 视频播放进度
     */
    private int timeAchieved = 0;
    /**
     * 视频状态
     */
    private int state = STATE0_DEFAULT;

    public Video(int videoID, String videoName, String imageUrl,
                 String videoUrl) {
        super();
        this.videoID = videoID;
        this.videoName = videoName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public Video(int videoID, String videoName, String imageUrl,
                 String videoUrl, int timeTotal, int timeAchieved) {
        super();
        this.videoID = videoID;
        this.videoName = videoName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.timeTotal = timeTotal;
        this.timeAchieved = timeAchieved;
    }

    public Video(int videoID, String videoName, String imageUrl,
                 String videoUrl, int timeTotal, int timeAchieved, int state) {
        super();
        this.videoID = videoID;
        this.videoName = videoName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.timeTotal = timeTotal;
        this.timeAchieved = timeAchieved;
        this.state = state;
    }

    public int getVideoID() {
        return videoID;
    }

    public void setVideoID(int videoID) {
        this.videoID = videoID;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(int timeTotal) {
        this.timeTotal = timeTotal;
    }

    public int getTimeAchieved() {
        return timeAchieved;
    }

    public void setTimeAchieved(int timeAchieved) {
        this.timeAchieved = timeAchieved;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
