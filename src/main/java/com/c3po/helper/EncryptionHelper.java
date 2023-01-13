package com.c3po.helper;

import com.c3po.helper.environment.Configuration;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

public class EncryptionHelper {
    public static String _encrypt(String input, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public static String _decrypt(String encryptedInput, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        var input = Base64.getDecoder().decode(encryptedInput);
        return new String(cipher.doFinal(input));
    }


    public static String decrypt(String value) {
        try {
            return _decrypt(value, Configuration.instance().getEncryptionKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String value) {
        try {
            return _encrypt(value, Configuration.instance().getEncryptionKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
