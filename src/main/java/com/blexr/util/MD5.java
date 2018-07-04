package com.blexr.util;

import java.security.MessageDigest;

public class MD5 {

    /*
     * Generate checksum for given array of bytes
     */
    public static String generateChecksum(byte[] dataBytes) throws Exception {
	MessageDigest md = MessageDigest.getInstance("SHA1");
	md.update(dataBytes, 0, dataBytes.length);
	byte[] mdbytes = md.digest();

	// convert the byte to hex format
	StringBuffer sb = new StringBuffer("");
	for (int i = 0; i < mdbytes.length; i++) {
	    sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	}

	System.out.println("Digest(in hex format):: " + sb.toString());
	return sb.toString();
    }
}
