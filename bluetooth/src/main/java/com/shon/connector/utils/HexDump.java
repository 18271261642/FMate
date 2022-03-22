package com.shon.connector.utils;

import android.util.Log;

import com.shon.connector.Config;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clone of Android's HexDump class, for use in debugging. Cosmetic changes
 * only.十六进制数据操作
 */
public class HexDump {
    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * 4个byte转int，不够补0
     * @param high_h
     * @param high_l
     * @param low_h
     * @param low_l
     * @return
     */
    public static   int getIntFromBytes(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }

    public static   int getIntFromBytes( byte low_h, byte low_l) {
        return (low_h & 0xff) << 8 | low_l & 0xff;
    }

    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();

        byte[] line = new byte[16];
        int lineIndex = 0;

        result.append("\n0x");
        result.append(toHexString(offset));

        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == 16) {
                result.append(" ");

                for (int j = 0; j < 16; j++) {
                    if (line[j] > ' ' && line[j] < '~') {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(".");
                    }
                }

                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }

            byte b = array[i];
            result.append(" ");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

            line[lineIndex++] = b;
        }

        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            count++;
            for (int i = 0; i < count; i++) {
                result.append(" ");
            }

            for (int i = 0; i < lineIndex; i++) {
                if (line[i] > ' ' && line[i] < '~') {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }
        }

        return result.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
            buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
        }

        return new String(buf);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static String toHexString(short i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = new byte[1];
        array[0] = b;
        return array;
    }

    public static byte[] toByteArray(int i) {
        byte[] array = new byte[4];

        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);

        return array;
    }

    public static byte[] toByteArray(long i) {
        byte[] array = new byte[4];

        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);

        return array;
    }

    public static byte[] toByteArrayLength(int i, int size) {
        byte[] array = new byte[size];
        for (int j = 0; j < size; j++) {
            array[j] = (byte) ((i >> 8 * (size - j - 1)) & 0xFF);
        }
        return array;
    }


    public static byte[] minusToByteArray(int i){
        byte[] array = new byte[2];
        array[0] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) (i & 0xFF);

        array[0] = 1;
        return array;
    }



    /**
     * @param i
     * @return 返回俩位的数组
     */
    public static byte[] toByteArrayTwo(int i) {
        byte[] array = new byte[2];

        array[1] = (byte) (i & 0xFF);
        array[0] = (byte) ((i >> 8) & 0xFF);

        return array;
    }

    public static byte[] toByteArray(short i) {
        byte[] array = new byte[2];

        array[1] = (byte) (i & 0xFF);
        array[0] = (byte) ((i >> 8) & 0xFF);

        return array;
    }

    public static byte[] int2Byte2(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 24) & 0xff);
        bytes[1] = (byte) ((i >> 16) & 0xff);
        bytes[2] = (byte) ((i >> 8) & 0xff);
        bytes[3] = (byte) (i & 0xff);
        return bytes;
    }

    private static int toByte(char c) {
        if (c >= '0' && c <= '9')
            return (c - '0');
        if (c >= 'A' && c <= 'F')
            return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f')
            return (c - 'a' + 10);

        throw new RuntimeException("Invalid hex char '" + c + "'");
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
                    .charAt(i + 1)));
        }
        return buffer;
    }


    /**
     * md5加密方法
     *
     * @param info 密码源
     * @return
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 异或和
     */

    private static final char[] bcdLookup = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;


    public static String XORAnd(byte[] bytes) {
        int str = 0;

        for (int i = 0; i < bytes.length; i++) {
            str ^= Integer.valueOf((bcdLookup[(bytes[i] >>> MESSAGE_DEVICE_NAME) & 15] + "" + bcdLookup[bytes[i] & 15] + ""), 16);
        }
        return Integer.toHexString(str);
    }

    public static byte[] getSameBytes(byte[] byteA, int i) {
        int no = byteA.length - i;
        byte[] b = new byte[i];
        for (int j = 0; j < i; j++) {
            b[j] = byteA[no + j];
        }
        return b;
    }

    public static byte[] getMergeBytes(byte[] pByteA, byte[] pByteB) {
        int numA = pByteA.length;
        int numB = pByteB.length;
        byte[] b = new byte[numA + numB];
        for (int i = 0; i < numA; i++) {
            b[i] = pByteA[i];
        }
        for (int i = 0; i < numB; i++) {
            b[numA + i] = pByteB[i];
        }
        return b;
    }

