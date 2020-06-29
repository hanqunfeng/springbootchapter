package org.lynx;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class ZipUtil {
	
	
	public byte[] unzipData(byte[] bytes) throws ZipException {
		InputStream is = new ByteArrayInputStream(bytes);
		InflaterInputStream zis = new InflaterInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		try {
			while ((bytesRead = zis.read(buffer, 0, 1024)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			byte[] data = out.toByteArray();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bytes = null;
			try {
				zis.close();
				out.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		throw new ZipException();
	}
	

	public byte[] zipData(byte[] bytes) throws ZipException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(out);
		try {
			dos.write(bytes);
			dos.finish();
			dos.flush();
			byte[] data = out.toByteArray();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				dos.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		throw new ZipException();
	}
	
}
