package acme;

import junit.framework.TestCase;

public class SimpleTinyURLTest extends TestCase {
    static String longURL = "www.google.com";

    public SimpleTinyURLTest(String name) {
        super( name );
    }

    // url with or without trailing slash should produce the same key
    public void testEncodeWithTrailingSlash()  {
        String encodedNoTrailingSlash = SimpleTinyURL.encode(longURL);
        String longURLWithSlash = longURL + "/";
        String encodedWithTrailingSlash = SimpleTinyURL.encode(longURLWithSlash);

        assertEquals("Error: url with or without trailing slash should produce the same encoding key",
                encodedNoTrailingSlash, encodedWithTrailingSlash );
    }

    // should be able to decode a previously encoded long URL
    // should not be able to decode a non existing shorten URL
    public void testDecode() {
        String expectedLongURL = SimpleTinyURL.decode("nosuch");
        assertNull(expectedLongURL);

        String shortURL = SimpleTinyURL.encode(longURL);
        expectedLongURL = SimpleTinyURL.decode(shortURL);
        assertEquals("Error: decoded URL is not the same as the encoded URL", expectedLongURL, longURL);

    }

}