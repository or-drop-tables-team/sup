package org.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class UtilsTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UtilsTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    /**
     * Test that strings are tokenized as expected.
     */
    public void testTokenizer()
    {
        String first = "something";
        String rest = "bla foo baz !?";

        // Test the basic one, single spaces.
        TokenPair tp = Utils.tokenize(first + " " + rest);
        assertEquals(tp.first, first);
        assertEquals(tp.rest, rest);

        // Add multiple spaces. They should not be trimmed.
        tp = Utils.tokenize(first + "    " + rest);
        assertEquals(tp.first, first);
        assertEquals(tp.rest, "   " + rest);
    }
}
