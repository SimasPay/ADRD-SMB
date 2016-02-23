package com.mfino.bsim.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;

public class CryptoService {/*

    public static byte[] decryptWithPBE(final byte[] cipherText, final char[] password, final byte[] salt, int iterationCount) throws DataLengthException, IllegalStateException, InvalidCipherTextException, InvalidKeyException,
            NoSuchAlgorithmException, BadPaddingException { // IllegalBlockSizeException, NoSuchPaddingException, 

        final PBEParametersGenerator keyGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        keyGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password), salt, iterationCount);
        final CipherParameters keyParams = keyGenerator.generateDerivedParameters(256, 128);

        final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
        cipher.init(false, keyParams);

        final byte[] processed = new byte[cipher.getOutputSize(cipherText.length)];
        int outputLength = cipher.processBytes(cipherText, 0, cipherText.length, processed, 0);
        outputLength += cipher.doFinal(processed, outputLength);

        final byte[] results = new byte[outputLength];
        System.arraycopy(processed, 0, results, 0, outputLength);
        return results;
    }

    public static byte[] generateSalt() {
        byte salt[] = new byte[8];
        try {
            SecureRandom saltGen = SecureRandom.getInstance("SHA1PRNG");
            saltGen.nextBytes(salt);
        } catch (Exception ex) {
            byte[] salt1 = {0, 0, 0, 0, 0, 0, 0, 0};
            return salt1;
        }
        return salt;

    }

    public static byte[] encryptWithPBE(final byte[] plainText, final char[] password, final byte[] salt, int iterationCount) throws DataLengthException, IllegalStateException, InvalidCipherTextException, InvalidKeyException,
            NoSuchAlgorithmException, BadPaddingException { // IllegalBlockSizeException, NoSuchPaddingException, 

        final PBEParametersGenerator keyGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        keyGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password), salt, iterationCount);
        final CipherParameters keyParams = keyGenerator.generateDerivedParameters(256, 128);

        final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
        cipher.init(true, keyParams);

        final byte[] processed = new byte[cipher.getOutputSize(plainText.length)];
        int outputLength = cipher.processBytes(plainText, 0, plainText.length, processed, 0);
        outputLength += cipher.doFinal(processed, outputLength);

        final byte[] results = new byte[outputLength];
        System.arraycopy(processed, 0, results, 0, outputLength);
        return results;
    }

    public static char[] generateHash(String mdn, String pin) throws Exception {
        try {
        	System.out.println("The mdn and pin is:"+mdn+" ,"+pin);
            SHA256Digest digest = new SHA256Digest();
            digest.update(pin.getBytes(com.mfino.bsim.services.Constants.UTF_8), 0, pin.getBytes(com.mfino.bsim.services.Constants.UTF_8).length);
            digest.update(mdn.getBytes(com.mfino.bsim.services.Constants.UTF_8), 0, mdn.getBytes(com.mfino.bsim.services.Constants.UTF_8).length);

            byte[] res = new byte[digest.getDigestSize()];
            digest.doFinal(res, 0);
            return binToHex(res);
        } catch (Exception ex) {
        }
        return null;

    }

    public static char[] binToHex(byte[] byteArray) {

        char[] hexadecimalChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[byteArray.length * 2];// every byte becomes
        // two Hex
        // characters
        for (int i = 0; i < hexChars.length / 2; i++) {
            hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
            hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
        }
       // System.out.print("The final hexa decimal:"+hexChars);
        return hexChars;
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println(generateHash("987654321", "123456"));
//        String message = "I am plain Text";
//        byte[] key = "123456781234567812345678".getBytes(Constants.UTF_8);
//
//        byte[] cipherText = encryptWithAES(key,
//                message.getBytes(Constants.UTF_8));
//        byte[] out = decryptWithAES(key, cipherText);
//
//        System.out.println("AES result="
//                + new String(out, Constants.UTF_8));
//
//        byte[] salt = {42, -94, -85, 112, 3, -101, 85, 16};
//        char[] pbeKey = "2B6E40EC1702F914477724F614DFF2824B059554".toCharArray();
//        message = Constants.ZEROES_STRING;
//        byte[] pbeEncrypted = encryptWithPBE(message.getBytes(Constants.UTF_8), pbeKey, salt, 20);
//
//        byte[] pbeDecrypted = decryptWithPBE(pbeEncrypted, pbeKey, salt, 20);
//        System.out.println("PBE Result= " + new String(pbeDecrypted, Constants.UTF_8));
//
//        SecureResponseDataContainer rs = new SecureResponseDataContainer();
//
//        rs.setSalt("6082D85C83D0A9D4");
//        rs.setAuthenticationString("3852EBCE762E462F951BFDE45A7C499D");
//        rs.setAESkeyString("14C52634491653897BC08DD8FDE151C4261F303A7CAC363D5567171C272FB74B");
//
//        salt = CryptoService.hexToBin(rs.getSalt().toCharArray());
//        byte[] encryptedZeroes = CryptoService.hexToBin(rs.getAuthenticationString().toCharArray());
//        byte[] encryptedKey = CryptoService.hexToBin(rs.getAESkeyString().toCharArray());
//
//        String pinHash = new String(CryptoService.generateHash("987654321", "123456"));
//        String password = pinHash.substring(Constants.PasswordInDigestSrartsFrom, Constants.PasswordInDigestEndsAt);
//        byte[] aesKey = CryptoService.decryptWithPBE(encryptedKey, password.toCharArray(), salt, Constants.PBE_ITERATION_COUNT);
//        byte[] decryptedZeroes = CryptoService.decryptWithAES(aesKey, encryptedZeroes);
//        String str = new String(decryptedZeroes, Constants.UTF_8);
//        if (Constants.ZEROES_STRING.equals(str)) {
//            System.out.println("Happy");
//        }
//
//    }

    public static byte[] encryptWithAES(byte[] key, byte[] plainText) throws Exception {
        return cipheringWithAES(new KeyParameter(key), plainText, true);
    }

    public static byte[] encryptWithAES(KeyParameter keyParameter, byte[] plainText) throws Exception {
        return cipheringWithAES(keyParameter, plainText, true);
    }

    //	private static byte[] cipheringWithAES(byte[] key, byte[] plainText,
    //			boolean isEncryption) throws Exception {
    //		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
    //				new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
    //		KeyParameter kp = new KeyParameter(key);
    //		cipher.init(isEncryption, kp);
    //		byte[] temp = new byte[cipher.getOutputSize(plainText.length)];
    //		int noOfOutBytes = -1;
    //		noOfOutBytes = cipher.processBytes(plainText, 0, plainText.length,
    //				temp, 0);
    //		noOfOutBytes = noOfOutBytes + cipher.doFinal(temp, noOfOutBytes);
    //		byte[] out = new byte[noOfOutBytes];
    //		for (int i = 0; i < noOfOutBytes; i++)
    //			out[i] = temp[i];
    //		return out;
    //	}
    private static byte[] cipheringWithAES(KeyParameter keyParameter, byte[] plainText, boolean isEncryption) throws Exception {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
        cipher.init(isEncryption, keyParameter);
        byte[] temp = new byte[cipher.getOutputSize(plainText.length)];
        int noOfOutBytes = -1;
        noOfOutBytes = cipher.processBytes(plainText, 0, plainText.length, temp, 0);
        noOfOutBytes = noOfOutBytes + cipher.doFinal(temp, noOfOutBytes);
        byte[] out = new byte[noOfOutBytes];
        for (int i = 0; i < noOfOutBytes; i++) {
            out[i] = temp[i];
        }
        return out;
    }

    public static byte[] decryptWithAES(byte[] key, byte[] plainText) throws Exception {
        return cipheringWithAES(new KeyParameter(key), plainText, false);
    }

    public static byte[] decryptWithAES(KeyParameter keyParameter, byte[] plainText) throws Exception {
        return cipheringWithAES(keyParameter, plainText, false);
    }

    public static byte[] hexToBin(char[] hexChars) {
        int[] intArray = new int[hexChars.length / 2];
        int length = hexChars.length;
        if ((length & 0x1) == 0x1) {
            return null;
        }
        int size = 0;
        for (int i = 0; i < hexChars.length; i++) {
            char c = hexChars[i];
            if (!isHexaDigit(c)) {
                return null;
            }
            int N = 0;
            if (c >= '0' && c <= '9') {
                N = c - 0x30;
            } else if (c >= 'A' && c <= 'F') {
                N = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                N = c - 'a' + 10;
            } else {
                return null;
            }
            if ((size & 0x1) == 0x1) // two HEX chars become one byte
            {
                intArray[(size & 0xff) >> 1] += (N & 0xff);
            } else {
                intArray[(size & 0xff) >> 1] = (N & 0xff) << 4;// to avoid nasty
            }				                                               // surprises and
            // we are concerned with only the first 8 bits
            size++;
            length--;
        }
        return intArrayToByteArray(intArray);
    }

    private static boolean isHexaDigit(char c) {
        if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
            return true;
        }
        return false;
    }

    public static byte[] intArrayToByteArray(int[] intArr) {
        byte[] byteArr = new byte[intArr.length];
        for (int i = 0; i < intArr.length; i++) {
            byteArr[i] = (byte) intArr[i];
        }
        return byteArr;
    }
*/}

