package Main;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

public class Encryption {

    private static final String AES_ALGORITHM = "AES";
    private static final int KEY_SIZE = 16;

    public static byte[] encrypt(String plaintext, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(padKey(secretKey), AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plaintext.getBytes());
    }

    public static String decrypt(byte[] ciphertext, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(padKey(secretKey), AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptBytes = cipher.doFinal(ciphertext);
        return new String(decryptBytes);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String generatePassword() {
        int length = 16;
        String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
        String specialChars = "!@£$%^&*()-_=+[]{}|;:',.<>?";
        String numberChars = "0123456789";

        String allChars = uppercaseChars + lowercaseChars + specialChars + numberChars;

        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        password.append(uppercaseChars.charAt(random.nextInt(uppercaseChars.length())));
        password.append(lowercaseChars.charAt(random.nextInt(lowercaseChars.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        password.append(numberChars.charAt(random.nextInt(numberChars.length())));

        for (int i = password.length(); i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }
        return new String(passwordArray);
    }

    private static byte[] padKey(String key) {
        byte[] paddedKey = new byte[KEY_SIZE];
        byte[] keyBytes = key.getBytes();
        System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, KEY_SIZE));
        return paddedKey;
    }
}
