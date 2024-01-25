package com.hanqf.resumedtransferoffset.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <h1>IO工具类</h1>
 * Created by hanqf on 2024/1/25 12:09.
 */


public class MyIOUtil {

    /**
     * <h2>从输入流中读取指定的字节数到输出流</h2>
     * 每写入4096字节后就输出到客户端
     *
     * @param inputStream       要读取的输入流
     * @param skipBytes         输入流要跳过的字节数
     * @param length            要读取的字节数，如果为null，则全部复制
     * @param outputStream      输出流
     * @param flushWhileWriting 是否边写边输出，如果是false，需要手动flush
     */
    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, Long skipBytes, Long length, OutputStream outputStream, boolean flushWhileWriting) throws IOException {
        byte[] buffer = new byte[1024]; // 缓冲区大小保持不变

        try {
            // 跳过指定的字节数
            skipBytes(inputStream, skipBytes);

            // 读取输入流，写入输出流
            long bytesWritten = 0;
            int bytesRead;

            // 如果length为null, 全部复制
            if (length == null) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    writeToOutputStream(outputStream, flushWhileWriting, buffer, bytesRead);
                }
            } else {
                while (bytesWritten < length && (bytesRead = inputStream.read(buffer)) != -1) {
                    if (bytesWritten + bytesRead > length) {
                        bytesRead = (int) (length - bytesWritten);
                    }
                    writeToOutputStream(outputStream, flushWhileWriting, buffer, bytesRead);
                    bytesWritten += bytesRead;
                }
            }
        } catch (IOException e) {
            throw new IOException("Error occurred while transferring data from input stream to output stream.", e);
        }
    }

    private static void skipBytes(InputStream inputStream, Long skipBytes) throws IOException {
        if (skipBytes != null) {
            long actualSkipped = 0;
            while (actualSkipped < skipBytes) {
                actualSkipped += inputStream.skip(skipBytes - actualSkipped);
            }
        }
    }

    private static void writeToOutputStream(OutputStream outputStream, boolean flushWhileWriting, byte[] buffer, int bytesRead) throws IOException {
        outputStream.write(buffer, 0, bytesRead);
        if (flushWhileWriting) {
            outputStream.flush();
        }
    }


    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, null, null, outputStream, false);
    }

    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, boolean flushWhileWriting) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, null, null, outputStream, flushWhileWriting);
    }

    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, Long skipBytes, OutputStream outputStream) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, skipBytes, null, outputStream, false);
    }

    public void copyDataFromInputStreamToOutputStream(InputStream inputStream, Long skipBytes, OutputStream outputStream, boolean flushWhileWriting) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, skipBytes, null, outputStream, flushWhileWriting);
    }

    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, Long skipBytes, Long length, OutputStream outputStream) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, skipBytes, length, outputStream, false);
    }

    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, Long length) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, null, length, outputStream, false);
    }

    public static void copyDataFromInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, Long length, boolean flushWhileWriting) throws IOException {
        copyDataFromInputStreamToOutputStream(inputStream, null, length, outputStream, flushWhileWriting);
    }
}
