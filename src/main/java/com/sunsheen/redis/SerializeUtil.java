package com.sunsheen.redis;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sun.misc.BASE64Decoder;

public class SerializeUtil {
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String serializeString(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 搴忓垪鍖�
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return getBASE64(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getBASE64(byte[] bytes) {
		if (bytes == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(bytes);
	}

	public static byte[] getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	public static Object unserialize(String serieslizeStr) {
		if (serieslizeStr != null) {
			try {
				byte[] bytes = getFromBASE64(serieslizeStr);
				ObjectInputStream ois = new ObjectInputStream(
						new BufferedInputStream(new ByteArrayInputStream(bytes)));
				return ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
