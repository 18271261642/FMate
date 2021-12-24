package com.shon.net.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Date : 2020/7/22 10:02
 * Package name : net.yt.whale.net.util
 * Des :
 */
public final class RequestBodyUtil {

    /**
     * 创建Json类型的body
     *
     * @param jsonArray JSONArray
     * @return RequestBody
     */
    public static RequestBody createJson(JSONArray jsonArray) {
        return createJson(jsonArray.toString());
    }

    /**
     * 创建Json类型的body
     *
     * @param jsonObject JSONObject
     * @return RequestBody
     */
    public static RequestBody createJson(JSONObject jsonObject) {
        return createJson(jsonObject.toString());
    }

    /**
     * 创建Json类型的body
     *
     * @param string String
     * @return RequestBody
     */
    public static RequestBody createJson(String string) {
        return RequestBody.create(string, MediaType.parse("application/json; charset=utf-8"));
    }


    public static RequestBody createFileFormData(File file) {
        return RequestBody.create(file, MediaType.parse("multipart/form-data"));
    }

    public static MultipartBody.Part createBodyPart(File file) {
        RequestBody requestFile = createFileFormData(file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }
}
