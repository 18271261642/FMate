package com.example.xingliansdk.ui.fragment.map.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 功能:地图类型智能检测
 */

public class MapType {


    private static final String URL_A_MAP = "https://www.amap.com/";
    private static final String URL_GOOGLE_MAP = "https://www.google.com/";
    private static int callbackCount = 0;
    private static boolean isAMap;
    private static boolean isGoogleMap;
    private static TYPE mSmartType = TYPE.UNKNOWN;
    private static OnMapTypeCallback callback;

    public enum TYPE {
        /**
         * 未知
         */
        UNKNOWN,
        /**
         * 高德
         */
        A_MAP,
        /**
         * 谷歌
         */
        GOOGLE_MAP,

    }

    private static boolean isZH() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        if (language == null) {
            return false;
        }
        return "zh".equalsIgnoreCase(language) || "cn".equalsIgnoreCase(language);
    }

    public static TYPE getSmartMapType() {
        if (mSmartType == null) {
            mSmartType = TYPE.UNKNOWN;
        }
        //用户选择
        int selectMapType =  1; //MapStorage.getSelectMapType();
        switch (selectMapType) {
            case 0: /*智能选择*/
                //采用默认智能选择,如果智能选择失败 则mSmartType 取上一次的智能选择的结果
                if (mSmartType == TYPE.UNKNOWN) {
//                    switch (MapStorage.getLastSmartMapType()) {
//                        case -1:
//                        case 0:
                            mSmartType = TYPE.A_MAP;
//                            break;
//                        case 1:
//                            mSmartType = TYPE.GOOGLE_MAP;
//                            break;
//                    }
                }
                break;
            case 1: /*用户强制选择了高德*/
                mSmartType = TYPE.A_MAP;
                break;
            case 2: /*用户强制选择了谷歌*/
                mSmartType = TYPE.GOOGLE_MAP;
                break;
        }
        return mSmartType;
    }


    public static void refresh(final Context context,OnMapTypeCallback callback) {
        MapType.callback = callback;

        //是中文系统,同时谷歌play服务也没安装 先认为是国内
        if (isZH() && !isGooglePlayServicesAvailable(context)) {
            mSmartType = TYPE.A_MAP;
        }

        callbackCount = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isAMap = ping(URL_A_MAP);
                callbackCount++;
                checkType();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isGoogleMap = ping(URL_GOOGLE_MAP);
                callbackCount++;
                checkType();
            }
        }).start();
    }

    private static void checkType() {
        if (callbackCount >= 2) {
            if (isGoogleMap) {
                //如果谷歌可访问,优先选择谷歌
                mSmartType = TYPE.GOOGLE_MAP;
            } else if (isAMap) {
                //谷歌不可访问 则使用高德
                mSmartType = TYPE.A_MAP;
            }else{
                mSmartType = TYPE.UNKNOWN;
            }
            if (callback != null) {
                callback.callback(mSmartType);
            }
        }
    }

    /**
     * 能使用谷歌地图
     *
     * @param context
     * @return
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        try {
            int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context.getApplicationContext());
            return resultCode == ConnectionResult.SUCCESS;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 取得谷歌服务错误弹窗
     *
     * @param activity
     * @return
     */
    public static Dialog getGooglePlayServicesErrorDialog(Activity activity) {
        return GoogleApiAvailability.getInstance().getErrorDialog(activity, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.getApplicationContext()), 0);
    }

    public interface OnMapTypeCallback{
        void callback(TYPE type);
    }

    private static boolean ping(String uri) {
        try {
            HttpsURLConnection connection = getHttpsURLConnection(uri);
            if (connection == null) return false;
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30 * 1000);
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Throwable e) {

        }
        return false;
    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(String uri) throws IOException {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (ctx == null) return null;
        SSLSocketFactory ssf = ctx.getSocketFactory();

        URL url = new URL(uri);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(false);
        return httpsConn;
    }
}
