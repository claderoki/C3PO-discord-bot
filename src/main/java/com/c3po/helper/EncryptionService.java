package com.c3po.helper;

import com.c3po.helper.environment.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final Configuration configuration;

    private String _encrypt(String input, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String _decrypt(String encryptedInput, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        var input = Base64.getDecoder().decode(encryptedInput);
        return new String(cipher.doFinal(input));
    }

    public String decrypt(String value) {
        try {
            return _decrypt(value, configuration.getEncryptionKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(String value) {
        try {
            return _encrypt(value, configuration.getEncryptionKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
