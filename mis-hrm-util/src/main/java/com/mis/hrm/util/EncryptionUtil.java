package com.mis.hrm.util;

import com.google.common.base.Strings;
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
public class EncryptionUtil {

    private static String KEY_EXCEPTION = "键不能为空";
    private static String VALUE_EXCEPTION = "被加密的字符串不能为空或者NULL";

    /**
     * MD5加密
     * @param str 被加密的字符串
     * @return 加密过后的字符串
     */
    public static String md5(String str) {
        if (Strings.isNullOrEmpty(str)){
            throw new EncryptionException(VALUE_EXCEPTION);
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(str.getBytes());
            return Hex.encodeHexString(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException();
        }
    }

    /**
     * sha1加密
     * @param str 被加密的字符串
     * @return 加密过后的字符串
     */
    public static String sha1(String str) {
        if (Strings.isNullOrEmpty(str)){
            throw new EncryptionException(VALUE_EXCEPTION);
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
            byte[] shaBytes = md.digest(str.getBytes());
            return Hex.encodeHexString(shaBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException();
        }
    }

    /**
     * MAC加密
     * @param str 被加密字符串
     * @param key 密钥
     * @return 加密过后的字符串
     */
    public static String mac(String str, String key) {
        if (Strings.isNullOrEmpty(str)){
            throw new EncryptionException(VALUE_EXCEPTION);
        }
        if (Strings.isNullOrEmpty(key)){
            throw new EncryptionException(KEY_EXCEPTION);
        }
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

    /**
     * 便于自定义加密函数
     * @param value 被加密字符串
     * @param func 加密函数
     * @return 加密过后的字符串
     */
    public static String encode(String value, Function<String, String> func){
        Objects.requireNonNull(func);
        return func.apply(value);
    }

    /**
     * 便于自定义加密函数
     * @param value 被加密字符串
     * @param key 密钥
     * @param func 加密函数
     * @return 加密过后的字符串
     */
    public static String encode(String value, String key, BiFunction<String, String, String> func){
        Objects.requireNonNull(func);
        return func.apply(value, key);
    }

    /**
     * 加密错误的非受检异常
     */
    private static class EncryptionException extends RuntimeException{
        EncryptionException() {
        }

        EncryptionException(String msg) {
            super(msg);
        }
    }
}
