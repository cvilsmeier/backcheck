package de.backcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.backcheck.util.HexUtils;

public class ChecksummerImpl implements Checksummer {

	@Override
	public String checksum(File file) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		DigestInputStream in = null;
		try {
			in = new DigestInputStream(new FileInputStream(file), md);
			byte[] buf = new byte[32 * 1024];
			while( in.read(buf) >= 0 ) {
				// keep reading
			}
		} finally{
			if( in != null ) {
				try {
					in.close();
				} catch (Exception ignore) {}
			}
		}
		byte[] md5 = md.digest();
		return HexUtils.bytesToHex(md5);
	}

}
