package ResourceAdministrator.KeyStorage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class KeyStorage {
    private KeyStore ks;

    public KeyStorage() {
        try {
            ks = KeyStore.getInstance("pkcs12");
            char[] passwordArr = "FreshAvocado".toCharArray();
            ks.load(null, passwordArr);

            FileOutputStream fos = new FileOutputStream("EdgeNodeKeyStore.jks");
            ks.store(fos, passwordArr);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String sharedSecret = "VonderWiegebiszurBahre";
        KeySpec spec = new PBEKeySpec(sharedSecret.toCharArray(), "FloriansamWerk".getBytes(), 65536, 256);
        SecretKey tmp = null;
        try {
            assert factory != null;
            tmp = factory.generateSecret(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        assert tmp != null;
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        this.storeKey(secret.getEncoded(), "shared-key");
        System.out.println("Edge Node stored shared key: " + Arrays.toString(secret.getEncoded()));
    }

    public void storeKey(byte[] publicKey, String alias){
        SecretKey key = new SecretKeySpec(publicKey, 0, publicKey.length, "RSA");
        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection("FreshAvocado".toCharArray());
        try {
            ks.setEntry(alias, secret, password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

    }

    public Key getEntry(String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        if(ks.containsAlias(alias)){
            return ks.getKey(alias, "FreshAvocado".toCharArray());
        }
        else{
            return null;
        }

    }
}
