package com.xhance.sdk.security;


import java.security.MessageDigest;

public class Md5Utils {
    private static String MD5 = "MD5";

    public static String textOfMd5(String value) throws Exception {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] var2 = value.getBytes();
        MessageDigest var3 = MessageDigest.getInstance(MD5);
        var3.update(var2, 0, var2.length);
        byte[] var4 = var3.digest();
        int var5 = var4.length;
        char[] var6 = new char[var5 * 2];
        int var7 = 0;

        for(int var8 = 0; var8 < var5; ++var8) {
            byte var9 = var4[var8];
            var6[var7++] = var1[var9 >>> 4 & 15];
            var6[var7++] = var1[var9 & 15];
        }

        return new String(var6);
    }

}
