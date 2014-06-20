
/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: guard 
 * Author: dixingxing
 * Createdate: 下午7:10:40
 * Version: 1.0
 *
 */
package org.dxx.rpc.config.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jaxb 解析xml工具类
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-5-31 下午7:10:40   
 */
public final class JaxbMapper {
	private static final Logger LOG = LoggerFactory.getLogger(JaxbMapper.class);
	
	private JaxbMapper() {}
	
	/**
	 * xml string -> object
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-5-31 下午7:13:38
	 * @param xml
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> clazz) {
		try {
			return (T) JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	private static StringBuffer toStringBuffer(InputStream is) throws IOException {
		StringWriter output = new StringWriter();
		InputStreamReader input = new InputStreamReader(is);
		char buffer[] = new char[4096];
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
		}
		return output.getBuffer();
	}
	
	/**
	 * xml file -> object
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-5-31 下午7:18:10
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	public static <T> T fromClasspathXmlFile(String fileName, Class<T> clazz) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		try {
			return fromXml(toStringBuffer(is).toString(), clazz);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
