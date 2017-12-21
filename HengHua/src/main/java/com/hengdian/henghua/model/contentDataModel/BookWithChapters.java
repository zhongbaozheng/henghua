package com.hengdian.henghua.model.contentDataModel;

import java.util.List;

/**
 * 教材，含章节
 * 
 * @author Anderok
 *
 */
public class BookWithChapters extends Book {
	/** 该书的章节list集合 */
	private List<Chapter> chapterList;

	public BookWithChapters() {
		super();
	}

	public BookWithChapters(int bookID, String bookName,
			List<Chapter> chapterList,int reviewTotal,int testTotal) {
		super(bookID, bookName,reviewTotal,testTotal,0);
		this.chapterList = chapterList;
	}


	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}
	
//	public void refreshCount(){
//		if(chapterList!=null){
//
//		}
//	}
}
