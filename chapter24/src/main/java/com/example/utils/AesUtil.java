package com.example.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <p></p>
 * Created by hanqf on 2020/9/2 13:28.
 */


public class AesUtil {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";// 默认的加密算法
    private static final String CHARSETNAME = "UTF-8";
    private static final String DEFAULT_PASSWORD = "ABCDEFG"; //默认密码

    /**
     * AES 加密操作
     *
     * @param content  待加密内容
     * @param password 加密密码
     * @return String 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            // 创建密码器
            final Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 设置为UTF-8编码
            final byte[] byteContent = content.getBytes(CHARSETNAME);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            // 加密
            final byte[] result = cipher.doFinal(byteContent);
            // 通过Base64转码返回
            return Base64.encodeBase64String(result);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.fillInStackTrace());
        }
    }

    public static String encrypt(String content) {
        return encrypt(content, DEFAULT_PASSWORD);
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param password
     * @return String
     */
    public static String decrypt(String content, String password) {
        try {
            // 实例化
            final Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            // 执行操作
            final byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            // 采用UTF-8编码转化为字符串
            return new String(result, CHARSETNAME);
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.fillInStackTrace());
        }
    }

    public static String decrypt(String content) {
        return decrypt(content, DEFAULT_PASSWORD);
    }

    /**
     * 生成加密秘钥
     *
     * @param password 加密的密码
     * @return SecretKeySpec
     */
    private static SecretKeySpec getSecretKey(final String password) {
        // 返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            // AES 要求密钥长度为 128，192，256
            //因为某些国家的进口管制限制，Java发布的运行环境包中的加解密有一定的限制。比如默认不允许256位密钥的AES加解密。
            //Java 8 Update 151 (8u151) 以后可以通过如下方法修改限制
            //1.在%JDK_HOME%\jre\lib\security\java.security文件中设置属性crypto.policy=unlimited
            //2.或者在代码中指定：Security.setProperty("crypto.policy", "unlimited");

            //这里没做任何处理，查看java.security发现，默认值就是不限制,java version "1.8.0_211"
            kg.init(256, secureRandom);
            // 生成一个密钥
            final SecretKey secretKey = kg.generateKey();
            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex.fillInStackTrace());
        }
    }

    public static void main(String[] args) {
        final String password = "abc-efg-uio-ppp";
        String str = "哈哈哈哈呵呵呵呵额";
        String encryptStr = encrypt(str, password);
        System.out.println(encryptStr);
        System.out.println(decrypt(encryptStr, password));
    }
}
