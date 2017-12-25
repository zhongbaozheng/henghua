package com.hengdian.henghua.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.model.NameValuePair;
import com.hengdian.henghua.model.contentDataModel.Book;
import com.hengdian.henghua.model.contentDataModel.BookWithChapters;
import com.hengdian.henghua.model.contentDataModel.BookWithScore;
import com.hengdian.henghua.model.contentDataModel.Chapter;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.model.contentDataModel.Course;
import com.hengdian.henghua.model.contentDataModel.CourseWithBuyStatus;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Books;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Chapters;
import com.hengdian.henghua.model.contentDataModel.Rs_CWXX_BooksWithChapters;
import com.hengdian.henghua.model.contentDataModel.Rs_CW_ChapterContents;
import com.hengdian.henghua.model.contentDataModel.Rs_KS_Books;
import com.hengdian.henghua.model.contentDataModel.Rs_KS_ExamCommit;
import com.hengdian.henghua.model.contentDataModel.Rs_LogIn;
import com.hengdian.henghua.model.contentDataModel.Rs_LogOut;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_GroupByType;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_NotGroup;
import com.hengdian.henghua.model.contentDataModel.Rs_SP_Courses;
import com.hengdian.henghua.model.contentDataModel.Rs_SP_CoursesWithBuyStatus;
import com.hengdian.henghua.model.contentDataModel.Rs_SP_Videos;
import com.hengdian.henghua.model.contentDataModel.Rs_UpdatePwd;
import com.hengdian.henghua.model.contentDataModel.Video;
import com.hengdian.henghua.utils.HttpUtil.HttpResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hengdian.henghua.utils.AsyncDataUtil.isNetworkAvailable;

public class DataRequestUtil {
    public static final int REQUEST_EXCEPTION = 0x5678;
    public static final int REQUEST_NETWORK_UNAVAILABLE = 0x6789;

    public static final int DEF_NUM = 0;
    public static final String DEF_STR = "没有该字段内容";

    private static final int EXC_500 = 500;//500异常代码
    //最大重试次数
    private static final int RETRY_MAX = 3;
    private static final int SLEEP_TIME = 200;

    /*请求500异常重试计数*/

    private static int retryLogin = 0;
    private static int retryLogout = 0;
    private static int retryUpdatePassWord = 0;
    private static int retryCwxx_getAllBooksWithChapters = 0;
    private static int retryCw_getChapterContents = 0;
    private static int retryXx_getQuestions = 0;
    private static int retryXx_getQuestionsGroupByType = 0;
    private static int retryCg_getBooks = 0;
    private static int retryCg_getChapters = 0;
    private static int retryCg_getQuestionsOfChapter = 0;
    private static int retryKs_getAllBooksWithScore = 0;
    private static int retryKs_getQuestionsOfBook = 0;
    private static int retryKs_examCommit = 0;
    private static int retrySp_getAllCourse = 0;
    private static int retrySp_getAllCourseWithBuyStatus = 0;
    private static int retrySp_getVideosOfCourse = 0;

    /**
     * 学生模块，登录
     *
     * @param userName 用户名
     * @param pwd      密码
     */
    public static synchronized Rs_LogIn logIn(String userName, String pwd) {

        String url = Config.FunctionalURL.STUDENT_LOGIN;

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userName);
        params.put("password", pwd);

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        String tokenID = "";
        String realName = "";

        if (200 == status) {
            tokenID = GsonUtil.getString(jsonObj, "token_id", "");
            realName = GsonUtil.getString(jsonObj, "realname", "");
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;


        Rs_LogIn mRs = new Rs_LogIn(status, userName, realName, pwd, tokenID);

        if (EXC_500 == status && retryLogin < RETRY_MAX) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryLogin++;
            mRs = logIn(userName, pwd);
        }
        retryLogin = 0;

