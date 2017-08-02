package acme;

import acme.store.DBStore;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SimpleTinyURLTest {
    private static String invalidShortURL = "nosuch";
    private static String longURL = "www.google.com";
    private static DBStore dbStore;
    private static SimpleTinyURL testInstance;
    private static final Logger log = Logger.getLogger(SimpleTinyURL.class);


    @Before
    public void setup() throws Exception {
        String shortURL = SimpleTinyURL.md5Hash(longURL);
        dbStore = mock(DBStore.class);
        when(dbStore.get(shortURL)).thenReturn(longURL);
        when(dbStore.put(shortURL, longURL)).thenReturn(longURL);
        when(dbStore.get(invalidShortURL)).thenReturn(null);

        testInstance = new SimpleTinyURL(dbStore);
    }

    // should return null on decoding an non-existing shorten URL
    @Test
    public void testInvalidShortURL() {
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
        log.info("Test: after SimpleTinyURL encode");
        String returnedLongURL = testInstance.decode(shortURL);
        assertEquals("Error: decoded URL is not the same as the encoded URL", longURL, returnedLongURL);
    }

}