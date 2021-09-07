package base.service.frameworks.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * <pre>
 * Created by someone on 2017-04-01.
 *
 * </pre>
 */
@SuppressWarnings("unused")
public class RSAUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(RSAUtil.class);

    private static final int MAX_ENCRYPT_BLOCK = 117;

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 获取PKCS12格式私钥
     *
     * @param pPassword 密码
     * @param pCertPath 证书路径
     * @return 私钥
     */
    public static PrivateKey getPrivateKeyPKCS12(String pPassword, String pCertPath) {
        PrivateKey  privateKey   = null;
        InputStream inputStreams = null;
        try {
            inputStreams = RSAUtil.class.getClassLoader().getResourceAsStream(pCertPath);
            KeyStore keystoreCA = KeyStore.getInstance("PKCS12");
            // 读取CA根证书
            keystoreCA.load(inputStreams, pPassword.toCharArray());

            Enumeration<?> aliases = keystoreCA.aliases();
            String         keyAlias;
            if (aliases != null) {
                while (aliases.hasMoreElements()) {
                    keyAlias = (String) aliases.nextElement();
                    // 获取CA私钥
                    privateKey = (PrivateKey) (keystoreCA.getKey(keyAlias, pPassword.toCharArray()));
                    if (privateKey != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            if (inputStreams != null) {
                try {
                    inputStreams.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
        return privateKey;
    }

    /**
     * 获取X.509格式公钥
     *
     * @param pCertPath 证书路径
     * @return 公钥
     */
    public static PublicKey getPublicKeyX509(String pCertPath) {
        PublicKey   publicKey    = null;
        InputStream inputStreams = null;
        try {
            inputStreams = RSAUtil.class.getClassLoader().getResourceAsStream(pCertPath);
            CertificateFactory cf  = CertificateFactory.getInstance("X.509");
            Certificate        cac = cf.generateCertificate(inputStreams);
            publicKey = cac.getPublicKey();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            if (inputStreams != null) {
                try {
                    inputStreams.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
        return publicKey;
    }

    public static String sha1_rsa_base64(PrivateKey pPrivateKey, String pContent, Charset pCharset) {
        try {
            Signature signet = Signature.getInstance("SHA1WithRSA");
            signet.initSign(pPrivateKey);
            signet.update(pContent.getBytes(pCharset));
            byte[] signed = signet.sign();
            return new String(Base64.encodeBase64(signed), pCharset);
        } catch (Exception e) {
            LOG.error(e);
        }
        return "";
    }

    /**
     * rsa验签
     *
     * @param pPublicKey 公钥
     * @param pContent   内容
     * @param pSign      sign
     * @param pCharset   编码
     * @return true为正确
     */
    public static boolean sha1WithRSACheck(PublicKey pPublicKey, String pContent, String pSign, Charset pCharset) {
        boolean bFlag = false;
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pPublicKey);
            signature.update(pContent.getBytes(pCharset));
            if (signature.verify(Base64.decodeBase64(pSign.getBytes(pCharset)))) {
                //跑不进条件语句里面
                bFlag = true;
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return bFlag;
    }

    public static byte[] encrypt(byte[] pContent, String pPublicKey) throws Exception {
        PublicKey publicKey = getPublicKey(pPublicKey);
        Cipher    cipher    = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int                   inputLen = pContent.length;
        ByteArrayOutputStream out      = new ByteArrayOutputStream();
        int                   offSet   = 0;
        int                   i        = 0;
        byte[]                cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(pContent, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(pContent, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptText = out.toByteArray();
        out.close();
        return encryptText;
    }

    public static PublicKey getPublicKey(String pPublicKey) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(pPublicKey);
        X509EncodedKeySpec keySpec    = new X509EncodedKeySpec(keyBytes);
        KeyFactory         keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static void main(String[] args) {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHKBtkVPY873AuGb/CRs9y8xma1N8jAX+lZR5kMeY+Ue4GhMUd3a0nxcBoxZZnqzdKMoo4f+IIuAi1qvpbNgwbzfQ6iecp+SDfOQ2/RNDvLtvm5pIck8kkYyV8KXcPVsBNg3OIb9Mg5DBS5HDVQ+eWUGmKuYMRtnT3K3r4Z7rWMQIDAQAB";
        String content = "123";
        try {
            System.out.println(EncryptUtil.base64_encode(encrypt(content.getBytes(), publicKey)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
