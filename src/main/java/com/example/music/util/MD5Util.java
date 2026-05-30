package com.example.music.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 密码加密工具类
 */
public class MD5Util {

    /**
     * 对字符串进行 MD5 加密
     *
     * @param str 明文密码
     * @return 32位小写 MD5 密文
     */
    public static String md5(String str) {
        try {
            // 获取MD5加密对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 加密
            byte[] digest = md.digest(str.getBytes());
            // 转成32位十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
