package com.shon.net.interceptor;

import android.text.format.DateUtils;
import android.util.Log;

import com.shon.net.util.TimeUts;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by Admin
 * Date 2022/6/9
 */
public class LoggingInterceptor implements Interceptor {

    private static final String TAG = "日期拦截器";


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response =  chain.proceed(request);
        return logForResponse(response);
    }


    private Response logForResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.e(TAG,String.format("response url:%s,code:%s,time is:%s,headers:%s", clone.request().url(), clone.code(), TimeUts.getCurrTime(System.currentTimeMillis()) + "ms", clone.protocol()));

            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null && isText(mediaType)) {
                    String content = body.string();
                    Log.e(TAG,String.format("message:%s,contentType:%s,content is:%s,", clone.message(), mediaType.toString(),content ));
                    body = ResponseBody.create(mediaType, content);
                    return response.newBuilder().body(body).build();
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        }finally {


        }
        return response;

    }




    private void logForRequest(Request request){
        String url = request.url().toString();
        String method = request.method();
        Headers headers = request.headers();
        String headerStr = headers != null && headers.size() > 0 ? headers.toString() : "";
        String requestData = String.format("request url:%s,method:%s,\nheaders:%s", url, method, headerStr);
        Log.e(TAG,requestData);
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType mediaType = requestBody.contentType();
            if (mediaType != null && isText(mediaType)) {
                Log.e(TAG,String.format("requestBody mediaType:%s,bodyToString:%s"+ mediaType.toString()+ bodyToString(request)));
            }
        }

    }


    private String bodyToString(final Request request) {
        final Request copy = request.newBuilder().build();
        final Buffer buffer=new Buffer();
        try {
            copy.body().writeTo(buffer);
        } catch (IOException e) {
            return "something error,when show requestBody";
        }
        return buffer.readUtf8();
    }


    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")) {
                return true;
            }
        }
        return false;
    }

}
