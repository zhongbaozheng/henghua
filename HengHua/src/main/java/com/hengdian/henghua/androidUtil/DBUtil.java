package com.hengdian.henghua.androidUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.hengdian.henghua.model.contentDataModel.BookWithChapters;
import com.hengdian.henghua.model.contentDataModel.BookWithScore;
import com.hengdian.henghua.model.contentDataModel.Chapter;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Chapters;
import com.hengdian.henghua.model.contentDataModel.Rs_CWXX_BooksWithChapters;
import com.hengdian.henghua.model.contentDataModel.Rs_CW_ChapterContents;
import com.hengdian.henghua.model.contentDataModel.Rs_KS_Books;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_GroupByType;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_NotGroup;

import java.util.List;

/**
 * 数据库操作类
 * <p>
 * Created by Ander on 2017/1/23.
 */
public class DBUtil extends SQLiteOpenHelper {

    //类没有实例化普通成员不能用作父类构造器的参数,必须声明为静态

    private static final String DB_NAME = "hengdian.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "T_DATA";

    private static final String COL_ACCOUNT = "ACCOUNT";//账号
    private static final String COL_TYPE = "TYPE"; //REVIEW,TEST,COURSE
    private static final String COL_BOOK_ID = "BOOK_ID";
    private static final String COL_CHAPTER_ID = "CHAPTER_ID";
    private static final String COL_TOTAL = "TOTAL";
    private static final String COL_REACHED = "REACHED";
    private static final String COL_JSON_DATA = "JSON_DATA";


