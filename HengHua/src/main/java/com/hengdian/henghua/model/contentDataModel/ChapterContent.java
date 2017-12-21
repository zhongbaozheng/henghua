package com.hengdian.henghua.model.contentDataModel;

/**
 * 
 * 一个章节的内容更
 * 
 * @author Anderok
 *
 */
public class ChapterContent {
	//章节状态标签
	public static final int STATE0_DEFAULT = 0x00;//默认，未读
	public static final int STATE1_READING = 0x01;//阅读中
	public static final int STATE2_HAS_READ = 0x02;//已读
	public static final int STATE3_FAVORITE = 0x03;//收藏
	public static final int STATE4_DROPPED = 0x04;//丢弃
	
	
	/** 章节内容ID */
	private int contentID;
	/**章节内容标题*/
	private String contentTile;
	/** 章节内容 */
	private String content;
	/**阅读状态*/
	private int state = STATE0_DEFAULT;

	public boolean isShow = false;

	public ChapterContent() {
		super();
	}

	public ChapterContent(int contentID,String contentTile, String content) {
		super();
		this.contentID = contentID;
		this.contentTile = contentTile;
		this.content = content;
	}

	public int getContentID() {
		return contentID;
	}

	public void setContentID(int contentID) {
		this.contentID = contentID;
	}

	public String getContentTile() {
		return contentTile;
	}

	public void setContentTile(String contentTile) {
		this.contentTile = contentTile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}	
}
