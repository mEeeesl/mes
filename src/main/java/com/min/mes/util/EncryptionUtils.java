package com.min.mes.util;

import lombok.AllArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@AllArgsConstructor
public class EncryptionUtils {
    private static final String AES_KEY = "MES94050000000000000000000000000"; // 32바이트
    private static final String IV = AES_KEY.substring(0, 16); // 16바이트 IV

    // 1. AES256 암호화 (Hex 결과 반환 - 오라클과 맞춤)
    public static String encAES256(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encrypted);
    }

    // 2. AES256 복호화
    public static String decAES256(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        byte[] decodedBytes = hexToBytes(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // 3. SHA256 단방향 암호화
    public static String encSHA256(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(md.digest());

        /*
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
         */
    }

    // Hex 변환 도우미 메서드들
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString().toUpperCase();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}

