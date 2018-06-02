package io.github.samirsamir.passwordkeeper.util;

public interface EncryptionHandler {

    String encrypt(String text);
    String decrypt(String encryptedText);
}
