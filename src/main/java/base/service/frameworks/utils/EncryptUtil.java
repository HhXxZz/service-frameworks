package base.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SuppressWarnings({"WeakerAccess", "unused"})
public class EncryptUtil {
    private static final Logger LOG = LogManager.getLogger(EncryptUtil.class);

    private static final String algorithmStr = "AES/ECB/PKCS5Padding";

    public static byte[] aes_encode(String pSecretKey, String pContent) {
        try {
            Cipher        cipher      = Cipher.getInstance(algorithmStr);
            byte[]        byteContent = pContent.getBytes(StandardCharsets.UTF_8);
            byte[]        keyBuffer   = getKey(pSecretKey);
            SecretKeySpec key         = new SecretKeySpec(keyBuffer, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(byteContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] aes_decode(String pSecretKey, byte[] pContent) {
        try {
            byte[]        keyBuffer   = getKey(pSecretKey);
            SecretKeySpec key         = new SecretKeySpec(keyBuffer, "AES");
            Cipher        cipher      = Cipher.getInstance(algorithmStr);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(pContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] aes_cbc_pc7_decode(byte[] pSecretKey, byte[] pIV, byte[] pContent) {
        try {
            SecretKeySpec key       = new SecretKeySpec(pSecretKey, "AES");
            Cipher        cipher    = Cipher.getInstance("AES/CBC/PKCS7Padding", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(pIV));
            return cipher.doFinal(pContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String base64_encode(byte[] pContent){
        return Base64.getEncoder().encodeToString(pContent);
    }

    public static byte[] base64_decode(String pContent){
        return Base64.getDecoder().decode(pContent);
    }

    public static byte[] getKey(String pSecret) {
        byte[] rByte;
        if (pSecret != null) {
            rByte = pSecret.getBytes();
        } else {
            rByte = new byte[24];
        }
        return rByte;
    }

    public static byte[] sha_256(String pContent){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return sha.digest(pContent.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] hmac_sha_256(String pMessage, String pKey){
        try {
            Mac           sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key  = new SecretKeySpec(pKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return sha256_HMAC.doFinal(pMessage.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] sha1(String pContent){
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(pContent.getBytes(StandardCharsets.UTF_8), 0, pContent.length());
            byte[] digest = sha1.digest();
            sha1.reset();
            return digest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 将字符串加密，按照 base64(aes(字符串)) 的方式
     * @param pSecretKey AES密钥
     * @param pContent 被加密内容
     * @return 加密结果
     */
    public static String encode_aes_base64(String pSecretKey, String pContent) {
        return base64_encode(aes_encode(pSecretKey, pContent));
    }

    /**
     * 将字符串解密，按照 aes(base64(字符串)) 的方式, 与{@link EncryptUtil#encode_aes_base64} 对应
     * @param pSecretKey AES密钥
     * @param pContent 加密内容
     * @return 解密内容
     */
    public static String decode_base64_aes(String pSecretKey, String pContent) {
        return new String(aes_decode(pSecretKey, base64_decode(pContent)));
    }

    /**
     * 将字符串加密，按照 base64(sha1(字符串)) 的方式
     * @param pContent 被加密内容
     * @return 加密结果
     */
    public static String encode_base64_sha1(String pContent){
        return base64_encode(sha1(pContent));
    }

    /**
     * 将字符串加密，按照 base64(hmac_sha_256(字符串)) 的方式
     * @param pContent 被加密内容
     * @param pKey 密钥
     * @return 加密结果
     */
    public static String encode_base64_hmac_sha_256(String pContent, String pKey){
        return base64_encode(hmac_sha_256(pContent, pKey));
    }

    /**
     * 将字符串加密，按照 base64(sha_256(字符串)) 的方式
     * @param pContent 被加密内容
     * @return 加密结果
     */
    public static String encode_base64_sha_256(String pContent){
        return base64_encode(sha_256(pContent));
    }


    public static void main(String[] args) {
        long storeOrderID = 1000002;
        String md5 = MD5.getMD5(String.valueOf(storeOrderID));
        String secret = md5.substring(16, 32).toUpperCase();
        System.out.println(md5);
        System.out.println(secret);
        String content = "{\"codes\":[{\"name\":\"card01\",\"value\":\"123abc\"},{\"name\":\"card02\",\"value\":\"456efg\"}]}";
        String encrypted = EncryptUtil.encode_aes_base64(secret, content);
        System.out.println("Encode : " + encrypted);
        System.out.println("Decode : " + EncryptUtil.decode_base64_aes(secret, encrypted));


        System.out.println(encode_aes_base64("KHSUDT*&STGDIS#F", "1"));
    }

}