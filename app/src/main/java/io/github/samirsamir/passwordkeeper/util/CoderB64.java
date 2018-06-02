package io.github.samirsamir.passwordkeeper.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CoderB64 implements EncryptionHandler{

    private final String KEY = "YOUR_KEY";

    @Override
    public String encrypt(String text) {
        return code(KEY, text);
    }

    @Override
    public String decrypt(String encryptedText) {
        return decode(KEY, encryptedText);
    }

    private String code(String key, String text){
        int j=0;
        byte[] data;
        char[] arrayKey = key.toCharArray();

        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        List<Byte> byteList = new ArrayList<>();

        for(int i = 0; i < data.length; i++){
            byteList.add((byte) (((int) (data[i])) ^ (int) (arrayKey[j])));

            if (j < (arrayKey.length-1)){
                j++;
            } else{
                j=0;
            }
        }

        byte[] arrayKeyEncrypted = new byte[byteList.size()];

        for(int i=0; i < arrayKeyEncrypted.length; i++){
            arrayKeyEncrypted[i] = byteList.get(i);
        }

        return Base64.encodeToString(arrayKeyEncrypted, Base64.DEFAULT);
    }

    private String decode(String inputKey, String encryptedText){
        byte[] tmp = Base64.decode(encryptedText, Base64.DEFAULT);
        char[] arrayKey = inputKey.toCharArray();
        int j=0;

        List<Byte> byteArrayList = new ArrayList<>();
        char[] tmpAC = new String(tmp).toCharArray();

        for(int i=0; i<tmpAC.length;i++){
            byteArrayList.add((byte) (((int) (tmpAC[i]) ^ (int) (arrayKey[j]))));

            if(j<(inputKey.length()-1)){
                j++;
            } else {
                j=0;
            }
        }

        byte[] arrayByte = new byte[byteArrayList.size()];

        for(int i=0; i < arrayByte.length; i++){
            arrayByte[i] = byteArrayList.get(i);
        }

        try {
            return new String(arrayByte,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
