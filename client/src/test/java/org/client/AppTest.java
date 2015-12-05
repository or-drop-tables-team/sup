package org.client;

import org.common.Utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCommonUtilsGet42()
    {
    	Utils util = new Utils();
        assertEquals( 42, util.get42());
    }
    
    // test the login message looks right
    public void testLoginMessageCreate()
    {
    	ClientApp app = new ClientApp();
    	String loginMsg = app.createLoginMessageForUserPass("foobaz", "P@ssw0rd");
    	assertEquals( loginMsg, "login foobaz P@ssw0rd" );
    }

    // test the register message looks right
    public void testRegistrationMessageCreate()
    {
    	ClientApp app = new ClientApp();
    	String regMsg = app.createRegistrationMessageForUserPass("foobaz", "P@ssw0rd");
    	assertEquals( regMsg, "register foobaz P@ssw0rd" );
    }
}
