package de.backcheck.util;

public abstract class HexUtils {

	public static String bytesToHex(byte[] buffer) {
		int m = buffer.length;
		int n = 2 * m;
		int l = 0;
		char[] chars = new char[n];
		for (int k = 0; k < m; k++) {
			byte v = buffer[k];
			int i = (v >> 4) & 0xf;
			chars[l++] = (char) (i >= 10 ? ('a' + i - 10) : ('0' + i));
			i = v & 0xf;
			chars[l++] = (char) (i >= 10 ? ('a' + i - 10) : ('0' + i));
		}

		return new String(chars);
	}

	public static byte[] hexToBytes(String input) {
		int n = input.length() / 2;
		byte[] buffer = new byte[n];
		int l = 0;
		for (int k = 0; k < n; k++) {
			char c = input.charAt(l++);
			byte b = (byte) ((c >= 'a' ? (c - 'a' + 10) : (c - '0')) << 4);
			c = input.charAt(l++);
			b |= (byte) (c >= 'a' ? (c - 'a' + 10) : (c - '0'));
			buffer[k] = b;
		}
		return buffer;
	}

}
