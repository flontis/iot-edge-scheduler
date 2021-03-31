package ResourceAdministrator.KeyStorage;

import java.security.KeyStore;
import java.security.KeyStoreException;

public class KeyStorage {

    public KeyStorage() {
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
