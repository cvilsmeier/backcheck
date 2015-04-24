package de.backcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksummerImpl implements Checksummer {

	@Override
	public byte[] checksum(File file) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("cannot instantiate MD5 algorithm", e);
		}
		DigestInputStream in = null;
		try {
			in = new DigestInputStream(new FileInputStream(file), md);
			byte[] buf = new byte[32 * 1024];
			long total = 0;
			long t = 0;
			int c;
			while( (c=in.read(buf)) >= 0 ) {
				total += c;
				t += c;
				if( t >= 1024*1024*1024 ) {
					long gb = (long)Math.round(total/(1024*1024*1024d)); 
					System.out.println("  digest "+file+" / "+gb+"gb");
					t = 0;
				}
			}
		} finally{
			if( in != null ) {
				try {
					in.close();
				} catch (Exception ignore) {}
			}
		}
		byte[] md5 = md.digest();
		return md5;
	}

}
