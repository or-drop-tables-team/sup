package org.server;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.*;

import org.common.Utils;

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
        File f = new File("tmp_test.txt");
        try {
            // normally this will be gotten from a socket, but that's
            // ok for this test.
            pw = new PrintWriter( new FileOutputStream( f ) );
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

        // clean up that fil
        try {
            Files.delete( f.toPath() );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Test the auth works
    public void testCheckAuthentication() {
        // Create a test DB to use.
        String dbfile = "testdb.db";
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
            System.out.println("Opened test authentication database.");
            // Now make a table
            stmt = c.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USERS (NAME TEXT NOT NULL, PASSHASH INT NOT NULL);");
            stmt.executeUpdate("INSERT INTO USERS (NAME, PASSHASH) VALUES ( \"steve\", " + Utils.hashPass("password") + ");");
            // Close up
            stmt.close();
            c.close();
        }
        catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("Failed to operate on test auth database!");
        }

        // Now verify that our authentication works for "steve/password".
        assertTrue(SupServer.authenticateUser("steve", "password", "testdb.db"));
        // And a couple that don't.
        assertFalse(SupServer.authenticateUser("other", "password", "testdb.db"));
        assertFalse(SupServer.authenticateUser("steve", "notpassword", "testdb.db"));

        File f = new File(dbfile);
        // clean up that file
        try {
            Files.delete( f.toPath() );
        } catch (IOException e) {
            System.out.println("Failed to clean up test database.");
        }
    }
}
