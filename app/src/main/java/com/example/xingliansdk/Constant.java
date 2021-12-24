package com.example.xingliansdk;

import android.os.Environment;

import java.io.File;

public class Constant {
    public static String baseUrl="http://47.107.53.249:8089";
    public static class Path {

        /**
         * sd卡根目录
         */
        public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        /**
         * 项目根目录
         */
        public static final String ROOT_PATH = mkdirs(SDCARD +  "/test_map" );

        /**
         * 图片缓存位置
         */
        public static final String CACHE_IMAGE = mkdirs(ROOT_PATH + "/cache_image");

        /**
         * 下载Apk缓存
         */
        public static final String CACHE_APP_DOWNLOAD = mkdirs(ROOT_PATH + "/cache_app_download");
        /**
         * OTA文件下载地址
         */
        public static final String CACHE_OTA_DOWNLOAD = mkdirs(ROOT_PATH + "/cache_ota_download");

        /**
         * 崩溃日志保存位置
         */
        public static final String CACHE_LOG = mkdirs(ROOT_PATH + "/cache_log");

        /**
         * 分享图片保存位置
         */
        public static final String CACHE_SHARE_IMAGE = mkdirs(ROOT_PATH + "/cache_share");

        /**
         * 地图缓存位置
         */
        public static final String CACHE_MAP = mkdirs(ROOT_PATH + "/cache_map");
        /**
         * 地图轨迹文件位置
         */
        public static final String CACHE_MAP_GPX_PATH = mkdirs(ROOT_PATH + "/cache_map/gpx");

        /**
         * 远程拍照存储的位置
         */
        public static final File CAMERA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        private static String mkdirs(String path) {
            File file = new File(path);
            if (file.isFile()) {
                if (!file.getParentFile().exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                }
            } else if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
            }
            return path;
        }

    }


}
