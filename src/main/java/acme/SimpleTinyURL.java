package acme;


import acme.store.DBStore;
import acme.store.InMemoryStore;
import acme.store.StoreException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

public class SimpleTinyURL {
    private static final int MAX_COLLISION = 5;
    private static SimpleTinyURL instance;
    private static final Logger log = Logger.getLogger(SimpleTinyURL.class);
    private static final int KEY_SPACE = 7;
    private static final int CACHE_CAPACITY=1000;
    private InMemoryStore<String, String> inMemStore;
    private DBStore dbStore;
    private static final Charset iso8859 = Charset.forName("ISO-8859-1");

    private SimpleTinyURL() {
    }

    protected SimpleTinyURL(InMemoryStore<String, String> inMemStore, DBStore dbStore) {
        this.inMemStore = inMemStore;
        this.dbStore = dbStore;
    }

    protected void setInMemStore(InMemoryStore<String, String> inMemStore) {
        this.inMemStore = inMemStore;
    }

    protected void setDbStore(DBStore dbStore) {
        this.dbStore = dbStore;
        instance = this;
    }

    public static SimpleTinyURL getInstance() {
        if (instance == null) {
            instance = new SimpleTinyURL();
            instance.setInMemStore(new InMemoryStore<String, String>(CACHE_CAPACITY));
            try {
                instance.setDbStore(new DBStore());
            } catch (IOException e) {
                throw new RuntimeException("Cannot connect to the database store", e);
            }
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
            log.info("decoded {" + shortURL + ", " + longURL + "}");
            return longURL;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }



    public String encode(String longURL) {
        try {
            int collisionCounter = 0;
            String normalizedLongURL = longURL;
            String shortURL;

            // remove trailing slash
            if (longURL.endsWith("/")) {
                normalizedLongURL = longURL.substring(0, longURL.length() - 1);
            }

            do {
                shortURL = md5Hash(normalizedLongURL);

                // must put dbStore first before inMemStore
                // to take advantage of the db unique key constraint and therefore no need to synchronize the 2 stores on put()
                String dbStoreLongURL = dbStore.put(shortURL, normalizedLongURL);
                if (!dbStoreLongURL.equals(normalizedLongURL)) {
                    collisionCounter++;
                    normalizedLongURL = collisionCounter + ":" + normalizedLongURL;
                    log.warn("SimpleTinyURL.encode() Key collision: key=" + shortURL + "  DB store URL= " + dbStoreLongURL + "   normalizedLongURL= " + normalizedLongURL);
                } else {
                    inMemStore.put(shortURL, normalizedLongURL);
                    break;
                }
            } while (collisionCounter > 0 && collisionCounter < MAX_COLLISION);

            if (collisionCounter == MAX_COLLISION) {
                throw new StoreException("Too many collisions for url =>" + longURL);
            }
            log.info("encoded {" + shortURL + ", " + longURL + "}");
            return shortURL;
        } catch (Exception e) {
            log.error("Cannot encode url " + longURL + ". " + e.getMessage(), e);
            return null;
        }
    }

    public static String md5Hash(String longURL) {
        byte[] b = DigestUtils.md5(longURL.getBytes(iso8859));
        return new String(Base64.getEncoder().encode(b)).substring(0, KEY_SPACE);
    }
}
