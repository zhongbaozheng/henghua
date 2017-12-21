package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 有视频的课程汇总
 *
 * @author Anderok
 */
public class Rs_SP_Courses extends Result {

    /**
     * 有视频的课程list集合
     */
    private List<Course> courseList;

    public Rs_SP_Courses(int status, List<Course> courseList) {
        super(status);
        this.courseList = courseList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
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
