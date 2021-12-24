package com.shon.net.ssl;

import android.annotation.SuppressLint;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Date : 2020/7/21 11:59
 * Package name : net.yt.whale.net.ssl
 * Des :
 */
public class TrustAllHostnameVerifier implements HostnameVerifier {
    @SuppressLint("BadHostnameVerifier")
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
