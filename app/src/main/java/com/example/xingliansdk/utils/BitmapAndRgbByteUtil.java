package com.example.xingliansdk.utils;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

import com.google.gson.Gson;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;


/**
     * bitmap and rgb bytes dual transfer
     *
     * @author guochao
     * @version 1.0
     * @since 2019/12/12
     */
    public class BitmapAndRgbByteUtil {

        private static final String TAG = BitmapAndRgbByteUtil.class.getSimpleName();
        public static final int RGB_DATA_WIDTH_OR_HEIGHT = 512;
        private static int mOriginWidth;
        private static int mOriginHeight;

        /**
         * 单通道数组转bitmap
         */
        public static Bitmap rgb2BitmapFor123(byte[] data, int width, int height) {
            long startTime = System.currentTimeMillis();
            try {

                int[] colors = convertByteToColor123(data);//取RGB值转换为int数组
                if (colors == null) {
                    return null;
                }
                return Bitmap.createBitmap(colors, 0, width, width, height,
                        Bitmap.Config.ARGB_8888);

            } catch (Exception e) {

            } finally {

            }
            return null;
        }


        /**
         * 三通道数组转3通道bitmap
         */
        public static Bitmap rgb2BitmapFor323(byte[] data, int width, int height) {
            long startTime = System.currentTimeMillis();
            try {

                int[] colors = convertByteToColor323(data);//取RGB值转换为int数组
                if (colors == null) {
                    return null;
                }
                return Bitmap.createBitmap(colors, 0, width, width, height,
                        Bitmap.Config.ARGB_8888);

            } catch (Exception e) {

            } finally {

            }
            return null;
        }

        /**
         * 将一个byte数转成int
         * 实现这个函数的目的是为了将byte数当成无符号的变量去转化成int
         *
         * @param data byte字节
         */
        private static int convertByteToInt(byte data) {
            int heightBit = (int) ((data >> 4) & 0x0F);
            int lowBit = (int) (0x0F & data);
            return heightBit * 16 + lowBit;
        }


        /**
         * 将纯RGB数据数组转化成int像素数组
         *
         * @param data rgb数组 输入为单通道,输出为3通道
         */
        public static int[] convertByteToColor123(byte[] data) {
            int size = -1;
            if (data != null) {
                size = data.length;
            }
            if (size == 0) {
                return null;
            }
            int arg = 0;
            if (size % 3 != 0) {
                arg = 1;
            }
            // 一般RGB字节数组的长度应该是3的倍数，
            // 不排除有特殊情况，多余的RGB数据用黑色0XFF000000填充
            int[] color = new int[size + arg];
            int red, green, blue;
            int colorLen = color.length;
            if (arg == 0) {
                for (int i = 0; i < colorLen; ++i) {
                    red = convertByteToInt(data[i]);
                    green = convertByteToInt(data[i]);
                    blue = convertByteToInt(data[i]);
                    // 获取RGB分量值通过按位或生成int的像素值
                    color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
                }
            } else {
                for (int i = 0; i < colorLen - 1; ++i) {
                    red = convertByteToInt(data[i]);
                    green = convertByteToInt(data[i]);
                    blue = convertByteToInt(data[i]);
                    color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
                }
                color[colorLen - 1] = 0xFF000000;
            }
            return color;
        }


        /**
         * 将纯RGB数据数组转化成int像素数组，转三通道
         *
         * @param data rgb数组 输入为三通道
         */
        private static int[] convertByteToColor323(byte[] data) {
            int size = -1;
            if (data != null) {
                size = data.length;
            }
            if (size == 0) {
                return null;
            }
            int arg = 0;
            if (size % 3 != 0) {
                arg = 1;
            }
            // 一般RGB字节数组的长度应该是3的倍数，
            // 不排除有特殊情况，多余的RGB数据用黑色0XFF000000填充
            int[] color = new int[size / 3 + arg];
            int red, green, blue;
            int colorLen = color.length;
            if (arg == 0) {
                for (int i = 0; i < colorLen; ++i) {
                    red = convertByteToInt(data[i * 3]);
                    green = convertByteToInt(data[i * 3 + 1]);
                    blue = convertByteToInt(data[i * 3 + 2]);
                    // 获取RGB分量值通过按位或生成int的像素值
                    color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
                }
            } else {
                for (int i = 0; i < colorLen - 1; ++i) {
                    red = convertByteToInt(data[i * 3]);
                    green = convertByteToInt(data[i * 3 + 1]);
                    blue = convertByteToInt(data[i * 3 + 2]);
                    color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
                }
                color[colorLen - 1] = 0xFF000000;
            }
            return color;
        }


        /**
         * 将bitmap 转换为RGB数组（三通道）
         * @param bitmap
         * @return
         */
        public static byte[] bitmap2RGBData(Bitmap bitmap) {

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] intValues = new int[width * height];
            bitmap.getPixels(intValues, 0, width, 0, 0, width,
                    height);
            byte[] rgb = new byte[width * height * 3];
            byte[] rgbByte = new byte[width * height * 2];
            for (int i = 0; i < intValues.length; ++i) {
                final int val = intValues[i];
//                TLog.Companion.error("val=="+val);
//                rgb[i * 3] = (byte) ((val>>3 << 11));//R
//                rgb[i * 3 + 1] = (byte) ((val>>2 << 5));//G
//                rgb[i * 3 + 2] = (byte) (val>>3);//B
                rgb[i * 3] = (byte) ((val<<8) &0xF8);//R
                rgb[i * 3 + 1] = (byte) ((val<<3) & 0xFC);//G
                rgb[i * 3 + 2] = (byte) (val >>3);//B
//                TLog.Companion.error(" rgb[i * 3]+="+ rgb[i * 3]);
//                TLog.Companion.error(" rgb[i * 3]+="+ rgb[i * 3+1]);
//                TLog.Companion.error(" rgb[i * 3]+="+ rgb[i * 3+2]);
//                    int colorValue=
//                int A = (val >> 24) & 0xff;
//                int R = (val >> 16) & 0xff;
//                int G = (val >> 8) & 0xff;
//                int B = val & 0xff;
//                rgb[i++] = (byte) R;
//                rgb[i++] = (byte) G;
//                rgb[i++] = (byte) B;
//                rgb[i++] = (byte) A;
            }
            for (int i = 0; i < intValues.length; i++) {
                int clr = intValues[i];
                int red = (clr & 0x00ff0000) >> 16; // 取高两位
                int green = (clr & 0x0000ff00) >> 8; // 取中两位
                int blue = clr & 0x000000ff; // 取低两位
                red = red >> 3;
                green = green >> 2;
                blue = blue >> 3;
                red = red << 11;
                green = green << 5;
                int color = red + green + blue;
                rgbByte[i * 2]= (byte) (color>>8);
                rgbByte[i * 2+1]= (byte) (color&0x00ff);
            }
            TLog.Companion.error("==="+rgbByte.length);
            return   rgbByte;
        }

    /**
     * 把像素数组转化成565的像素集合
     *
     * @param pixels
     * @param size
     * @return
     */
    public static byte[] getHexPixels(int[] pixels, int size) {
        ArrayList<String> totalPixels = new ArrayList<String>(size);
        byte[] byteList = new byte[size * 2];
        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16; // 取高两位
            int green = (clr & 0x0000ff00) >> 8; // 取中两位
            int blue = clr & 0x000000ff; // 取低两位
            red = red >> 3;
            green = green >> 2;
            blue = blue >> 3;
            red = red << 11;
            green = green << 5;
            int color = red + green + blue;
            byteList[i * 2]= (byte) (color>>8);
            byteList[i * 2+1]= (byte) (color&0x00ff);
           // totalPixels.add(color>>8);
          //  totalPixels.add(coh);
            if(color>0xff)
            {
             //   TLog.Companion.error(" color=="+color+"  red=== "+red+" green==="+green+" blue==="+blue);
                if(color>0xffff)
                {
                    TLog.Companion.error(" color=="+color);
                }
            }
            String coh = Integer.toHexString(color);

            if (coh.length() < 4) {
                for (int j = 0; coh.length() < 4; j++) {
                    coh = "0" + coh;
                }
            }
            //转成16进制颜色数据
            //coh="0x"+coh;
            totalPixels.add(coh);

        }
        TLog.Companion.error("totalPixels+="+byteList.length);
        TLog.Companion.error("totalPixels+="+ByteUtil.getHexString(byteList));
        return byteList;
    }
        public static Bitmap resizeTo512(Bitmap bm) {
            mOriginWidth = bm.getWidth();
            mOriginHeight = bm.getHeight();
            TLog.Companion.error(
                    "resizeTo512 origin width: " + mOriginWidth + ", height:" + mOriginHeight);
            // 设置想要的大小
            int newWidth = RGB_DATA_WIDTH_OR_HEIGHT;
            int newHeight = RGB_DATA_WIDTH_OR_HEIGHT;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / mOriginWidth;
            float scaleHeight = ((float) newHeight) / mOriginHeight;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            return Bitmap.createBitmap(bm, 0, 0, mOriginWidth, mOriginHeight, matrix, true);
        }

        public static Bitmap resize512ToOrigin(Bitmap bm) {

            // 获取之前图片的原始比例
            int newWidth = mOriginWidth;
            int newHeight = mOriginHeight;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / RGB_DATA_WIDTH_OR_HEIGHT;
            float scaleHeight = ((float) newHeight) / RGB_DATA_WIDTH_OR_HEIGHT;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            return Bitmap.createBitmap(bm, 0, 0, RGB_DATA_WIDTH_OR_HEIGHT,
                    RGB_DATA_WIDTH_OR_HEIGHT, matrix, true);
        }

    /**
     * 布局生成一块新的图片
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
       // v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    }

