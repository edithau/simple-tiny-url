package acme;


import acme.store.URLStore;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.Base64;

public class SimpleTinyURL {
    private static final int KEY_SPACE = 6;
    private static final int STORE_CAPACITY=10;
    private static final URLStore store = new URLStore(STORE_CAPACITY);


    // return the longURL matches the shortURL.  return null if the decode process failed.
    public static String decode(String shortURL)  {
        return store.get(shortURL);
    }


    public static String encode(String longURL) {
        String normalizedLongURL = longURL;

        // remove trailing slash
        if (longURL.endsWith("/")) {
            normalizedLongURL = longURL.substring(0, longURL.length() - 1);
        }
        String shortURL = md5Hash(normalizedLongURL);
        store.put(shortURL, normalizedLongURL);
        return shortURL;
    }

    private static String md5Hash(String longURL) {
        byte[] b = DigestUtils.md5(longURL.getBytes());
        return new String(Base64.getEncoder().encode(b)).substring(0, KEY_SPACE);
    }
}
