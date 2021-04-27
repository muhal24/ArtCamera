package com.artcamera.artcamera.net;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EDUtils {


    private static String sKey = "431b5fc3c1677ed7"+"38a";
    public static String DecryptStr(String sSrc, String sKey) {
        try {
            if (sKey == null || sKey.length() != 16) {
                Log.e("DecryptData", "Key is null or length is not 16");
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] original = cipher.doFinal(Base64.decode(sSrc, Base64.DEFAULT));
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String EncryptStr(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            Log.e("EncryptData", "Key is null");
            return null;
        }
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String xxx (){
        String openKey = sKey.substring(0,sKey.length()-3);
        openKey = openKey.replaceAll("7","a");
        return openKey;
    }
}
