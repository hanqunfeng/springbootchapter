package com.example.minanew.mina.protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * 自定义解编码器工厂
 *
 */

public class ByteArrayCodecFactory implements ProtocolCodecFactory {

	private ByteArrayDecoder decoder;
	private ByteArrayEncoder encoder;

	public ByteArrayCodecFactory() {
		this(Charset.defaultCharset());
	}

	public ByteArrayCodecFactory(Charset charSet) {
		encoder = new ByteArrayEncoder(charSet);
		decoder = new ByteArrayDecoder(charSet);
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