    public DBUtil(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DB_NAME, null, DB_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(DB_NAME, "create Database...");
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" + COL_ACCOUNT + " VARCHAR, " + COL_TYPE + " VARCHAR, "
                + COL_BOOK_ID + " VARCHAR, " + COL_CHAPTER_ID + " VARCHAR, "
                + COL_TOTAL + " INTEGER, " + COL_REACHED + " INTEGER, "
                + COL_JSON_DATA + " TEXT)";

        String dropTableSql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableSql);
        db.execSQL(createTableSql);
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(DB_NAME, "update Database...");
        //db.execSQL("ALTER TABLE hengdian ADD COLUMN other STRING");
    }

    /**
     * 保存知识重温教材（含章节），列表
     *
     * @param data 数据集
     */
    public void saveReviewBookChapterList(Rs_CWXX_BooksWithChapters data) {
        if (data == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.REVIEW_LIST, "", ""};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere
                , indexVal, null, null, null);

        String jsonData = new Gson().toJson(data);

        if (cursor.moveToNext()) {
            ContentValues val = new ContentValues();
            val.put(COL_JSON_DATA, jsonData);
            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {
            Values val = new Values();
            val.type = TYPE.REVIEW_LIST;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取知识重温教材（含章节），列表
     *
     * @return 数据集
     */
    public Rs_CWXX_BooksWithChapters getReviewBookChapterList() {
        SQLiteDatabase db = getReadableDatabase();

        Rs_CWXX_BooksWithChapters rs = new Gson().fromJson(getJsonData(db, false, TYPE.REVIEW_LIST, "", ""), Rs_CWXX_BooksWithChapters.class);

        if (rs == null) {
            db.close();
            return null;
        }

        List<BookWithChapters> bookList = rs.getBookList();
        for (BookWithChapters book : bookList) {


            int bookAchieved = 0;

            List<Chapter> chapterList = book.getChapterList();
            for (Chapter chapter : chapterList) {
                Progress progress = getChapterProgress(db, false, TYPE.REVIEW_CONTENT, book.getBookID() + "", chapter.getChapterID() + "");

                chapter.setReviewAchieved(progress.reached);

                bookAchieved += progress.reached;
            }

            book.setReviewAchieved(bookAchieved);
        }

        db.close();

        return rs;
    }


    /**
     * 保存章节知识重温内容
     *
     * @param bookID    教材
     * @param chapterID 章节ID
     * @param data      数据集
     */
    public void saveReviewContent(String bookID, String chapterID, Rs_CW_ChapterContents data) {
        if (data == null) {
            return;
        }

        List<ChapterContent> list = data.getChapterContentList();

        int total = list.size();
        int reached = 0;

        for (int i = 0; i < total; i++) {
            if (list.get(i).getState() > ChapterContent.STATE0_DEFAULT) {
                reached++;
            }
        }

//        reached = reached-1; //bug解决之一

        data.setNumTotal(total);
        data.setNumAchieved(reached);
        Log.e("reached,total",reached+" | "+total);

        String jsonData = new Gson().toJson(data);

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.REVIEW_CONTENT, bookID, chapterID};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere, indexVal, null, null, null);
        if (cursor.moveToNext()) {

            ContentValues val = new ContentValues();
            val.put(COL_TOTAL, total);
            val.put(COL_REACHED, reached);
            val.put(COL_JSON_DATA, jsonData);

            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {

            Values val = new Values();
            val.type = TYPE.REVIEW_CONTENT;
            val.bookID = bookID;
            val.chapterID = chapterID;
            val.total = total;
            val.reached = reached;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取知识重温内容
     *
     * @param bookID    教材ID
     * @param chapterID 章节ID
     * @return
     */
    public Rs_CW_ChapterContents getReviewContent(String bookID, String chapterID) {
        return new Gson().fromJson(getJsonData(getReadableDatabase(), true, TYPE.REVIEW_CONTENT, bookID, chapterID), Rs_CW_ChapterContents.class);

    }

    /**
     * 保存练习模式的教材（含章节）列表
     *
     * @param data 数据集
     */
    public void saveExerciseBookChapterList(Rs_CWXX_BooksWithChapters data) {
        if (data == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.EXERCISE_LIST, "", ""};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere
                , indexVal, null, null, null);

        String jsonData = new Gson().toJson(data);

        if (cursor.moveToNext()) {
            ContentValues val = new ContentValues();
            val.put(COL_JSON_DATA, jsonData);
            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {
            Values val = new Values();
            val.type = TYPE.EXERCISE_LIST;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取练习模式的教材（含章节）列表
     *
     * @return 数据集
     */
    public Rs_CWXX_BooksWithChapters getExerciseBookChapterList() {
        SQLiteDatabase db = getReadableDatabase();

        Rs_CWXX_BooksWithChapters rs = new Gson().fromJson(getJsonData(db, false, TYPE.EXERCISE_LIST, "", ""), Rs_CWXX_BooksWithChapters.class);

        if (rs == null) {
            db.close();
            return null;
        }

        List<BookWithChapters> bookList = rs.getBookList();
        for (BookWithChapters book : bookList) {

            int bookAchieved = 0;

            List<Chapter> chapterList = book.getChapterList();
            for (Chapter chapter : chapterList) {
                Progress progress = getChapterProgress(db, false, TYPE.EXERCISE_CONTENT, book.getBookID() + "", chapter.getChapterID() + "");

                chapter.setTestAchieved(progress.reached);


                bookAchieved += progress.reached;
            }


            book.setTestAchieved(bookAchieved);
        }

        db.close();

        return rs;
    }

    /**
     * 保存练习模式的习题列表
     *
     * @param bookID    教材ID
     * @param chapterID 章节ID
     * @param data      数据集
     */
    public void saveExerciseContent(String bookID, String chapterID, Rs_Questions_GroupByType data) {
        if (data == null) {
            return;
        }

        List<Question> listSingle = data.getSingleChoiceQuestionList();
        List<Question> listMultiple = data.getMultipleChoiceQuestionList();
        List<Question> listTrueFalse = data.getTrueOrFalseQuestionList();

        int total = listSingle.size() + listMultiple.size() + listTrueFalse.size();

        int reached = 0;
        for (int i = 0; i < listSingle.size(); i++) {
            if (listSingle.get(i).getState() > Question.STATE2_ANSWERED) {
                reached++;
            }
        }

        for (int i = 0; i < listMultiple.size(); i++) {
            if (listMultiple.get(i).getState() > Question.STATE2_ANSWERED) {
                reached++;
            }
        }

        for (int i = 0; i < listTrueFalse.size(); i++) {
            if (listTrueFalse.get(i).getState() > Question.STATE2_ANSWERED) {
                reached++;
            }
        }

        data.setNumTotal(total);
        data.setNumAchieved(reached);

        String jsonData = new Gson().toJson(data);

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.EXERCISE_CONTENT, bookID, chapterID};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere, indexVal, null, null, null);
        if (cursor.moveToNext()) {

            ContentValues val = new ContentValues();
            val.put(COL_TOTAL, total);
            val.put(COL_REACHED, reached);
            val.put(COL_JSON_DATA, jsonData);

            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {

            Values val = new Values();
            val.type = TYPE.EXERCISE_CONTENT;
            val.bookID = bookID;
            val.chapterID = chapterID;
            val.total = total;
            val.reached = reached;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取练习模式的习题列表
     *
     * @param bookID    教材ID
     * @param chapterID 章节ID
     * @return
     */
    public Rs_Questions_GroupByType getExerciseContent(String bookID, String chapterID) {
        return new Gson().fromJson(getJsonData(getReadableDatabase(), true, TYPE.EXERCISE_CONTENT, bookID, chapterID), Rs_Questions_GroupByType.class);
    }

//    /**
//     * 保存闯关模式的教材列表
//     *
//     * @param account 账号
//     * @param data    json数据
//     */
//    public void saveBreakthroughBookList(String account, String data) {
//        SQLiteDatabase db = getWritableDatabase();
//        delete(db, account, DBIndexResolver.breakthroughBookList());
//        insert(db, account, DBIndexResolver.breakthroughBookList(), data);
//        db.close();
//    }
//
//    /**
//     * 获取闯关模式的教材列表
//     *
//     * @param account 账号
//     * @return json数据
//     */
//    public String getBreakthroughBookList(String account) {
//        return query(account, DBIndexResolver.breakthroughBookList());
//    }

    /**
     * 保存闯关模式的章节（关卡）列表
     *
     * @param bookID 教材ID
     * @param data   json数据
     */
    public void saveStageList(String bookID, Rs_CG_Chapters data) {
        if (data == null || data.getStatus() != 200) {
            return;
        }


        String jsonData = new Gson().toJson(data);

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.STAGE_LIST, bookID, ""};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere, indexVal, null, null, null);
        if (cursor.moveToNext()) {

            ContentValues val = new ContentValues();
            val.put(COL_TOTAL, 0);
            val.put(COL_REACHED, 0);
            val.put(COL_JSON_DATA, jsonData);

            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {

            Values val = new Values();
            val.type = TYPE.STAGE_LIST;
            val.bookID = bookID;
            val.chapterID = "";
            val.total = 0;
            val.reached = 0;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取关模式的章节（关卡）列表
     *
     * @param bookID 教材ID
     * @param data
     * @return
     */
    public Rs_CG_Chapters getStageList(String bookID, Rs_CG_Chapters data) {
        SQLiteDatabase db = getReadableDatabase();

//        Rs_CG_Chapters rs = new Gson().fromJson(getJsonData(db, false, TYPE.STAGE_LIST, "", ""), Rs_CG_Chapters.class);

        if (data == null) {
            db.close();
            return null;
        }

        if (data.getStatus() != 200) {
            db.close();
            return data;
        }

        List<Chapter> list = data.getChapterList();
        for (int i=0;i< list.size();i++) {
            Chapter chapter = list.get(i);

            Progress progress = getChapterProgress(db, false, TYPE.STAGE_CONTENT, bookID, chapter.getChapterID() + "");

            //没有内容的关卡也算通关，0=0
            if (progress.reached == progress.total && progress.reached == 100) {
                chapter.setState(Chapter.STATE2_PASSED);

            } else if (progress.reached != progress.total && progress.reached == 50) {
                chapter.setState(Chapter.STATE1_UNLOCKED);
            } else {
                chapter.setState(Chapter.STATE0_DEFAULT);
            }
        }

        db.close();

        return data;
    }


    /**
     * 保存闯关模式的试题
     *
     * @param bookID    教材ID
     * @param chapterID 章节ID
     * @param total
     * @param achieved
     */
    public void saveStageContent(String bookID, String chapterID, int total, int achieved) {

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.STAGE_CONTENT, bookID, chapterID};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA,COL_TOTAL,COL_REACHED}, indexWhere, indexVal, null, null, null);
        if (cursor.moveToNext()) {
            if(achieved > cursor.getInt(2)) {
                ContentValues val = new ContentValues();
                val.put(COL_TOTAL, total);
                val.put(COL_REACHED, achieved);
                val.put(COL_JSON_DATA, "");

                db.update(TABLE_NAME, val, indexWhere, indexVal);
            }
        } else {

            Values val = new Values();
            val.type = TYPE.STAGE_CONTENT;
            val.bookID = bookID;
            val.chapterID = chapterID;
            val.total = total;
            val.reached = achieved;
            val.jsonData = "";
            insert(db, val);
        }

        cursor.close();
        db.close();
    }

//    /**
//     * 获取闯关模式的试题
//     *
//     * @param bookID    教材ID
//     * @param chapterID 章节ID
//     * @return json数据
//     */
//    public Rs_Questions_NotGroup getStageContent(String bookID, String chapterID) {
//        return new Gson().fromJson(getJsonData(getReadableDatabase(), true, TYPE.STAGE_CONTENT, bookID, chapterID), Rs_Questions_NotGroup.class);
//    }


    /**
     * 获取考试模式的教材列表
     *
     * @param data 账号
     * @return
     */
    public Rs_KS_Books getExamBookList(Rs_KS_Books data) {
        SQLiteDatabase db = getReadableDatabase();

        if (data == null) {
            db.close();
            return null;
        }

        if (data.getBookList().size() == 0) {
            db.close();
            return data;
        }

        List<BookWithScore> bookList = data.getBookList();
        for (BookWithScore book : bookList) {

            Progress progress = getBookProgress(db, false, TYPE.EXAM_CONTENT, book.getBookID() + "");
            //book.setNumTotal(progress.total);
            book.setTestAchieved(progress.reached);
        }

        db.close();

        return data;
    }


    /**
     * 保存考试模式的习题
     *
     * @param bookID 教材ID
     * @param data   数据
     */
    public void saveExamContent(String bookID, Rs_Questions_NotGroup data) {
        if (data == null || data.getQuestionList().size() == 0) {
            return;
        }

        List<Question> list = data.getQuestionList();

        int total = list.size();
        int reached = 0;

        for (int i = 0; i < total; i++) {
            if (list.get(i).getState() >= Question.STATE2_ANSWERED) {
                reached++;
            }
        }

        data.setNumTotal(total);
        data.setNumAchieved(reached);

        String jsonData = new Gson().toJson(data);

        SQLiteDatabase db = getWritableDatabase();

        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and "
                + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), TYPE.EXAM_CONTENT, bookID, ""};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_JSON_DATA}, indexWhere, indexVal, null, null, null);
        if (cursor.moveToNext()) {

            ContentValues val = new ContentValues();
            val.put(COL_TOTAL, total);
            val.put(COL_REACHED, reached == 0 ? 100000 : reached);
            val.put(COL_JSON_DATA, jsonData);

            db.update(TABLE_NAME, val, indexWhere, indexVal);

        } else {

            Values val = new Values();
            val.type = TYPE.EXAM_CONTENT;
            val.bookID = bookID;
            val.chapterID = "";
            val.total = total;
            val.reached = reached == 0 ? 100000 : reached;
            val.jsonData = jsonData;

            insert(db, val);
        }

        cursor.close();
        db.close();
    }

    /**
     * 获取考试模式的习题
     *
     * @param bookID 教材ID
     * @return
     */
    public Rs_Questions_NotGroup getExamContent(String bookID) {
        return new Gson().fromJson(getJsonData(getReadableDatabase(), true, TYPE.EXAM_CONTENT, bookID, ""), Rs_Questions_NotGroup.class);

    }


    /**
     * 删除该账号所有数据
     */
    public void cleanDataOfAccount() {

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_ACCOUNT + "=?";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql, new String[]{MyApplication.getAccountInfo().getAccount()});
        db.close();
    }

    /**
     * 单条json数据查询复用工具
     *
     * @param type
     * @param bookID
     * @param chapterID
     * @return json数据
     */
    private String getJsonData(SQLiteDatabase db, boolean closeDb, String type, String bookID, String chapterID) {
        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and " + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), type, bookID, chapterID};

        String[] queryCols = new String[]{COL_JSON_DATA};

        Cursor cursor = db.query(TABLE_NAME, queryCols, indexWhere, indexVal, null, null, null);

        String jsonDate = null;
        boolean isHas = false;
        if (cursor.moveToNext()) {
            isHas = true;
            jsonDate = cursor.getString(0);
        }

        cursor.close();

        if (closeDb) {
            db.close();
        }
        return jsonDate;
    }

    /**
     * 获取教材进度
     *
     * @param db
     * @param closeDb 是否关闭db
     * @param type
     * @param bookID
     * @return
     */
    private Progress getBookProgress(SQLiteDatabase db, boolean closeDb, String type, String bookID) {
        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and " + COL_BOOK_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), type, bookID};

        String[] queryCols = new String[]{"sum(" + COL_TOTAL + ")", "sum(" + COL_REACHED + ")"};

        Cursor cursor = db.query(TABLE_NAME, queryCols, indexWhere, indexVal, null, null, null);

        boolean isHas = false;

        int total = 0;
        int reached = 0;

        if (cursor.moveToNext()) {
            isHas = true;
            total = cursor.getInt(0);
            reached = cursor.getInt(1);
        }

        cursor.close();

        if (closeDb) {
            db.close();
        }
        return new Progress(total, reached);
    }


    /**
     * 获取章节进度
     *
     * @param db
     * @param closeDb   是否关闭db
     * @param type
     * @param bookID
     * @param chapterID
     * @return
     */
    private Progress getChapterProgress(SQLiteDatabase db, boolean closeDb, String type, String bookID, String chapterID) {
        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and " + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=?";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), type, bookID, chapterID};

        String[] queryCols = new String[]{"sum(" + COL_TOTAL + ")", "sum(" + COL_REACHED + ")"};

        Cursor cursor = db.query(TABLE_NAME, queryCols, indexWhere, indexVal, null, null, null);

        boolean isHas = false;

        int total = 0;
        int reached = 0;

        if (cursor.moveToNext()) {
            isHas = true;
            total = cursor.getInt(0);
            reached = cursor.getInt(1);
        }

        cursor.close();
        if (closeDb) {
            db.close();
        }
        return new Progress(total, reached);
    }

    /**
     * 插值复用工具
     *
     * @param db  SQLiteDatabase
     * @param val 插值对象
     * @return
     */
    private SQLiteDatabase insert(SQLiteDatabase db, Values val) {
        if (val == null) {
            return db;
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_ACCOUNT, MyApplication.getAccountInfo().getAccount());
        cv.put(COL_TYPE, val.type);
        cv.put(COL_BOOK_ID, val.bookID);
        cv.put(COL_CHAPTER_ID, val.chapterID);
        cv.put(COL_TOTAL, val.total);
        cv.put(COL_REACHED, val.reached);
        cv.put(COL_JSON_DATA, val.jsonData);

        db.insert(TABLE_NAME, null, cv);

        return db;
    }


    /**
     * 删除复用工具
     *
     * @param db        SQLiteDatabase
     * @param type      类型,DBUtil.TYPE.*
     * @param bookID    教材
     * @param chapterID 章节ID
     * @return
     */
    private SQLiteDatabase delete(SQLiteDatabase db, String type, String bookID, String chapterID) {
        String indexWhere = COL_ACCOUNT + "=? and " + COL_TYPE + "=? and " + COL_BOOK_ID + "=? and " + COL_CHAPTER_ID + "=? ";
        String[] indexVal = new String[]{MyApplication.getAccountInfo().getAccount(), type, bookID, chapterID};

        db.delete(TABLE_NAME, indexWhere, indexVal);

        return db;
    }


    class Values {

        public String type = null;
        public String bookID = "";
        public String chapterID = "";
        public int total = 0;
        public int reached = 0;
        public String jsonData = null;

        public Values() {

        }

        public Values(String type, String bookID, String chapterID, String jsonData) {
            this.type = type;
            this.bookID = bookID;
            this.chapterID = chapterID;
            this.jsonData = jsonData;
        }
    }

    class Progress {
        public int total = 0;
        public int reached = 0;

        Progress(int total, int reached) {
            this.total = total;
            this.reached = reached;
        }
    }

    private class TYPE {

        private static final String REVIEW_LIST = "REVIEW_LIST";
        private static final String REVIEW_CONTENT = "REVIEW_CONTENT";

        private static final String EXERCISE_LIST = "EXERCISE_LIST";
        private static final String EXERCISE_CONTENT = "EXERCISE_CONTENT";

        private static final String BREAKTHROUGH_LIST = "BREAKTHROUGH_LIST";
        private static final String STAGE_LIST = "STAGE_LIST";
        private static final String STAGE_CONTENT = "STAGE_CONTENT";

        private static final String EXAM_LIST = "EXAM_LIST";
        private static final String EXAM_CONTENT = "EXAM_CONTENT";

        private static final String COURSE_LIST = "COURSE_LIST";
        private static final String VIDEO_LIST = "VIDEO_LIST";
    }


}

