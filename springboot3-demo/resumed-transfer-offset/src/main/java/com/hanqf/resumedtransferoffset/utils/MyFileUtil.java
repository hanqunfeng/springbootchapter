package com.hanqf.resumedtransferoffset.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.Files;

/**
 * <h1>文件工具类</h1>
 * Created by hanqf on 2022/5/13 23:07.
 */

@Slf4j
public class MyFileUtil {

    /**
     * 获取文件MD5值
     */
    public static String getFileMD5(File file) {
        InputStream inputStream = fileToInputStream(file);
        String md5DigestAsHex = null;
        try {
            md5DigestAsHex = DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return md5DigestAsHex;
    }

    public static String getInputStreamMD5(InputStream inputStream) {
        String md5DigestAsHex = null;
        try {
            md5DigestAsHex = DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return md5DigestAsHex;
    }

    public static String getBytesMD5(byte[] bytes) {
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(bytes);
        return md5DigestAsHex;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static InputStream fileToInputStream(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            log.error("", e);
        }
        return inputStream;
    }

    public static byte[] fileToBytes(File file) {
        byte[] buffer = null;
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (Exception e) {
            log.error("", e);
        }
        return buffer;
    }

    public static void bytesToFile(byte[] buffer, File file) {
        try (OutputStream output = new FileOutputStream(file);
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(output)) {
            bufferedOutput.write(buffer);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void upLoadFile(InputStream stream, String file) {
        log.info("FileTool upLoadFile begin file:" + file);
        OutputStream bos = null;
        byte[] buffer = new byte[8192];

        try {
            File f = new File(file);
            f.createNewFile();
            bos = new FileOutputStream(f);

            int bytesRead;
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (Exception var14) {
            log.error("FileTool upLoadFile exception:", var14);
            var14.printStackTrace();
        } finally {
            try {
                bos.close();
                stream.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        log.info("FileTool upLoadFile end");
    }

    public static void delete(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception var2) {
        }

    }

    public static byte[] readFile(String fileName) throws IOException {
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

    public static String readFileToStr(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (fileName.startsWith("classpath:")) {
            fileName = fileName.replace("classpath:", "");
            Resource resource = new ClassPathResource(fileName);


            //打成jar后不能将classpath下的文件直接读取成文件
            //File file = resource.getFile();

            //使用如下方式可以在打成jar后读取到流
            try (InputStream inputStream = resource.getInputStream()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            File file = new File(fileName);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File not found: " + fileName);
            }

        }
        return sb.toString();
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

}
