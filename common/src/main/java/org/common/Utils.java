package org.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Common utilities and code shared for both the client and server.
 *
 */
public class Utils
{
    public static final String SUCCESS_STS = "status 000 OK";
    public static final String FAIL_INTERNAL = "status 200 Internal Error";
    public static final String FAIL_LOGIN_USERNAME_TAKEN = "status 101 Username Taken";
    public static final String FAIL_USER_NOT_ONLINE = "status 104 User Not Online";

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
            System.out.println("Failed to receive message.");
            e.printStackTrace();
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
     * Given a plaintext password, return the hash for storage and verification.
     */
    public static int hashPass(String plaintext) {
        // Note, it's possible for this to change across Java versions, so as is
        // there's a possibility we swap underlying Java versions and users won't
        // be able to log in.
        return plaintext.hashCode();
    }
}