/*
 * SQLite采用的是动态数据类型，会根据存入值自动判断。
 * SQLite具有以下五种数据类型：
 * 1.NULL：空值。
 * 2.INTEGER：带符号的整型，具体取决有存入数字的范围大小。
 * 3.REAL：浮点数字，存储为8-byte IEEE浮点数。
 * 4.TEXT：字符串文本。
 * 5.BLOB：二进制对象。
 * <p>
 * <p>
 * 实际上，sqlite3也接受如下的数据类型：
 * smallint 16 位元的整数。
 * integer 32 位元的整数。
 * decimal(p,s) p 精确值和 s 大小的十进位整数，精确值p是指全部有几个数(digits)大小值，s是指小数点後有几位数。如果没有特别指定，则系统会设为 p=5; s=0 。
 * float  32位元的实数。
 * double  64位元的实数。
 * char(n)  n 长度的字串，n不能超过 254。
 * varchar(n) 长度不固定且其最大长度为 n 的字串，n不能超过 4000。
 * graphic(n) 和 char(n) 一样，不过其单位是两个字元 double-bytes， n不能超过127。这个形态是为了支援两个字元长度的字体，例如中文字。
 * vargraphic(n) 可变长度且其最大长度为 n 的双字元字串，n不能超过 2000
 * date  包含了 年份、月份、日期。
 * time  包含了 小时、分钟、秒。
 * timestamp 包含了 年、月、日、时、分、秒、千分之一秒。
 * <p>
 * datetime 包含日期时间格式，必须写成'2010-08-05'不能写为'2010-8-5'，否则在读取时会产生错误！
 * <p>
 * Sqlite常用数据类型,这句话本身就有问题，因为：SQLite是无类型的.
 * 这意味着你可以保存任何类型的数据到你所想要保存的任何表的任何列中,
 * 无论这列声明的数据类型是什么(只有自动递增Integer Primary Key才有用).
 * 对于SQLite来说对字段不指定类型是完全有效的
 * <p>
 * <p>
 * <p>
 * char、varchar、text和nchar、nvarchar、ntext的区别:
 * 1、CHAR。CHAR存储定长数据很方便，CHAR字段上的索引效率级高，比如定义char(10)，那么不论你存储的数据是否达到了10个字节，都要占去10个字节的空间,不足的自动用空格填充。
 * 2、VARCHAR。存储变长数据，但存储效率没有CHAR高。如果一个字段可能的值是不固定长度的，我们只知道它不可能超过10个字符，把它定义为 VARCHAR(10)是最合算的。VARCHAR类型的实际长度是它的值的实际长度+1。为什么“+1”呢？这一个字节用于保存实际使用了多大的长度。从空间上考虑，用varchar合适；从效率上考虑，用char合适，关键是根据实际情况找到权衡点。
 * 3、TEXT。text存储可变长度的非Unicode数据，最大长度为2^31-1(2,147,483,647)个字符。
 * 4、NCHAR、NVARCHAR、NTEXT。这三种从名字上看比前面三种多了个“N”。它表示存储的是Unicode数据类型的字符。我们知道字符中，英文字符只需要一个字节存储就足够了，但汉字众多，需要两个字节存储，英文与汉字同时存在时容易造成混乱，Unicode字符集就是为了解决字符集这种不兼容的问题而产生的，它所有的字符都用两个字节表示，即英文字符也是用两个字节表示。nchar、nvarchar的长度是在1到4000之间。和char、varchar比较起来，nchar、nvarchar则最多存储4000个字符，不论是英文还是汉字；而char、varchar最多能存储8000个英文，4000个汉字。可以看出使用nchar、nvarchar数据类型时不用担心输入的字符是英文还是汉字，较为方便，但在存储英文时数量上有些损失。
 * 所以一般来说，如果含有中文字符，用nchar/nvarchar，如果纯英文和数字，用char/varchar。
 */