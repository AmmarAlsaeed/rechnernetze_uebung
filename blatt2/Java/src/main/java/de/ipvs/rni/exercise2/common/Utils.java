package de.ipvs.rni.exercise2.common;

import de.ipvs.rni.exercise2.layers.ApplicationLayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bibartoo
 */
public class Utils {

	public static Object bytesToObj(ArrayDeque<Byte> byteBuffer) {
		Object obj = new Object();
		byte[] bytes = new byte[byteBuffer.size()];
		int j = 0;
		// Unboxing byte values. (Byte[] to byte[])
		Iterator<Byte> it = byteBuffer.iterator();
		while (it.hasNext()) {
			bytes[j++] = it.next();
		}
//		System.out.println(Utils.bytesToHex(byteBuffer));
		try (ObjectInputStream objInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			obj = objInputStream.readObject();
		} catch (IOException ex) {
			Logger.getLogger(ApplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ApplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
		}
		return obj;
	}

	public static ArrayDeque<Byte> objToBytes(Serializable obj) {
		ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(byteArrayInputStream)) {
			oos.writeObject(obj);
		} catch (IOException ex) {
			Logger.getLogger(ApplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
		}
		ArrayDeque<Byte> outputBuffer = new ArrayDeque<>();
		byte[] bytes = byteArrayInputStream.toByteArray();
		for (int i = 0; i < bytes.length; i++) {
			outputBuffer.add(bytes[i]);
		}
//        System.out.println(Utils.bytesToHex(outputBuffer));
		return outputBuffer;
	}

	// Source:
	// https://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java
	// Last Accesed: 2018/05/05
	public static Byte[] hexStringToByteArray(String s) {
		int len = s.length();
		Byte[] data = new Byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	// Source:
	// https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
	// Last Accessed: 2018/06/05
	public static String bytesToHex(ArrayDeque<Byte> bytes) {
		char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[bytes.size() * 2];
		Iterator<Byte> it = bytes.iterator();
		int j = 0;
		while (it.hasNext()) {
			int v = it.next() & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			j++;
		}
		return new String(hexChars);
	}

    // Source:
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    // Last Accessed: 2018/06/05
    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0);
    }

	public static String bytesToHex(byte[] bytes, int offset) {
		return bytesToHex(bytes, offset, bytes.length);
	}

	public static String bytesToHex(byte[] bytes, int offset, int length) {
		char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[length * 2];

		int j = 0;
		for(int i=offset; i<offset+length; i++) {
			int v = bytes[i] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			j++;
		}
		return new String(hexChars);
	}
}
