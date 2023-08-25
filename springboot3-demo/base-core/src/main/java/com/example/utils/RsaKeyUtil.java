package com.example.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 根据文件地址 得到公钥 私钥
 *
 * @author
 */
public class RsaKeyUtil {

    private static final int DEFAULT_KEY_SIZE = 2048;

    private static final String KEY_ALGORITHM = "RSA";

    public static KeyPair getKeyPair(String privateKeyfilePath, String publicKeyfilePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final PrivateKey privateKey = getPrivateKey(privateKeyfilePath);
        final PublicKey publicKey = getPublicKey(publicKeyfilePath);
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 从文件中读取公钥
     *
     * @param filename 公钥保存路径，相对于classpath
     * @return 公钥对象
     * @throws Exception
     */
    public static PublicKey getPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = readFile(filename);
        return getPublicKey(bytes);
    }

    /**
     * 从文件中读取密钥
     *
     * @param filename 私钥保存路径，相对于classpath
     * @return 私钥对象
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = readFile(filename);
        return getPrivateKey(bytes);
    }

    /**
     * 获取公钥
     *
     * @param bytes 公钥的字节形式
     * @return
     */
    private static PublicKey getPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePublic(spec);
    }

    /**
     * 获取密钥
     *
     * @param bytes 私钥的字节形式
     * @return
     */
    private static PrivateKey getPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePrivate(spec);
    }

    /**
     * 根据密文，生存rsa公钥和私钥,并写入指定文件
     *
     * @param publicKeyFilename  公钥文件路径
     * @param privateKeyFilename 私钥文件路径
     * @param secret             生成密钥的密文
     * @param keySize            keySize
     */
    public static void generateKey(String publicKeyFilename, String privateKeyFilename, String secret, int keySize) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        keyPairGenerator.initialize(Math.max(keySize, DEFAULT_KEY_SIZE), secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // 获取公钥并写出
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        publicKeyBytes = Base64.getEncoder().encode(publicKeyBytes);
        writeFile(publicKeyFilename, publicKeyBytes);
        // 获取私钥并写出
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        privateKeyBytes = Base64.getEncoder().encode(privateKeyBytes);
        writeFile(privateKeyFilename, privateKeyBytes);
    }

    private static byte[] readFile(String fileName) throws IOException {
        if (fileName.startsWith("classpath:")) {
            fileName = fileName.replace("classpath:", "");
            Resource resource = new ClassPathResource(fileName);

            //打成jar后不能将classpath下的文件直接读取成文件
            //File file = resource.getFile();

            //使用如下方式可以在打成jar后读取到流
            try (InputStream inputStream = resource.getInputStream()) {
                return toByteArray(inputStream);
            }
        }
        return Files.readAllBytes(new File(fileName).toPath());
    }

    /**
     * InputStream转化为byte[]数组
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        }
    }

    private static void writeFile(String destPath, byte[] bytes) throws IOException {
        File dest = new File(destPath);
        if (!dest.exists()) {
            dest.createNewFile();
        }
        Files.write(dest.toPath(), bytes);
    }


    /**
     * 解密
     *
     * @param data 已加密数据
     * @param key  密钥，公钥或私钥
     * @return
     */
    public static byte[] decryptByKey(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥，公钥或私钥
     * @return
     */
    public static byte[] encryptByKey(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        String publicKeyFilename = "id_key_rsa.pub";
        String privateKeyFilename = "id_key_rsa";
        String secret = "123456";
        generateKey(publicKeyFilename, privateKeyFilename, secret, DEFAULT_KEY_SIZE);


        final PublicKey publicKey = getPublicKey(publicKeyFilename);
        final PrivateKey privateKey = getPrivateKey(privateKeyFilename);

        //公钥加密，私钥解密
        byte[] bytes = encryptByKey("hello 世界你好".getBytes(StandardCharsets.UTF_8), publicKey);

        String s = new String(decryptByKey(bytes, privateKey), StandardCharsets.UTF_8);
        System.out.println(s);

        //私钥加密，公钥解密
        bytes = encryptByKey("hello 世界你好".getBytes(StandardCharsets.UTF_8), privateKey);

        s = new String(decryptByKey(bytes, publicKey), StandardCharsets.UTF_8);
        System.out.println(s);


    }
}