//	public static String PassWordHexString(String pw) {
//		String password = null;
//		for (int i = 0; i < pw.length(); i+=2) {
//			if (i == 0) {
//				password = "0" + pw.charAt(i);
//			} else {
//				password = password + "0" + pw.charAt(i);
//			}
//		}
//		return password;
//	}

    public static byte[] PassWordHexString(String hexString) {
        Log.e("拼接的值", hexString);
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
                    .charAt(i + 1)));
            TLog.Companion.error("keya+=" + buffer[i]);
            TLog.Companion.error("ksssseya+=" + buffer[i] / 2);
        }
        return buffer;
    }

    /**
     * 俩个数组合并
     *
     * @param bt1 头
     * @param bt2 处理数据
     * @return 返回的新数组
     */
    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        int i = 0;
        for (byte bt : bt1) {
            bt3[i] = bt;
            i++;
        }

        for (byte bt : bt2) {
            bt3[i] = bt;
            i++;
        }
        return bt3;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2, byte[] bt3, byte[] bt4, byte[] bt5) {
        byte[] btArray = new byte[bt1.length + bt2.length + bt3.length + bt4.length + bt5.length];
        int i = 0;
        for (byte bt : bt1) {
            btArray[i] = bt;
            i++;
        }
        for (byte bt : bt2) {
            btArray[i] = bt;
            i++;
        }
        for (byte bt : bt3) {
            btArray[i] = bt;
            i++;
        }
        for (byte bt : bt4) {
            btArray[i] = bt;
            i++;
        }
        for (byte bt : bt5) {
            btArray[i] = bt;
            i++;
        }
        return btArray;
    }

    public static byte[] byteMerger(byte[] bt1, byte bt2) {
        byte[] bt3 = new byte[bt1.length + 1];
        int i = 0;
        for (byte bt : bt1) {
            bt3[i] = bt;
            i++;
        }
        bt3[i] = bt2;
        return bt3;
    }

    public static byte[] byteMerger(byte bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt2.length + 1];
        int i = 0;
        bt3[i] = bt1;
        i++;
        for (byte bt : bt2) {
            bt3[i] = bt;
            i++;
        }

        return bt3;
    }

    /**
     * 16转10进制
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        //16进制转成16进制  当如果你手动输入的值为10时 转成16进制应该是a
        hexString = Integer.toHexString(Integer.parseInt(hexString, 16));
        if (hexString.length() <= 1)//当输入的月日时分秒为0开头时加0组成01 02这种格式
            hexString = "0" + hexString;
        TLog.Companion.error("添加过后的hexString+=" + hexString);
//		int d = Integer.valueOf("ff", 16);
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * @param data 要异或的数据
     * @return 最终返回的异或数据结果
     */
    public static byte byteXOR(byte[] data) {
        byte byteXor = 0x00;
        for (int i = 0; i < data.length; i++) {
            byteXor = (byte) (byteXor ^ data[i]);

        }

        return (byte) (byteXor & 0XFF);
    }

    /**
     * 取字节低5位[4:0] 并位移8位
     */
    public static int getXocInt(byte xocByte) {
        int result = xocByte & Config.LOWER_FIVE;
        return result << 8;

    }

    public static int getXocInt(byte xocByte, byte key) {
        int result = xocByte & key;
        return result << 16;
    }

    /**
     * c-byte数组转换成java-int值 <br/> 高位在前 <br/> [24 - 16 - 08 - 00]
     *
     * @param buffer byte数组
     * @param index  转换开始位置
     * @param len    转换的长度
     */
    public static int byte2intHigh(byte[] buffer, int index, int len) {
        if (buffer == null || index + len > buffer.length) {
            return 0;
        }
        int value = 0;
        for (int i = 0, j = len - 1; i < len; i++, j--) {
            value += (byte2Int(buffer[i + index]) << (j * 8));
        }
        return value;
    }

    /**
     * c无符号的值，转换成java-int值
     */
    public static int byte2Int(byte byteNum) {
        return byteNum & 0xff;
    }


    /**
     * 将字符串转换为 unicode 字符.
     *
     * @param src 要转换的字符串.
     * @return byte[] 字符串的字节数组.
     * @throws NullPointerException     输入的字符串为空时，抛出该异常.
     * @throws IllegalArgumentException 转义字符如 \t,在做为参数是应该是 \\t,否则程序报异常.
     */
    public static byte[] string2Unicode(String src)
            throws NullPointerException, IllegalArgumentException {
        if (src == null) {
            throw new NullPointerException("the parameter of src is Null");
        }

        // 定义常量: 空格
        final String SPACE = " ";
        // 定义常量: 16 进制
        final int RADIX_HEX = 16;

        if ("".equals(src)) {
            src = src + SPACE;
        }

        // 字符串长度
        final int len = src.length();
        // 字符串转换为字符数组
        char[] utfChar = src.toCharArray();
        System.out.println("utfChar length = " + utfChar.length);
        String hexStr = "";
        // 返回值（unicode字符编码是两个字节）
        byte[] result = new byte[len * 2];

        for (int i = 0; i < len; i++) {
            // 转换为16进制
            hexStr = Integer.toHexString(utfChar[i]);
            // System.out.println("hexStr = " + hexStr);
            // 英文字符需要在前面补加数字 00
            if (hexStr.length() > 1 && hexStr.length() <= 2) {
                hexStr = "00" + hexStr;
            } else { // 如果转义字符写错（如\t应该是\\t），报此异常
                // continue;
                throw new IllegalArgumentException(
                        "the parameter of src is error, and you'd better check escaped character.");
            }
            // 截取字符串
            String head = hexStr.substring(0, 2);
            String tail = hexStr.substring(2, 4);
            try {
                // 16进制数转换为byte
                result[i * 2] = (byte) Integer.parseInt(head, RADIX_HEX);
                result[i * 2 + 1] = (byte) Integer.parseInt(tail, RADIX_HEX);
            } catch (NumberFormatException nfe) {
                continue;
            }
        }

        return result;
    }

    /**
     * unicode 转字符数组
     */
    public static char[] unicode2Chars(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        char[] cs = new char[hex.length];

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            cs[i] = (char) data;
        }

        return cs;
    }

    private byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);

        return bb.array();

    }


    public static String getUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append(String.format("\\u%04x", Integer.valueOf(c)));
        }

        return unicode.toString();
    }

    /**
     * 把byte[]转成string类型的byte数组
     *
     * @param b
     * @return
     */

    public static String bytesToString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (byte temp : b) {
            sb.append(String.format("%02x", temp));
        }
        return sb.toString();
    }

    /**
     * 将string类型的byte数组转成bytep[]
     *
     * @param str
     * @return
     */
    public static byte[] stringToByte(String str) {
        byte[] data = new byte[str.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str.substring(0 + i * 2, 2 + i * 2), 16).byteValue();
        }
        return data;
    }

   public static List<Integer> GetBitStatu(int num)
    {
        List<Integer> mWeekList=new ArrayList();
        for (int i = 0; i <8 ; i++) {
            TLog.Companion.error(i+"i++++"+ ((num & (1 << i))&1));
            TLog.Companion.error(i+"++++"+ ((num >>i)&1));
//            if( (num & (1 << i))==1)
//            {
//                TLog.Companion.error("i+"+i+"==1");
//                mWeekList.add(1);
//               // return 1;
//            }
//            else {
//                mWeekList.add(0);
//                TLog.Companion.error("i+"+i+"==0");
//               // return 0;
//            }
        }
        return mWeekList;
    }
 }
