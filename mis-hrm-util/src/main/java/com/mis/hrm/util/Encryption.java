package com.mis.hrm.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author May
 */
public class Encryption {
    public static String md5(String str) {
        Objects.requireNonNull(str);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(str.getBytes());
            return Hex.encodeHexString(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException();
        }
    }

    public static String sha1(String str) {
        Objects.requireNonNull(str);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
            byte[] shaBytes = md.digest(str.getBytes());
            return Hex.encodeHexString(shaBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException();
        }
    }

    public static String mac(String str, String key) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(key);
        try{
            //初始化密钥生成器
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
            //获取密钥
            byte[] encodeBytes = key.getBytes();
            //还原密钥
            SecretKey restoreSecretKey = new SecretKeySpec(encodeBytes, "HmacMD5");
            Mac mac = Mac.getInstance(restoreSecretKey.getAlgorithm());
            mac.init(restoreSecretKey);
            byte[] hmacmd5Bytes = mac.doFinal(str.getBytes());
            return Hex.encodeHexString(hmacmd5Bytes);
        }catch(NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncryptionException();
        }
    }

    public static String encode(String value, Function<String, String> func){
        Objects.requireNonNull(func);
        return func.apply(value);
    }

    public static String encode(String value, String key, BiFunction<String, String, String> func){
        Objects.requireNonNull(func);
        return func.apply(value, key);
    }

    private static class EncryptionException extends RuntimeException{}
}
