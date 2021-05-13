package EdgeServer;


import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class KeyGenerator {
    protected enum Algorithm  {AES, RSA, DESede, Blowfish}
    private final Algorithm cryptoAlgo;
    private final KeyStorage keystore;

    protected KeyGenerator(Algorithm algo) {
        this.cryptoAlgo = algo;
        this.keystore = new KeyStorage();
        try {
            createSharedSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        switch (this.cryptoAlgo){
            case AES:
                createAESKey();
                break;
            case RSA:
                createRSAKeyPair();
                break;
            case Blowfish:
                createBlowfishKey();
                break;
            case DESede:
                createDESedeKey();
                break;
            default:
                System.out.println("Algorithm not available");
        }
    }

    protected byte[] getPubKey(){
        switch (this.cryptoAlgo){
            case AES:
                return getAESKey();
            case RSA:
                return getRSAPubKey();
            case Blowfish:
                return getBlowfishKey();
            case DESede:
                return getDESedeKey();
            default:
                return null;
        }
    }

    private void createAESKey(){
        javax.crypto.KeyGenerator keyGen = null;
        try {
            keyGen = javax.crypto.KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyGen != null;
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        keystore.storeKey(secretKey.getEncoded(), "AES-key");
        System.out.println("Stored AES key: " + Arrays.toString(secretKey.getEncoded()));
    }

    private byte[] getAESKey(){
        try {
            Key key = keystore.getEntry("AES-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }

    private void createRSAKeyPair(){
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyGen != null;
        keyGen.initialize(2048);
        KeyPair kpair = keyGen.genKeyPair();
        PublicKey publicKey = kpair.getPublic();
        PrivateKey privateKey = kpair.getPrivate();

        keystore.storeKey(publicKey.getEncoded(), "RSA-public-key");
        keystore.storeKey(privateKey.getEncoded(), "RSA-private-key");
        System.out.println("Stored RSA public key: " + Arrays.toString(publicKey.getEncoded()));
        System.out.println("Stored RSA private key: " + Arrays.toString(privateKey.getEncoded()));
    }

    private byte[] getRSAPubKey(){
        try {
            Key key = keystore.getEntry("RSA-public-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }

    private byte[] getRSAPrivateKey(){
        try {
            Key key = keystore.getEntry("RSA-private-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }

    private void createDESedeKey(){
        javax.crypto.KeyGenerator keyGen = null;
        try {
            keyGen = javax.crypto.KeyGenerator.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyGen != null;
        keyGen.init(168); // for example
        SecretKey secretKey = keyGen.generateKey();
        keystore.storeKey(secretKey.getEncoded(), "DESede-key");
        System.out.println("Stored DESede key: " + Arrays.toString(secretKey.getEncoded()));
    }

    private byte[] getDESedeKey(){
        try {
            Key key = keystore.getEntry("DESede-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }

    private void createBlowfishKey(){
        javax.crypto.KeyGenerator keyGen = null;
        try {
            keyGen = javax.crypto.KeyGenerator.getInstance("Blowfish");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyGen != null;
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        keystore.storeKey(secretKey.getEncoded(), "Blowfish-key");
        System.out.println("Stored Blowfish key: " + Arrays.toString(secretKey.getEncoded()));
    }

    private byte[] getBlowfishKey(){
        try {
            Key key = keystore.getEntry("Blowfish-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }

    private void createSharedSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        String sharedSecret = "VonderWiegebiszurBahre";
        KeySpec spec = new PBEKeySpec(sharedSecret.toCharArray(), "FloriansamWerk".getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        keystore.storeKey(secret.getEncoded(), "shared-key");
        System.out.println("Stored shared key: " + Arrays.toString(secret.getEncoded()));
    }

    protected byte[] getSharedSecret(){
        try {
            Key key = keystore.getEntry("shared-key", "FreshAvocado".toCharArray());
            return key.getEncoded();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "Failure".getBytes();
    }


}
