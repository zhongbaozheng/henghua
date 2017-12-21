package com.hengdian.henghua.utils;

import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.model.NameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public static final String TAG = "HttpUtil";

    public static final int REQUEST_EXCEPTION = -1;

    public static final String HTTP_CHARSET = "utf-8";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset="+HTTP_CHARSET);
    public static MediaType MEDIA_TYPE_ENCODED = MediaType.parse("application/x-www-form-urlencoded");

    private OkHttpClient okHttpClient;

    static class HttpResult {
        private int status = -1;
        public String content = null;

        public HttpResult() {
            super();
        }

        public HttpResult(int status, String content) {
            super();
            this.status = status;
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public String getContent() {
            return content;
        }
    }

    public static HttpResult get(String url, Map<String, String> paramMap,
                                 Map<String, String> RequestProperty) {

        return new HttpResult(0, "");
    }




    private static int tryCounter = 0;//不计url的粗略计数

    /**
     * HTTP POST请求
     *
     * @param urlPath   url
     * @param paramsMap 参数
     * @return IO异常返回HttpResult中status = -1,content=null
     */
    public static HttpResult post(String urlPath, Map<String, String> paramsMap) {
        MEDIA_TYPE_ENCODED = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

//        InputStreamReader isReader = null;
//        BufferedReader responseBr = null;

        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            StringBuilder sbd = new StringBuilder();
            //封装参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    sbd.append("&");
                }
                sbd.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String params = sbd.toString();
            LogUtil.d(TAG, "Param: " + params);

            //生成请求体
            RequestBody rqstBody = RequestBody.create(MEDIA_TYPE_ENCODED, params);

            //创建请求 设置请求属性
            Request request = new Request.Builder()
                    .url(urlPath)
                    .post(rqstBody)
                    .addHeader("accept","*/*")
                    .addHeader("Connection","Keep-Alive")
                    .addHeader("charset",HTTP_CHARSET)
                    .build();

            Call call = okHttpClient.newCall(request);

            //同步请求
            Response response = call.execute();
            //sbd.delete(0,sbd.length());
            //sbd = new StringBuilder();

//            if(HttpURLConnection.HTTP_OK == response.code()){
//
//                isReader = new InputStreamReader(response.body().byteStream(), HTTP_CHARSET);
//                responseBr = new BufferedReader(isReader);
//
//                String line;
//                while ((line = responseBr.readLine()) != null) {
//                    sbd.append(line).append("\n");
//                }
//            }
            String content = null;
            String rspTmp = response.body().string();
            if(HttpURLConnection.HTTP_OK != response.code() && tryCounter < 3){
                content = post( urlPath,  paramsMap).content;
                tryCounter++;
            }else {
                content = rspTmp;//sbd.toString();
                tryCounter = 0;
            }

            LogUtil.i(TAG,"Response Code: " + response.code());
            LogUtil.e(TAG,"Response Content: 【"+ rspTmp+"】");

            return new HttpResult(response.code(), content);

        } catch (IOException e) {
            LogUtil.e(TAG, "Http post failed" ,e);

            return new HttpResult();
        }finally {

           // IOCloseUtil.close(isReader);
           // IOCloseUtil.close(responseBr);
        }
    }
}

