package com.hengdian.henghua.model.contentDataModel;

/**
 * 教材，含成绩
 * 
 * @author Anderok
 *
 */
public class BookWithScore extends Book {
	/**教材考试成绩，未考教材score=-1*/
	private int score;
	
	public BookWithScore() {
		super();
	}
	
	public BookWithScore(int bookID,String bookName,int score) {
		super(bookID, bookName);
		this.score = score;
	}

	public BookWithScore(int bookID, String bookName,int testTotal,long timeRemain,int score) {
		super(bookID, bookName,0,testTotal, timeRemain);
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}	
}
