package cn.aki.mobilesafe.utils;

import se.simbio.encryption.Encryption;

/**
 * Created by Administrator on 2016/1/8.
 * 加密工具
 */
public class EncryptUtils {
    private static final byte[] IV=new byte[]{-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};
    private static final Encryption encryption=Encryption.getDefault("akiKey", "akiSalt", IV);

    /**
     * 加密
     * @param source 源文件
     * @return 密文
     */
    public static String encrypt(String source){
        return encryption.encryptOrNull(source);
    }

    /**
     * 解密
     * @param source 密文
     * @return  源文件
     */
    public static String decrypt(String source){
        return encryption.decryptOrNull(source);
    }
}
