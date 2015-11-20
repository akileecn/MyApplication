package cn.aki.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/11/19.
 * md5加密工具类
 */
public class Md5Utils {
    /**
     * MD5加密
     */
    public static String encode(String code){
        try {
            MessageDigest digest=MessageDigest.getInstance("MD5");
            byte[] bytes;
            bytes=digest.digest(code.getBytes());
            StringBuilder sb=new StringBuilder();
            for(byte b:bytes){
                int i=b&0x0f;
                String hex=Integer.toHexString(i);
                if(hex.length()<2){
                    hex="0"+hex;
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
