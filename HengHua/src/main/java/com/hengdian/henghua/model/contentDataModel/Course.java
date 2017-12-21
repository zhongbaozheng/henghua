package com.hengdian.henghua.model.contentDataModel;

/**
 * 课程（视频）
 * 
 * @author Anderok
 *
 */
public class Course {
	/** 课程ID */
	private int courseID;
	/** 课程名称 */
	private String courseName;
	/** 课程概述 */
	private String summary;
	/** 课程价格 */
	private double price;

	public Course() {
		super();
	}

	public Course(int courseID, String courseName, String summary,
			double price) {
		super();
		this.courseID = courseID;
		this.courseName = courseName;
		this.summary = summary;
		this.price = price;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
