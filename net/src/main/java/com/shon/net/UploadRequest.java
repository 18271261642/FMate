//package com.shon.net;
//
//import com.shon.net.BaseApi;
//import com.shon.net.UploadApi;
//import com.shon.net.callback.CommonCallback;
//
//
//import java.util.Map;
//import java.util.concurrent.Executors;
//
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * Auth : xiao.yunfei
// * Date : 2020/7/29 11:26
// * Package name : net.yt.whale.net
// * Des : 文件上传
// * <p>
// * 包含：
// * 上传单个文件，多个文件
// * 包含参数的单文件、多文件上传
// * <p>
// * 使用方式：
// * <p>
// * UploadRequest uploadRequest = new UploadRequest("http://xxxx.xxx.xxx/ssss/");
// * uploadRequest.upLoadSingleFile(file, new CommonCallback<BaseResult<String>>() {
// *
// *     @Override
// *     public void onSuccess(BaseResult<String> baseResult) {
// *     }
// *     @Override
// *     public void onError(int errorCode, String msg) {
// *     }
// * });
// */
//public final class UploadRequest extends BaseApi<UploadApi> {
//
//
//    public String token = "";
//
//
//
//    /**
//     * 上传文件，无需token
//     *
//     * @param uploadPath 上传路径
//     */
//    public UploadRequest(String uploadPath) {
//        super();
//        mRetrofit = new Retrofit.Builder()
//                .baseUrl(uploadPath)
//                .client(getOkHttpClient())
//                .addConverterFactory(GsonConverterFactory.create())
//                .callbackExecutor(Executors.newSingleThreadExecutor())
//                .build();
//    }
//
//    @Override
//    public OkHttpClient getOkHttpClient() {
//        OkHttpClientBuild okHttpClientBuild = new OkHttpClientBuild();
//        return okHttpClientBuild.getBaseBuild().build();
//    }
//
//
//    /**
//     * 单文件上传
//     *
//     * @param part part
//     */
//    public void upLoadSingleFile(String url,MultipartBody.Part part , CommonCallback<BaseResult<?>> commonCallback) {
//        Call<BaseResult<?>> call = getApiInterface(token).upLoadSingleFile(url,part);
//        call.enqueue(commonCallback);
//    }
//
//
//    /**
//     * 带参数的单文件上传
//     *
//     * @param requestBody requestBody
//     * @param part        part
//     */
//    public void upLoadSingleFile(String url,RequestBody requestBody, MultipartBody.Part part, CommonCallback<BaseResult<?>> commonCallback) {
//        Call<BaseResult<?>> call = getApiInterface(token).upLoadSingleFile(url,requestBody, part);
//        call.enqueue(commonCallback);
//    }
//
//    /**
//     * 多文件上传
//     *
//     * @param files files
//     */
//    public void upLoadFiles(String url,Map<String, RequestBody> files, CommonCallback<BaseResult<?>> commonCallback) {
//        Call<BaseResult<?>> call = getApiInterface(token).upLoadFiles(url,files);
//        call.enqueue(commonCallback);
//    }
//
//    /**
//     * 多文件上传
//     *
//     * @param requestBody requestBody
//     * @param files       files
//     */
//    public void upLoadFiles(String url,RequestBody requestBody, Map<String, RequestBody> files, CommonCallback<BaseResult<?>> commonCallback) {
//        Call<BaseResult<?>> call = getApiInterface(token).upLoadFiles(url,requestBody, files);
//        call.enqueue(commonCallback);
//    }
//
//
//}