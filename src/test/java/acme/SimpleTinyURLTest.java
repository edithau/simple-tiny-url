package acme;

import acme.store.DBStore;
import acme.store.InMemoryStore;
import acme.store.StoreException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SimpleTinyURLTest {
    private static String longURL, shortURLFromHash;
    private static DBStore dbStore;
    private static InMemoryStore<String, String> inMemStore;
    private static SimpleTinyURL testInstance;
    private static final Logger log = Logger.getLogger(SimpleTinyURL.class);


    @Before
    public void setup() throws StoreException {
        longURL = "www.google.com";
        shortURLFromHash = SimpleTinyURL.md5Hash(longURL);
        inMemStore = new InMemoryStore<String, String>(10);
        inMemStore.put(shortURLFromHash, longURL);
        dbStore = mock(DBStore.class);
        when(dbStore.get(shortURLFromHash)).thenReturn(longURL);
        when(dbStore.put(shortURLFromHash, longURL)).thenReturn(longURL);

        testInstance = new SimpleTinyURL(inMemStore, dbStore);
    }

    // should return null on decoding an non-existing shorten URL
    @Test
    public void testInvalidShortURL() throws StoreException{
        String invalidShortURL = "nosuch";

        when(dbStore.get(invalidShortURL)).thenReturn(null);
        String expectedLongURL = testInstance.decode(invalidShortURL);
        assertNull(expectedLongURL);
    }

    // url with or without trailing slash should produce the same key
    @Test
    public void testEncodeWithTrailingSlash()  {
        String encodedNoTrailingSlash = testInstance.encode(longURL);
        String longURLWithSlash = longURL + "/";
        String encodedWithTrailingSlash = testInstance.encode(longURLWithSlash);

        assertEquals("Error: url with or without trailing slash should produce the same encoding key",
                encodedNoTrailingSlash, encodedWithTrailingSlash );
    }

    // should be able to decode a previously encoded long URL
    @Test
    public void testDecode() {
        String shortURL = testInstance.encode(longURL);
        String returnedLongURL = testInstance.decode(shortURL);
        assertEquals("Error: decoded URL is not the same as the encoded URL", longURL, returnedLongURL);
    }

//     if 2 urls hash to the same key, prepend the incoming url with a prefix to solve collision
//     this test ensure the collision got resolved.
    @Test
    public void testCollision() throws Exception {
        String shortURL = testInstance.encode(longURL);
        assertEquals(shortURLFromHash, shortURL);

        // create collision - map shortURL (key) to some other URL
        String someOtherURL = "www.hello.com";
        when(dbStore.put(shortURL, longURL)).thenReturn(someOtherURL);
        when(dbStore.get(shortURL)).thenReturn(someOtherURL);
        inMemStore.put(shortURL, someOtherURL);

        // verify collision happened.  shortURL now maps to someOtherURL
        String testLongURL = testInstance.decode(shortURL);
        assertEquals(someOtherURL, testLongURL);


        // mock store behavior after collision
        String longURLAfterCollision = "1:" + longURL;
        String shortURLAfterCollision = SimpleTinyURL.md5Hash(longURLAfterCollision);
        when(dbStore.get(shortURLAfterCollision)).thenReturn(longURLAfterCollision);
        when(dbStore.put(shortURLAfterCollision, longURLAfterCollision)).thenReturn(longURLAfterCollision);
        inMemStore.put(shortURLAfterCollision, longURLAfterCollision);

        shortURL = testInstance.encode(longURL);
        assertEquals(shortURLAfterCollision, shortURL);
    }


}