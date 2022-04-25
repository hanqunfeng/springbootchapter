package com.example.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * <h1>AES/CBC/PKCS5Padding</h1>
 * Created by hanqf on 2022/3/25 10:31.
 */


public class AES_CBC_Util {

    private static final byte[] key = "98681314656453429868131465645342".getBytes();//key.length须满足16的整数倍
    private static final byte[] iv = "9868131465645342".getBytes();//iv.length须满足16的整数倍

    //private static final byte[] key = new byte[32];//key.length须满足16的整数倍
    //private static final byte[] iv = new byte[16];//iv.length须满足16的整数倍


    //private static final String transform = "AES/CBC/PKCS5Padding";
    private static final String transform = "AES/CBC/PKCS7Padding";
    private static final String algorithm = "AES";
    private static final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);


    //支持PKCS7Padding
    static{
        try{
            Security.addProvider(new BouncyCastleProvider());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String encrypt(String content) {

        try {
            Cipher cipher = Cipher.getInstance(transform);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] cipherData = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(cipherData);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.fillInStackTrace());
        }
    }

    public static String decrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(transform);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] decbbdt = cipher.doFinal(java.util.Base64.getDecoder().decode(content));
            // 采用UTF-8编码转化为字符串
            return new String(decbbdt, StandardCharsets.UTF_8);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.fillInStackTrace());
        }
    }

    public static void main(String[] args) {
        String encrypt = AES_CBC_Util.encrypt("测试内容");
        System.out.println(encrypt);
        System.out.println(AES_CBC_Util.decrypt(encrypt));
    }

}
