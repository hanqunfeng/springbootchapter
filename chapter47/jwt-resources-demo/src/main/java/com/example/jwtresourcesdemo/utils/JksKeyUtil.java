package com.example.jwtresourcesdemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>jks密钥证书</h1>
 * Created by hanqf on 2020/11/9 14:23.
 */
@Slf4j
public class JksKeyUtil {
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
                StringBuffer tStringBuffer = new StringBuffer();
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
        KeyStore store = null;
        Resource resource;
        if (filePath.startsWith("classpath:")) {
            resource = new ClassPathResource(filePath.replace("classpath:", ""));
        } else {
            resource = new PathResource(Paths.get(filePath));
        }

        try {
            synchronized (JksKeyUtil.class) {
                if (store == null) {
                    synchronized (JksKeyUtil.class) {
                        store = KeyStore.getInstance("jks");
                        inputStream = resource.getInputStream();
                        store.load(inputStream, storePassword.toCharArray());
                    }
                }
            }
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, keyPassword.toCharArray());
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
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
                KeyFactory factory = KeyFactory.getInstance("RSA");
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
            ByteBuffer bytes = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

}
