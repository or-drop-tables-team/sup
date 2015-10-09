package org.server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

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
    public void testApp()
    {
        assertTrue( true );
    }
    
    // Test the contacts works
    public void testContactsAdd()
    {
    	PrintWriter pw;
		try {
			// normally this will be gotten from a socket, but that's
			// ok for this test.
			pw = new PrintWriter( new FileOutputStream("tmp.txt") );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// fail and bail
			assertTrue(false);
			return;
		}
    	Contacts contacts = Contacts.getInstance();
    	
    	// Initially there is no one in the contact book
    	assertFalse( contacts.hasContact("steve"));
    	
    	// So we should be able to add one
    	assertTrue( contacts.addContact("steve", pw));
    	
    	// And now there is one!
    	assertTrue( contacts.hasContact("steve"));
    	
    	// We should be denied when trying to add again, no overwrite.
    	assertFalse( contacts.addContact("steve", pw));
    }
}