        return mRs;
    }

    /**
     * 学生模块，注销登录
     *
     * @param userName 用户名
     */
    public static Rs_LogOut logOut(String userName) {

        String url = Config.FunctionalURL.STUDENT_LOGOUT;

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userName);

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_LogOut mRs = new Rs_LogOut(status);

        if (EXC_500 == status && retryLogout < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryLogout++;
            mRs = logOut(userName);
        }
        retryLogout = 0;

        return mRs;
    }


    /**
     * 学生模块，修改密码
     *
     * @param userName 用户名
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     */
    public static Rs_UpdatePwd updatePassWord(String userName, String oldPwd,
                                              String newPwd) {

        String url = Config.FunctionalURL.STUDENT_UPDATE_PWD;

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userName);
        params.put("oldpassword", oldPwd);
        params.put("newpassword", newPwd);

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_UpdatePwd mRs = new Rs_UpdatePwd(status, userName, oldPwd, newPwd);

        if (EXC_500 == status && retryUpdatePassWord < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryUpdatePassWord++;
            mRs = updatePassWord(userName, oldPwd,
                    newPwd);
        }

        retryUpdatePassWord = 0;

        return mRs;


    }

    /**
     * 知识重温，学习模式，获取所有教材,含章节信息
     *
     * @param tokenID 登录标识
     */
    public static Rs_CWXX_BooksWithChapters cwxx_getAllBooksWithChapters(
            String tokenID) {

        String url = Config.FunctionalURL.CWXX_ALL_BOOKS_WHIT_CHAPTER;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);
        List<BookWithChapters> bookList = new ArrayList<BookWithChapters>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {

            JsonArray books = GsonUtil.getJsonArray(jsonObj, "textBooks");
            JsonObject book;
            JsonArray chapters;
            JsonObject chapter;

            List<Chapter> chapterList;
            for (int i = 0; i < books.size(); i++) {
                book = GsonUtil.getJsonObject(books, i);
                chapters = GsonUtil.getJsonArray(book, "chapters");

                int bookReviewTotal = 0;
                int bookTestTotal = 0;

                chapterList = new ArrayList<Chapter>();
                for (int j = 0; j < chapters.size(); j++) {
                    chapter = GsonUtil.getJsonObject(chapters, j);

                    int chapterReviewTotal = GsonUtil.getInt(
                            chapter, "denomenatorReview", 0);
                    int chapterTestTotal = GsonUtil.getInt(
                            chapter, "denomenatorStudy", 0);

                    chapterList.add(new Chapter(GsonUtil
                            .getInt(chapter, "c_id", 0), GsonUtil.getString(
                            chapter, "chapterName", "未知名称"), chapterReviewTotal, chapterTestTotal));

                    bookReviewTotal += chapterReviewTotal;
                    bookTestTotal += chapterTestTotal;
                }

                bookList.add(new BookWithChapters(GsonUtil.getInt(book, "Id", DEF_NUM),
                        GsonUtil.getString(book, "name", DEF_STR), chapterList, bookReviewTotal, bookTestTotal));

            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_CWXX_BooksWithChapters mRs = new Rs_CWXX_BooksWithChapters(status, bookList);

        if (EXC_500 == status && retryCwxx_getAllBooksWithChapters < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryCwxx_getAllBooksWithChapters++;
            mRs = cwxx_getAllBooksWithChapters(
                    tokenID);
        }
        retryCwxx_getAllBooksWithChapters = 0;

        return mRs;

    }

    /**
     * 知识重温，根据章节id获取对应的章节内容
     *
     * @param chapterID 章节ID
     * @param tokenID   登录标识
     */
    public static Rs_CW_ChapterContents cw_getChapterContents(String chapterID,
                                                              String tokenID) {

        String url = Config.FunctionalURL.CW_CONTENT_OF_CHAPTER;

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", chapterID);
        params.put("token_id", tokenID);

        List<ChapterContent> chapterContentList = new ArrayList<ChapterContent>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {
            JsonArray contents = GsonUtil.getJsonArray(jsonObj,
                    "chapterContents");
            JsonObject content;
            for (int i = 0; i < contents.size(); i++) {
                content = GsonUtil.getJsonObject(contents, i);
                chapterContentList
                        .add(new ChapterContent(GsonUtil.getInt(content, "Id", DEF_NUM), GsonUtil.getString(content, "title", DEF_STR),
                                GsonUtil.getString(content, "content", DEF_STR)));
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_CW_ChapterContents mRs = new Rs_CW_ChapterContents(status, chapterContentList);

        if (EXC_500 == status && retryCw_getChapterContents < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryCw_getChapterContents++;
            mRs = cw_getChapterContents(chapterID,
                    tokenID);
        }
        retryCw_getChapterContents = 0;

        return mRs;


    }

    /**
     * 废弃，学习模式，根据 章节id和试题类型 获取试题
     *
     * @param chapterID 章节ID
     * @param testType  试题类型:0 单选题,1 多选题,2 判断题
     * @param tokenID   登录标识
     */
    public static Rs_Questions_NotGroup xx_getQuestions(String chapterID,
                                                        String testType, String tokenID) {

        String url = Config.FunctionalURL.XX_QUESTIONS_OF_CHAPTER_BY_TYPE;


        Map<String, String> params = new HashMap<String, String>();
        params.put("id", chapterID);
        params.put("testsType", testType);
        params.put("token_id", tokenID);

        List<Question> questionList = new ArrayList<Question>(); // 先做空值实例化，避免请求失败时list=null

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {

            JsonArray questions = GsonUtil.getJsonArray(jsonObj, "testss");
            questionList = getQuestions(questions, "Id");
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_Questions_NotGroup mRs = new Rs_Questions_NotGroup(status, questionList);

        if (EXC_500 == status && retryXx_getQuestions < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryXx_getQuestions++;
            mRs = xx_getQuestions(chapterID,
                    testType, tokenID);
        }
        retryXx_getQuestions = 0;

        return mRs;

    }

    /**
     * 学习模式，根据章节id查找该章节所有试题， 试题根据类型分组（头部button切换显示）
     *
     * @param chapterID 章节ID
     * @param tokenID   登录标识
     */
    public static Rs_Questions_GroupByType xx_getQuestionsGroupByType(
            String chapterID, String tokenID) {

        String url = Config.FunctionalURL.XX_QUESTIONS_OF_CHAPTER_GROUP_BY_TYPE;

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", chapterID);
        params.put("token_id", tokenID);

        List<Question> singleChoiceQuestionList = new ArrayList<Question>();
        List<Question> multipleChoiceQuestionList = new ArrayList<Question>();
        List<Question> trueOrFalseQuestionList = new ArrayList<Question>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());


        if (200 == status) {// 请求成功，获取各种试题

            JsonObject questionGroups = GsonUtil.getJsonObject(jsonObj,
                    "testss");
            JsonArray singleChoiceQuestions = GsonUtil.getJsonArray(
                    questionGroups, "single");
            JsonArray multipleChoiceQuestions = GsonUtil.getJsonArray(
                    questionGroups, "multiple");
            JsonArray trueOrFalseQuestions = GsonUtil.getJsonArray(
                    questionGroups, "judge");

            singleChoiceQuestionList = getQuestions(singleChoiceQuestions, "Id");
            multipleChoiceQuestionList = getQuestions(multipleChoiceQuestions,
                    "id");
            trueOrFalseQuestionList = getQuestions(trueOrFalseQuestions, "id");
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;


        Rs_Questions_GroupByType mRs = new Rs_Questions_GroupByType(status, singleChoiceQuestionList,
                multipleChoiceQuestionList, trueOrFalseQuestionList);

        if (EXC_500 == status && retryXx_getQuestionsGroupByType < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryXx_getQuestionsGroupByType++;
            mRs = xx_getQuestionsGroupByType(
                    chapterID, tokenID);
        }
        retryXx_getQuestionsGroupByType = 0;

        return mRs;

    }

    /**
     * 闯关模式，获取所有教材
     *
     * @param tokenID 登录标识
     */
    public static Rs_CG_Books cg_getBooks(String tokenID) {

        String url = Config.FunctionalURL.CG_ALL_BOOKS;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);

        List<Book> bookList = new ArrayList<Book>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {
            JsonArray books = GsonUtil.getJsonArray(jsonObj, "textBooks");
            JsonObject book;
            for (int i = 0; i < books.size(); i++) {
                book = GsonUtil.getJsonObject(books, i);
                bookList.add(new Book(GsonUtil.getInt(book, "Id", DEF_NUM), GsonUtil
                        .getString(book, "name", DEF_STR)));
            }
        }


        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_CG_Books mRs = new Rs_CG_Books(status, bookList);

        if (EXC_500 == status && retryCg_getBooks < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryCg_getBooks++;
            mRs = cg_getBooks(tokenID);
        }
        retryCg_getBooks = 0;

        return mRs;

    }

    /**
     * 闯关模式，根据教材ID获取该教材的所有章节（即关卡）
     *
     * @param bookID  教材ID
     * @param tokenID 登录标识
     */
    public static Rs_CG_Chapters cg_getChapters(String bookID, String tokenID) {

        String url = Config.FunctionalURL.CG_CHAPTERS_OF_BOOK;

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", bookID);
        params.put("token_id", tokenID);

        List<Chapter> chapterList = new ArrayList<Chapter>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());


        if (200 == status) {
            JsonArray chapters = GsonUtil.getJsonArray(jsonObj, "chapters");
            JsonObject chapterStr;

            for (int i = 0; i < chapters.size(); i++) {
                chapterStr = GsonUtil.getJsonObject(chapters, i);

                Chapter chapter = new Chapter(GsonUtil.getInt(chapterStr, "Id", DEF_NUM),
                        GsonUtil.getString(chapterStr, "name", DEF_STR));

                chapterList.add(chapter);
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_CG_Chapters mRs = new Rs_CG_Chapters(status, chapterList);

        if (EXC_500 == status && retryCg_getChapters < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryCg_getChapters++;
            mRs = cg_getChapters(bookID, tokenID);
        }
        retryCg_getChapters = 0;

        return mRs;

    }

    /**
     * 闯关模式，根据章节id查找该章节所有试题
     *
     * @param chapterID 章节ID
     * @param tokenID   登录标识
     */
    public static Rs_Questions_NotGroup cg_getQuestionsOfChapter(
            String chapterID, String tokenID) {

        String url = Config.FunctionalURL.CG_QUESTIONS_OF_CHAPTER;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);
        params.put("id", chapterID);

        List<Question> questionList = new ArrayList<Question>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {

            JsonArray questions = GsonUtil.getJsonArray(jsonObj, "testss");
            questionList = getQuestions(questions, "Id");
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_Questions_NotGroup mRs = new Rs_Questions_NotGroup(status, questionList);

        if (EXC_500 == status && retryCg_getQuestionsOfChapter < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryCg_getQuestionsOfChapter++;
            mRs = cg_getQuestionsOfChapter(
                    chapterID, tokenID);
        }
        retryCg_getQuestionsOfChapter = 0;

        return mRs;

    }

    /**
     * 考试模式，获取所有考试教材(学生考过的和未考过的，即含分数标识)
     *
     * @param tokenID 登录标识
     */
    public static Rs_KS_Books ks_getAllBooksWithScore(String tokenID) {

        String url = Config.FunctionalURL.KS_ALL_BOOKS_WITH_SCORE;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);

        List<BookWithScore> bookList = new ArrayList<BookWithScore>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());


        if (200 == status) {
            JsonArray books = GsonUtil.getJsonArray(jsonObj, "textBooks");
            JsonObject book;
            for (int i = 0; i < books.size(); i++) {
                book = GsonUtil.getJsonObject(books, i);

                String bookName = GsonUtil.getString(book, "name", DEF_STR);
                //TODO  Id/id（考试） 会变
                int bookId = GsonUtil.getInt(book, "Id", -100);
                if (bookId == -100) {
                    bookId = GsonUtil.getInt(book, "id", -100);
                }
                int numTotal = GsonUtil.getInt(book, "denominator", DEF_NUM);
                long timeTotal = GsonUtil.getLong(book, "time", DEF_NUM) * 60000;
                int score = GsonUtil.getInt(book, "score", DEF_NUM);

                BookWithScore bk = new BookWithScore(bookId, bookName, numTotal, timeTotal, score);

                bookList.add(bk);
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_KS_Books mRs = new Rs_KS_Books(status, bookList);

        if (EXC_500 == status && retryKs_getAllBooksWithScore < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryKs_getAllBooksWithScore++;
            mRs = ks_getAllBooksWithScore(tokenID);
        }
        retryKs_getAllBooksWithScore = 0;

        return mRs;

    }

    /**
     * 考试模式，根据教材id查找相应的考试试题
     *
     * @param bookID  教材ID
     * @param tokenID 登录标识
     */
    public static Rs_Questions_NotGroup ks_getQuestionsOfBook(String bookID,
                                                              String tokenID) {

        String url = Config.FunctionalURL.KS_QUESTIONS_OF_BOOK;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);
        params.put("id", bookID);

        List<Question> questionList = new ArrayList<Question>();
        long time = 0;

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());


        if (200 == status) {
            JsonArray questions = GsonUtil.getJsonArray(jsonObj, "testss");
            questionList = getQuestions(questions, "Id");
            time = GsonUtil.getInt(jsonObj, "time", DEF_NUM) * 60000;
        }


        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_Questions_NotGroup mRs = new Rs_Questions_NotGroup(status, questionList, time);

        if (EXC_500 == status && retryKs_getQuestionsOfBook < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryKs_getQuestionsOfBook++;
            mRs = ks_getQuestionsOfBook(bookID,
                    tokenID);
        }
        retryKs_getQuestionsOfBook = 0;

        return mRs;

    }

    /**
     * 考试模式，提交整套考试结果（答案和分数）
     *
     * @param bookID  教材ID
     * @param answers 答案
     * @param score   成绩
     * @param tokenID 登录标识
     */
    public static Rs_KS_ExamCommit ks_examCommit(String bookID, String answers,
                                                 int score, String tokenID) {

        String url = Config.FunctionalURL.KS_EXAM_COMMIT;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);
        params.put("id", bookID);
        params.put("answers", answers);
        params.put("score", score + "");

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_KS_ExamCommit mRs = new Rs_KS_ExamCommit(status, bookID,
                answers, score);

        if (EXC_500 == status && retryKs_examCommit < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retryKs_examCommit++;
            mRs = ks_examCommit(bookID, answers,
                    score, tokenID);
        }
        retryKs_examCommit = 0;

        return mRs;

    }

    /**
     * 废弃，视频，获取所有课程
     *
     * @param tokenID 登录标识
     */
    public static Rs_SP_Courses sp_getAllCourse(String tokenID) {

        String url = Config.FunctionalURL.SP_ALL_COURSE;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);

        List<Course> courseList = new ArrayList<Course>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());


        NameValuePair[] nameValuePairs = new NameValuePair[]{new NameValuePair(
                "token_id", tokenID)};

        if (200 == status) {
            JsonArray courses = GsonUtil.getJsonArray(jsonObj, "courses");
            JsonObject course;
            for (int i = 0; i < courses.size(); i++) {
                course = GsonUtil.getJsonObject(courses, i);
                courseList.add(new Course(GsonUtil.getInt(course, "Id", DEF_NUM),
                        GsonUtil.getString(course, "name", DEF_STR), GsonUtil.getString(
                        course, "summary", DEF_STR), GsonUtil.getDouble(course,
                        "price", DEF_NUM)));
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_SP_Courses mRs = new Rs_SP_Courses(status, courseList);

        if (EXC_500 == status && retrySp_getAllCourse < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retrySp_getAllCourse++;
            mRs = sp_getAllCourse(tokenID);
        }
        retrySp_getAllCourse = 0;

        return mRs;

    }

    /**
     * 视频，获取所有课程，含购买状态
     *
     * @param tokenID 登录标识
     */
    public static Rs_SP_CoursesWithBuyStatus sp_getAllCourseWithBuyStatus(
            String tokenID) {

        String url = Config.FunctionalURL.SP_ALL_COURSE_WITH_BUY_STATUS;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);

        List<CourseWithBuyStatus> courseList = new ArrayList<CourseWithBuyStatus>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {

            JsonArray courses = GsonUtil.getJsonArray(jsonObj, "courses");
            JsonObject course;
            for (int i = 0; i < courses.size(); i++) {
                course = GsonUtil.getJsonObject(courses, i);
                courseList.add(new CourseWithBuyStatus(GsonUtil.getInt(course,
                        "Id", DEF_NUM), GsonUtil.getString(course, "name", DEF_STR), GsonUtil
                        .getString(course, "summary", DEF_STR), GsonUtil.getDouble(course,
                        "price", DEF_NUM), GsonUtil.getInt(course, "IsBuy", DEF_NUM)));
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_SP_CoursesWithBuyStatus mRs = new Rs_SP_CoursesWithBuyStatus(status, courseList);

        if (EXC_500 == status && retrySp_getAllCourseWithBuyStatus < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retrySp_getAllCourseWithBuyStatus++;
            mRs = sp_getAllCourseWithBuyStatus(
                    tokenID);
        }
        retrySp_getAllCourseWithBuyStatus = 0;

        return mRs;

    }

    /**
     * 视频，根据课程ID获取该课程所有视频
     *
     * @param tokenID  登录标识
     * @param courseID 课程ID
     */
    public static Rs_SP_Videos sp_getVideosOfCourse(String courseID,
                                                    String tokenID) {

        String url = Config.FunctionalURL.SP_VEDIO_OF_COURSE;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", tokenID);
        params.put("id", courseID);

        List<Video> videoList = new ArrayList<Video>();

        HttpResult rs = HttpUtil.post(url, params);

        JsonObject jsonObj = GsonUtil.toJsonObject(rs.getContent());
        int status = GsonUtil.getInt(jsonObj, "status", rs.getStatus());

        if (200 == status) {

            JsonArray videos = GsonUtil.getJsonArray(jsonObj, "courseDetailss");
            JsonObject video;
            for (int i = 0; i < videos.size(); i++) {
                video = GsonUtil.getJsonObject(videos, i);

                videoList.add(new Video(GsonUtil.getInt(video, "Id", DEF_NUM), GsonUtil
                        .getString(video, "name", DEF_STR), GsonUtil.getString(video,
                        "image_url", null), GsonUtil.getString(video, "video_url", null)));
            }
        }

        if (HttpUtil.REQUEST_EXCEPTION == status)
            status = REQUEST_EXCEPTION;

        Rs_SP_Videos mRs = new Rs_SP_Videos(status, videoList);
        if (EXC_500 == status && retrySp_getVideosOfCourse < RETRY_MAX) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retrySp_getVideosOfCourse++;
            mRs = sp_getVideosOfCourse(courseID,
                    tokenID);
        }
        retrySp_getVideosOfCourse = 0;

        return mRs;

    }

    /**
     * 将json 数组格式的试题 封装成list试题集合
     *
     * @param questions 试题json数组
     * @param IDKey     ID的key字符串(因为传过来的判断题和其他的IDkey不一样)
     * @return
     */
    private static List<Question> getQuestions(JsonArray questions, String IDKey) {
        List<Question> questionList = new ArrayList<Question>();

        JsonObject question;
        for (int i = 0; i < questions.size(); i++) {
            question = GsonUtil.getJsonObject(questions, i);

            String[] optionArr = null;
            // 选项处理
            if (question.has("options")) {// 选项题，单选/多选
                JsonArray options = GsonUtil.getJsonArray(question, "options");

                int optionSize = options.size();
                optionArr = new String[optionSize];

                for (int j = 0; j < optionSize; j++)
                    optionArr[j] = GsonUtil.getString(options, j, DEF_STR);

            } else {// 判断题，json不带选项，自己补
                optionArr = new String[]{"正 确", "错 误"};
            }

            questionList.add(new Question(GsonUtil.getInt(question, IDKey, DEF_NUM),
                    GsonUtil.getInt(question, "testsType", DEF_NUM), GsonUtil.getInt(
                    question, "score", DEF_NUM), GsonUtil.getString(question,
                    "testsContent", DEF_STR), optionArr, GsonUtil.getString(
                    question, "answer", DEF_STR), GsonUtil.getString(question,
                    "explain", DEF_STR)));
        }

        return questionList;
    }

    /**
     * 本类方法测试
     */
    private static void test() {

        String userName = "456";
        String pwd = EnDecodeUtil.MD5InLowerCase("123");// (old)pwd=202cb962ac59075b964b07152d234b70
        String newPwd = EnDecodeUtil.MD5InLowerCase("3214");// newPwd=89d4402dc03d3b7318bbac10203034ab

        String tokenID = "";
        String bookID = "3";
        String chapterID = "1";
        String testType = "1";
        String courseID = "1";

        System.out.println("-----------登录--------------");

        Rs_LogIn loginResult = logIn(userName, pwd);
        tokenID = loginResult.getToken_id();

        System.out.println(loginResult.getStatusMsg());
        System.out.println(loginResult.getUserName());
        System.out.println(loginResult.getRealName());
        System.out.println(loginResult.getPassWord());
        System.out.println(loginResult.getToken_id());


        System.out.println("-----------注销--------------");
        Rs_LogOut logOutResult = logOut(userName);

        System.out.println(logOutResult.getStatusMsg());

        System.out.println("-----------重新登录--------------");

        Rs_LogIn loginResult1 = logIn(userName, pwd);
        tokenID = loginResult1.getToken_id();

        System.out.println(loginResult1.getStatusMsg());
        System.out.println(loginResult1.getUserName());
        System.out.println(loginResult1.getRealName());
        System.out.println(loginResult1.getPassWord());
        System.out.println(loginResult1.getToken_id());

        System.out.println("--------------------------");

        System.out.println("tokenID=" + tokenID);
        System.out.println("pwd=" + pwd);
        System.out.println("newPwd=" + newPwd);

        System.out.println("-----------修改密码--------------");

        Rs_UpdatePwd updatePwdResult = updatePassWord(userName, pwd, newPwd);
        System.out.println(updatePwdResult.getStatusMsg());

        System.out.println("-----------修改密码（密码改回来）--------------");

        Rs_UpdatePwd updatePwdResult1 = updatePassWord(userName, newPwd,
                pwd);
        System.out.println(updatePwdResult1.getStatusMsg());

        System.out.println("-----------知识重温、学习模式，获取所有教材，含章节--------------");

        Rs_CWXX_BooksWithChapters rs_books = cwxx_getAllBooksWithChapters(tokenID);
        System.out.println(rs_books.getStatusMsg());
        for (BookWithChapters book : rs_books.getBookList()) {
            System.out.println(book.getBookID());
            System.out.println(book.getBookName());
            for (Chapter chapter : book.getChapterList()) {
                System.out.println(chapter.getChapterID());
                System.out.println(chapter.getChapterName());
            }
        }

        System.out.println("--------知识重温获取章节内容------------------");

        Rs_CW_ChapterContents chapterContent = cw_getChapterContents(
                chapterID, tokenID);
        System.out.println(chapterContent.getChapterContentList().size());
        for (int i = 0; i < chapterContent.getChapterContentList().size(); i++) {
            System.out.println(chapterContent.getChapterContentList()
                    .get(i).getContentID());
            System.out.println(chapterContent.getChapterContentList()
                    .get(i).getContentTile());
            System.out.println(chapterContent.getChapterContentList()
                    .get(i).getContent());
        }

        System.out.println("--废弃，学习模式:根据章节和试题类型获取某一类型的试题--------------");

        Rs_Questions_NotGroup questionsInOneType = xx_getQuestions(
                chapterID, testType, tokenID);
        System.out.println(questionsInOneType.getStatusMsg());

        for (Question question : questionsInOneType.getQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());
            for (String option : question.getOptions()) {
                System.out.println(option);
            }
            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("-------学习模式，按章节按类型分类的试题--------------");

        Rs_Questions_GroupByType group = xx_getQuestionsGroupByType(
                chapterID, tokenID);
        System.out.println(group.getStatusMsg());
        System.out.println("单选题------------");
        for (Question question : group.getSingleChoiceQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());

            System.out.println("选项：");
            for (String option : question.getOptions()) {
                System.out.println(option);
            }

            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("多选题------------");
        for (Question question : group.getMultipleChoiceQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());

            System.out.println("选项：");
            for (String option : question.getOptions()) {
                System.out.println(option);
            }

            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("判断题------------");

        for (Question question : group.getTrueOrFalseQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());

            System.out.println("选项：");
            for (String option : question.getOptions()) {
                System.out.println(option);
            }

            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("--------闯关模式获取所有教材，不含章节-----------------");

        Rs_CG_Books cg_allBooks = cg_getBooks(tokenID);
        System.out.println(cg_allBooks.getBookList().size());
        for (int i = 0; i < cg_allBooks.getBookList().size(); i++) {
            System.out
                    .println(cg_allBooks.getBookList().get(i).getBookID());
            System.out.println(cg_allBooks.getBookList().get(i)
                    .getBookName());
        }

        System.out.println("--------闯关模式章节(关卡)------------------");

        Rs_CG_Chapters chaptersResult = cg_getChapters(bookID, tokenID);
        System.out.println(chaptersResult.getChapterList().size());
        for (int i = 0; i < chaptersResult.getChapterList().size(); i++) {
            System.out.println(chaptersResult.getChapterList().get(i)
                    .getChapterID());
            System.out.println(chaptersResult.getChapterList().get(i)
                    .getChapterName());
        }

        System.out.println("-----闯关模式，根据章节类型混合的试题--------------");

        Rs_Questions_NotGroup rs_Questions_NotGroup = cg_getQuestionsOfChapter(
                chapterID, tokenID);
        System.out.println(rs_Questions_NotGroup.getStatusMsg());

        for (Question question : rs_Questions_NotGroup.getQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());
            for (String option : question.getOptions()) {
                System.out.println(option);
            }
            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("---------考试模式--获取所有考试教材-含分数-------------");

        Rs_KS_Books rs_KS_Books = ks_getAllBooksWithScore(tokenID);
        System.out.println(rs_KS_Books.getStatusMsg());
        for (BookWithScore book1 : rs_KS_Books.getBookList()) {
            System.out.println(book1.getBookID());
            System.out.println(book1.getBookName());
            System.out.println(book1.getScore());
        }

        System.out.println("----------考试模式--某一教材的试题--------------");

        Rs_Questions_NotGroup questionsOfBook = ks_getQuestionsOfBook(
                bookID, tokenID);
        ;
        System.out.println(questionsOfBook.getStatusMsg());

        for (Question question : questionsOfBook.getQuestionList()) {
            System.out.println(question.getID());
            System.out.println(question.getType());
            System.out.println(question.getScore());
            System.out.println(question.getQuestionContent());
            for (String option : question.getOptions()) {
                System.out.println(option);
            }
            System.out.println(question.getAnswer());
            System.out.println(question.getExplain());
        }

        System.out.println("------------考试结果提交结果--------------");

        Rs_KS_ExamCommit rs_KS_examCommit = ks_examCommit(bookID, "A", 100,
                tokenID);
        System.out.println(rs_KS_examCommit.getStatusMsg());
        System.out.println(rs_KS_examCommit.getBookID());
        System.out.println(rs_KS_examCommit.getAnswers());
        System.out.println(rs_KS_examCommit.getScore());

        System.out.println("-------废弃--获取所有有视频的课程，不含购买状态--------------");

        Rs_SP_Courses rs_courses = sp_getAllCourse(tokenID);
        System.out.println(rs_courses.getStatusMsg());

        for (Course course : rs_courses.getCourseList()) {
            System.out.println(course.getCourseID());
            System.out.println(course.getCourseName());
            System.out.println(course.getSummary());
            System.out.println(course.getPrice());

        }

        System.out.println("-----------获取所有有视频的课程，含购买状态--------------");

        Rs_SP_CoursesWithBuyStatus rs_coursesWithStatus = sp_getAllCourseWithBuyStatus(tokenID);
        System.out.println(rs_coursesWithStatus.getStatusMsg());

        for (CourseWithBuyStatus course : rs_coursesWithStatus
                .getCourseList()) {
            System.out.println(course.getCourseID());
            System.out.println(course.getCourseName());
            System.out.println(course.getSummary());
            System.out.println(course.getPrice());
            System.out.println(course.getBuyStatus());

        }

        System.out.println("-----------获取某一课程所有视频--------------");

        Rs_SP_Videos rs_videos = sp_getVideosOfCourse(courseID, tokenID);
        System.out.println(rs_videos.getStatusMsg());

        for (Video video : rs_videos.getVideoList()) {
            System.out.println(video.getVideoID());
            System.out.println(video.getVideoName());
            System.out.println(video.getImageUrl());
            System.out.println(video.getVideoUrl());

        }


    }

    public static void main(String[] args) {
        test();
    }
}
