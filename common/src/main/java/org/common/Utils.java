package org.common;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;


/**
 * Common utilities and code shared for both the client and server.
 *
 */
public class Utils
{
    public static final String SUCCESS_STS = "status 000 OK";
    public static final String FAIL_INTERNAL = "status 200 Internal Error";
    public static final String FAIL_LOGIN_USERNAME_TAKEN = "status 111 Username Taken";
    public static final String FAIL_LOGIN_USERNAME_INVALID = "status 112 Username Invalid";
    public static final String FAIL_LOGIN_PASSWORD_INVALID = "status 113 Password Invalid";
    public static final String FAIL_LOGIN_PERMISSION_DENIED = "status 212 Permission Denied";
    public static final String FAIL_USER_NOT_ONLINE = "status 131 User Not Online";

    /**
     * Does nothing but provide an easy way to test our test harness.
     *
     * @return Life, the universe, everything
     */
    public int get42( )
    {
        return 42;
    }

    /**
     * Send message out provided printwriter, terminated with EOT.
     *
     * @param out - the output stream for the connected socket associated with the user
     * @param msg - formatted message to send
     *
     * @return integer 0 on success, -1 on fail
     */
    public static int sendMessage(PrintWriter out, String msg) {

        int EOT = 0x04;

        msg = msg + Character.toString((char) EOT);
        out.print(msg);
        out.flush();

        return 0;
    }

    /**
     * Receive an EOT terminated message.
     *
     * @param - in - buffered reader for socket to receive on
     *
     * @return msg - received message without the EOT character
     */
    public static String receiveMessage(BufferedReader in) {

        String message = "";
        int EOT = 0x04;
        int character = 0;

        try {
            // read until failure or until end of message
            while((character = in.read()) != -1) {
                if(character == EOT) {
                    // that's the end of message
                    break;
                }
                message = message + Character.toString((char) character);
            }
        } catch (IOException e) {
        	System.out.println("Failed to receive message. Make sure to provide proper certificate store.");
            // error case return empty string
            return "";
        }

        // now we have the message read in
        return message;
    }

    /**
     * Given a protocol message, tokenize it into a TokenPair. This
     * just means that the first word is split out from the rest, and a tuple
     * of the first and rest is returned.
     *
     * @param str - the entire protocol message to tokenize
     *
     * @return TokenPair of the tokenized strings
     */
    public static TokenPair tokenize(String str) {
        String first = str.substring(0, str.indexOf(' '));
        String rest = str.substring(str.indexOf(' ') + 1);
        TokenPair tp = new TokenPair(first, rest);
        return tp;
    }

    /**
     * Given a plaintext password, return the SHA256 hash for storage and verification.
     * 
     * @param plaintext - String to calculate SHA-256 hash of
     * 
     * @return hex string of SHA-256 hash. Empty string on error, which means the algorithm
     * was not found. Since we do not vary the algorithm, this is never expected.
     */
    public static String hashPass(String plaintext) {
        // SHA 256 is a 32-byte output, which is too large to store as an integer
        // in sqlite (max 8 bytes). We could truncate, instead we'll convert to
        // String and store characters.
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERR: Unable to calculate hash, Algorithm not found.");
            return "";
        }
        md.update(plaintext.getBytes());
        byte[] result = md.digest();
        // Now we have the hash bytes in result, format as a string and return.
        return DatatypeConverter.printHexBinary(result);
    }
    
    /**
     * Check of whether a username is valid.
     * 
     * @param username - String of the username to check
     * 
     * @return boolean true if OK, false if not
     */
    public static boolean isValidUsername(String username) {
        // We'll use a single regular expression to allow only regular characters and
        // digits for the username
        return username.matches("[a-zA-Z0-9]+");
    }
    
    /**
     * Check of whether a password is valid.
     * 
     * @param password - String of the password to check
     * 
     * @return boolean true if OK, false if not
     */
    public static boolean isValidPassword(String password) {
        // Password is more permissive than username, allow some punctuation
        // as well.
        return password.matches("[a-zA-Z0-9_!.,@]+");
    }
}
