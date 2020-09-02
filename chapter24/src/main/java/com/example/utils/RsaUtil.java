package com.example.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>RSA加解密工具类</p>
 * Created by hanqf on 2020/9/2 17:28.
 *
 * 非对称加密 加密速度慢，适合少量数据加密
 *
 *
 */


public class RsaUtil {

    /**
     * 默认公钥
    */
    private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOE/ZVi2Gp2vFI73C3v1ORMYWeHtqkjApYMGmNexZswLTcHbOOYHB7GE12QElchwGBACII3sc2lIvYOg+jR8F1l+m9HSj9kRIVrkAE/pjiMkdq0cG4rf/WzG32C6C2HKIzFom4NILFdnONVwQI0xTydK5P6ks0hw78vq5aLi473QIDAQAB";
    /**
     * 默认私钥
    */
    private static final String DEFAULT_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI4T9lWLYana8UjvcLe/U5ExhZ4e2qSMClgwaY17FmzAtNwds45gcHsYTXZASVyHAYEAIgjexzaUi9g6D6NHwXWX6b0dKP2REhWuQAT+mOIyR2rRwbit/9bMbfYLoLYcojMWibg0gsV2c41XBAjTFPJ0rk/qSzSHDvy+rlouLjvdAgMBAAECgYB+UclGLXJbrkmwaxJwjC8kfD8sYopKmifoWMLAA5vgfUVQHygxghT/nI/ify26kl4H77JS6J9+K3xaduTIa1fYfl0g/A2Q9di9TAZrKXvsvF7yVdFGtbcj04Gtpj3RWmKifz/Lm6ijKkoLhTL+5bM9fY1DLAjtuihFLiLg4e+bgQJBAMEORMsAukkoSdVaBTX2tlQFEMoeWxNueAvwto1yQstvdod9NFBmCKp2A6UO4porhlXT4/nY213bFdRlDANCXWkCQQC8Zr8fCnzxlRxwKKkpfNWhpeD1m+T2gbj3DMCA9eGyyVdyjSljNMQqQHgbX2+trnKaEd1EoCNn/FqGFE56/HhVAkAeeIoTIIJGmb1Nl2/LHK2ahVIpFLF7V6xK9itaF/bC9UzYBcMEDZbAkO/yVW8etk/1rAQHo1q8bZ/8bhZ/TTJBAkACPgbQS8Oo7GlmPFWnFMFGJz9CLRTQikK1TgWMg2CoTBUqW9u+PoaHKjVDviMKIiVjCAtmIbHKLwq0xdEZTZ/xAkEAgxQ3o57RTomN3e4C5zn7//WZF6c0yaSciiV+XKBWDYbL+7CvnwM7v8dvzaMMdZHtFHr4E5pGGfTvWTSeQgSA9Q==";

    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥

    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        genKeyPair();
        System.out.println("随机生成的公钥为:" + keyMap.get(0));
        System.out.println("随机生成的私钥为:" + keyMap.get(1));

        //加密字符串
        String message = "RSA加密与解密";
        String messageEn = encrypt(message, keyMap.get(0));
        System.out.println("加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn, keyMap.get(1));
        System.out.println("还原后的字符串为:" + messageDe);

    }

    /**
     * 随机生成密钥对，用于生成新的密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(java.util.Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(java.util.Base64.getEncoder().encode((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKeyString);  //0表示公钥
        keyMap.put(1, privateKeyString);  //1表示私钥
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = java.util.Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = java.util.Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    public static String encrypt(String str) throws Exception {
        return encrypt(str,DEFAULT_PUBLIC_KEY);
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = java.util.Base64.getDecoder().decode(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = java.util.Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static String decrypt(String str) throws Exception {
        return decrypt(str,DEFAULT_PRIVATE_KEY);
    }
}
