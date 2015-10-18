package org.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Hello world!
 *
 */
public class Utils 
{
    public static final String SUCCESS_STS = "status 000 OK";
    public static final String FAIL_INTERNAL = "status 200 Internal Error";
    public static final String FAIL_LOGIN_USERNAME_TAKEN = "status 101 Username Taken";
    public static final String FAIL_USER_NOT_ONLINE = "status 104 User Not Online";

    /**
     * 
     * @return Life, the universe, everything
     */
    public int get42( )
    {
        return 42;
    }

    /**
     * Send message out provided printwriter, terminated with EOT.
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
     * @return msg - received message
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
}
