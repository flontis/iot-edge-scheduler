package EdgeServer;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class KeyStorage {
    private KeyStore ks;



    public KeyStorage() {
        try {
            ks = KeyStore.getInstance("pkcs12");
            char[] passwordArr = "FreshAvocado".toCharArray();
            ks.load(null, passwordArr);

            FileOutputStream fos = new FileOutputStream("EdgeServerKeystore.jks");
            ks.store(fos, passwordArr);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
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

    public void storeSecretKey(SecretKey key, String alias){
        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection("FreshAvocado".toCharArray());
        try {
            ks.setEntry(alias, secret, password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public Key getEntry(String alias, char[] password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        if(ks.containsAlias(alias)){
            return ks.getKey(alias, password);
        }
        else{
            return null;
        }

    }
}
