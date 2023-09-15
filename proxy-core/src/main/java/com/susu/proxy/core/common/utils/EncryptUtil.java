package com.susu.proxy.core.common.utils;

import lombok.extern.slf4j.Slf4j;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;


/**
 * <p>Description: encryption</p>
 * <p>加密工具类</p>
 * @author sujay
 * @version 22:11 2023/09/15
 * @since JDK1.8 <br/>
 */
@Slf4j
public class EncryptUtil {

    public static final String DES = "DES";

    public static String charset = "utf-8";

    public static int keySizeDES = 0;

    public static String keyGeneratorES(String res, String algorithm, String key, int keySize, boolean isEncode) {

        try {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            byte[] keyBytes;
            if (keySize == 0) {
                keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(keyBytes);
                kg.init(secureRandom);
            } else if (key == null) {
                kg.init(keySize);
            } else {
                keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                kg.init(keySize, new SecureRandom(keyBytes));
            }

            SecretKey sk = kg.generateKey();
            SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (isEncode) {
                cipher.init(1, sks);
                byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
                return parseByte2HexStr(cipher.doFinal(resBytes));
            } else {
                cipher.init(2, sks);
                byte[] bytes = parseHexStr2Byte(res);
                if (bytes != null) {
                    return new String(cipher.doFinal(bytes));
                }
                return null;
            }
        } catch (Exception var10) {
            log.error("", var10);
            return null;
        }
    }

    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();

        for (byte b : buf) {
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }

            return result;
        }
    }

    /**
     * 加密
     */
    public static String encode(String res, String key) {
        return keyGeneratorES(res, DES, key, keySizeDES, true);
    }

    /**
     * 解密
     */
    public static String decode(String res, String key) {
        return keyGeneratorES(res, DES, key, keySizeDES, false);
    }

}
