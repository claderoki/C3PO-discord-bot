package com.c3po.helper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

public class EncryptionHelper {
    private static Key fetchKey() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            return factory.generateSecret()
//            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
//
//            return keygenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static Key key = null;
    private static Key getKey() {
        if (key == null) {
            key = fetchKey();
        }
        return key;
    }


    public static String _decrypt(String value) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, getKey());
        byte[] textDecrypted = desCipher.doFinal(value.getBytes());
        return new String(textDecrypted);
    }

    public static String decrypt(String value) {
        try {
            return _decrypt(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String _encrypt(String value) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encrypted = desCipher.doFinal(value.getBytes());
        return new String(encrypted);
    }

    public static String encrypt(String value) {
        try {
            return _encrypt(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
