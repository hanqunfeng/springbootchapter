package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * <p>DES工具类</p>
 * Created by hanqf on 2020/9/2 10:30.
 *
 * 对称加密
 */


public class DesUtil {

    private static final Logger log = LoggerFactory.getLogger(DesUtil.class);

    // 设置密钥key
    private static final String DEFAULT_PASSWORD = "ABCDEFG";
    private static final String CHARSETNAME = "UTF-8";
    private static final String ALGORITHM = "DES";

    private static Key getSecretKey(final String password)  {
        try {
            // 生成DES算法对象
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            // 运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // 设置上密钥种子
            secureRandom.setSeed(DEFAULT_PASSWORD.getBytes());
            // 初始化基于SHA1的算法对象，DES算法有效密钥长度为56，默认就是56，所以可以不加长度参数
            //generator.init(secureRandom);
            generator.init(56,secureRandom);
            // 生成密钥对象
            return generator.generateKey();
        } catch (Exception e) {
            log.error("DESUtil 类加载 初始化加密对象异常");
            throw new RuntimeException(e);
        }
    }


    /**
     * <p>解密</p>
     *
     * @param srcData
     * @return java.lang.String
     * @author hanqf
     * 2020/9/2 10:30
     */
    public static String decrypt(String srcData,String password) {
        try {
            // 将字符串decode成byte[]
            byte[] bytes = java.util.Base64.getDecoder().decode(srcData);
            // 获取解密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            // 解密
            byte[] doFinal = cipher.doFinal(bytes);
            // 返回解密之后的信息
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            log.error("DESUtil 解密 Exception ：", e);
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String srcData) {
        return decrypt(srcData, DEFAULT_PASSWORD);
    }

    /**
     * <p>加密</p>
     *
     * @param srcData
     * @return java.lang.String
     * @author hanqf
     * 2020/9/2 10:31
     */
    public static String encrypt(String srcData,String password) {
        try {
            // 按UTF8编码
            byte[] bytes = srcData.getBytes(CHARSETNAME);
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            // 加密
            byte[] doFinal = cipher.doFinal(bytes);
            // byte[]to encode好的String并返回
            return java.util.Base64.getEncoder().encodeToString(doFinal);
        } catch (Exception e) {
            log.error("DESUtil 加密 Exception ：", e);
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String srcData) {
        return encrypt(srcData,DEFAULT_PASSWORD);
    }

    public static void main(String[] args) {
        final String password = "abc-efg-uio-ppp";
        String str = "哈哈哈哈呵呵呵呵额";
        String encryptStr = encrypt(str, password);
        System.out.println(encryptStr);
        System.out.println(decrypt(encryptStr, password));
    }
}
