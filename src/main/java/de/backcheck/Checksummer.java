package de.backcheck;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class Checksummer {

	public byte[] checksum(File file) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		try (FileInputStream is = new FileInputStream(file)) {
			try (DigestInputStream dis = new DigestInputStream(is, md) ) {
				byte[] buf = new byte[32 * 1024];
				long total = 0;
				long t = 0;
				int c;
				while( (c=dis.read(buf)) >= 0 ) {
					total += c;
					t += c;
					if( t >= 1024*1024*1024 ) {
						long gb = (long)Math.round(total/(1024*1024*1024d)); 
						System.out.println("  digest "+file+" / "+gb+"gb");
						t = 0;
					}
				}
			}
		}	
		byte[] md5 = md.digest();
		return md5;
	}

}
