package com.hengdian.henghua.model.contentDataModel;

/**
 * 课程（视频），含购买状态
 * @author Anderok
 *
 */
public class CourseWithBuyStatus extends Course {
	/**购买状态*/
	private int buyStatus;//0未购买，1已购买
	
	public CourseWithBuyStatus() {
		super();
	}

	public CourseWithBuyStatus(int courseID, String courseName, String summary,
			double price,int buyStatus) {
		super(courseID, courseName, summary, price);
		this.buyStatus = buyStatus;
	}

	public int getBuyStatus() {
		return buyStatus;
	}

	public void setBuyStatus(int buyStatus) {
		this.buyStatus = buyStatus;
	}	
}
