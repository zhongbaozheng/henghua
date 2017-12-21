package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 课程视频，含购买状态
 *
 * @author Anderok
 */
public class Rs_SP_CoursesWithBuyStatus extends Result {

    /**
     * 有视频的课程list集合
     */
    private List<CourseWithBuyStatus> courseList;

    public Rs_SP_CoursesWithBuyStatus(int status,
                                      List<CourseWithBuyStatus> courseList) {
        super(status);
        this.courseList = courseList;
    }


    public List<CourseWithBuyStatus> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseWithBuyStatus> courseList) {
        this.courseList = courseList;
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
