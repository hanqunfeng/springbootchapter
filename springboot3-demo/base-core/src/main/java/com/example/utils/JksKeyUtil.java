package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>jks密钥证书</h1>
 * Created by hanqf on 2020/11/9 14:23.
 */
@Slf4j
public class JksKeyUtil {
    private static final String KEY_ALGORITHM = "RSA";
    private static Pattern PEM_DATA = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL);


    /**
     * 将一个输入流转化为字符串
     *
     * @param tInputStream
     * @return
     */
    private static String getStreamToStr(InputStream tInputStream) {
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuilder tStringBuffer = new StringBuilder();
                String sTempOneLine;
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * <h2>密钥对</h2>
     * Created by hanqf on 2020/11/9 15:03. <br>
     *
     * @param filePath      jks密钥证书路径
     * @param alias         密钥别名
     * @param storePassword 密钥库密码
     * @param keyPassword   密钥密码
     * @return java.security.KeyPair
     * @author hanqf
     */
    public static KeyPair getKeyPair(String filePath, String alias, String storePassword, String keyPassword) {
        InputStream inputStream = null;
        KeyStore store;
        Resource resource;
        if (filePath.startsWith("classpath:")) {
            resource = new ClassPathResource(filePath.replace("classpath:", ""));
        } else {
            resource = new PathResource(Paths.get(filePath));
        }

        try {
            store = KeyStore.getInstance("JKS");
            inputStream = resource.getInputStream();
            store.load(inputStream, storePassword.toCharArray());

            RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, keyPassword.toCharArray());
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(spec);
            return new KeyPair(publicKey, key);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + resource, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("Cannot close open stream: ", e);
            }
        }
    }

    /**
     * <h2>根据公钥文件获取PublicKey</h2>
     * Created by hanqf on 2020/11/9 16:07. <br>
     *
     * @param publicKeyFilePath
     * @return java.security.PublicKey
     * @author hanqf
     */
    public static PublicKey getPublicKey(String publicKeyFilePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource;
        if (publicKeyFilePath.startsWith("classpath:")) {
            resource = new ClassPathResource(publicKeyFilePath.replace("classpath:", ""));
        } else {
            resource = new PathResource(Paths.get(publicKeyFilePath));
        }

        try (InputStream inputStream = resource.getInputStream()) {
            String streamToStr = getStreamToStr(inputStream);
            Matcher m = PEM_DATA.matcher(streamToStr.trim());

            if (!m.matches()) {
                throw new IllegalArgumentException("String is not PEM encoded data");
            }
            String type = m.group(1);
            if (type.equals("PUBLIC KEY")) {
                final byte[] bytes = Base64.getDecoder().decode(utf8Encode(m.group(2)));

                X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
                KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
                return factory.generatePublic(spec);
            }
            return null;
        }


    }

    /**
     * UTF-8 encoding/decoding. Using a charset rather than `String.getBytes` is less forgiving
     * and will raise an exception for invalid data.
     */
    private static byte[] utf8Encode(CharSequence string) {
        try {
            ByteBuffer bytes = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建jks密钥文件
     */
    public static void generateKey(String filePath, String alias, String storePassword, String keyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        X509Certificate certificate = generateCertificate(publicKey, privateKey);
        keyStore.setKeyEntry(alias, privateKey, keyPassword.toCharArray(), new X509Certificate[]{certificate});

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        keyStore.store(fileOutputStream, storePassword.toCharArray());
        fileOutputStream.close();
    }

    private static X509Certificate generateCertificate(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(new X500Name("CN=Test Certificate"),
                BigInteger.valueOf(System.currentTimeMillis()), new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365),
                new X500Name("CN=Test Certificate"),
                subPubKeyInfo);

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .build(privateKey);

        X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(x509CertificateHolder);
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
        String jksFile = "jks_key.jks";
        generateKey(jksFile, "jks", "123456", "123456");

        final KeyPair keyPair = getKeyPair(jksFile, "jks", "123456", "123456");
        final PublicKey publicKey = keyPair.getPublic();
        final PrivateKey privateKey = keyPair.getPrivate();


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
