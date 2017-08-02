package acme;


import acme.store.DBStore;
import acme.store.InMemoryStore;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Base64;

public class SimpleTinyURL {
    private static SimpleTinyURL instance;
    private static final Logger log = Logger.getLogger(SimpleTinyURL.class);
    private static final int KEY_SPACE = 6;
    private static final int CACHE_CAPACITY=10;
    private InMemoryStore<String, String> inMemStore;
    private DBStore dbStore;

    private SimpleTinyURL() {
        inMemStore = new InMemoryStore<String, String>(CACHE_CAPACITY);
        try {
            dbStore = new DBStore();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to the database store", e);
        }
    }

    protected SimpleTinyURL(DBStore dbStore) {
        this.inMemStore = new InMemoryStore<String, String>(CACHE_CAPACITY);;
        this.dbStore = dbStore;
        instance = this;
    }

    public static SimpleTinyURL getInstance() {
        if (instance == null) {
            instance = new SimpleTinyURL();
        }
        return instance;
    }

    // return the longURL matches the shortURL.  return null if the decode process failed.
    public String decode(String shortURL)  {
        try {
            String longURL = inMemStore.get(shortURL);
            if (longURL == null) {
                longURL = dbStore.get(shortURL);
                if (longURL != null)
                    inMemStore.put(shortURL, longURL);
            }
            return longURL;
        } catch (Exception e) {
            return null;
        }
    }


    public String encode(String longURL) {
        try {
            String normalizedLongURL = longURL;

            // remove trailing slash
            if (longURL.endsWith("/")) {
                normalizedLongURL = longURL.substring(0, longURL.length() - 1);
            }
            String shortURL = md5Hash(normalizedLongURL);

            // must put dbStore first before inMemStore
            // to take advantage of the db unique key constraint and therefore no need to synchronize the 2 stores on put()
            String dbStoreLongURL = dbStore.put(shortURL, normalizedLongURL);
            String inMemStoreLongURL = inMemStore.put(shortURL, normalizedLongURL);
            if (!dbStoreLongURL.equals(inMemStoreLongURL)) {
                // this could happen if there are staled key value pairs in the db or if there is a key collision.
                // TODO: retire the collided key forever
                log.error("Key collision: key=" + shortURL + "  DB store URL= " + dbStoreLongURL + "   In memory store URL= " + inMemStoreLongURL);
                return null;
            }

            return shortURL;
        } catch (Exception e) {
            log.error("Cannot encode url " + longURL, e);
            return null;
        }
    }

    public static String md5Hash(String longURL) {
        byte[] b = DigestUtils.md5(longURL.getBytes());
        return new String(Base64.getEncoder().encode(b)).substring(0, KEY_SPACE);
    }
}
