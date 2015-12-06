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
    
    // Test our username checker
    public void testValidUsernameCheck() {
        // Try some valid usernames
        assertTrue(Utils.isValidUsername("michael"));
        assertTrue(Utils.isValidUsername("m1cha3l"));
        assertTrue(Utils.isValidUsername("SuperUser"));
        assertTrue(Utils.isValidUsername("a"));
        
        // These should all be not valid
        assertFalse(Utils.isValidUsername(""));
        assertFalse(Utils.isValidUsername("mike.smith"));
        assertFalse(Utils.isValidUsername("mike smith"));
        assertFalse(Utils.isValidUsername("M1ke Smith"));
        assertFalse(Utils.isValidUsername("steve."));
    }
    
    // Test our password checker
    public void testValidPasswordCheck() {
        // Try some valid passwords
        assertTrue(Utils.isValidPassword("michael."));
        assertTrue(Utils.isValidPassword("m!cha3l"));
        assertTrue(Utils.isValidPassword("secr3t"));
        assertTrue(Utils.isValidPassword("a"));
        assertTrue(Utils.isValidPassword("secret_password!"));
        assertTrue(Utils.isValidPassword("p@ssword"));
        
        // These should all be not valid
        assertFalse(Utils.isValidPassword(""));
        assertFalse(Utils.isValidPassword("pass word"));
        assertFalse(Utils.isValidPassword("password*"));
    }
}
