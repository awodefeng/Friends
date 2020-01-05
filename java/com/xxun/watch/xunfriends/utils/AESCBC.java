package com.xxun.watch.xunfriends.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by xilvkang on 2017/11/29.
 */

public class AESCBC {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String KEY = "7wvQblUOL9qpzoz6";

    public static String encrypt(byte[] srcData,byte[] key,byte[] iv){
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encData = cipher.doFinal(srcData);
            return  new String(Base64.encode(encData,Base64.DEFAULT));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] encData,byte[] key,byte[] iv) {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] decbbdt = cipher.doFinal(encData);
            return new String(decbbdt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // AES CBC 加密
    public static byte[] encryptAESCBC(String sSrc,String sKey, String ivParameter){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return encrypted;
        }catch (Exception e){
            return null;
        }
    }

    //AES CBC 解密
    public static byte[] decryptAESCBC(byte [] encrypted, String sKey, String ivParameter){
        try {
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encrypted);
            return original;
        } catch (Exception ex) {
            return null;
        }
    }

}
