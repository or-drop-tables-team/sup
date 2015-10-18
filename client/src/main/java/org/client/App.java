package org.client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.common.Utils;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String serverAddress = "127.0.0.1";
    private static final int serverPort = 3000;
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    
    /**
     * Main entry point of the client application.
     * 
     * @param args
     */
    public static void main( String[] args )
    {
        App client = new App();
        client.start();

        // temporary, we will run this in a loop
        Console console = System.console();
        console.readLine("Any key to exit.");
    }

    /**
     * Start the client logic.
     */
    void start() {

        try {
            this.sock = new Socket(serverAddress, serverPort);
            this.out = new PrintWriter(this.sock.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Host \"" + serverAddress + "\" is unknown.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("Failed to connect. Is server running at \"" + serverAddress + ":" + serverPort + "\"?");
            e.printStackTrace();
            return;
        }

        // Now we know the server is online.

        // get their desired username. want to loop until they find an acceptable
        // username.
        boolean loginSuccessful = false;
        Console console = System.console();
        while(!loginSuccessful) {
            String username = console.readLine("Enter your username: ");
            String loginMsg = createLoginMessageForUser(username);
            Utils.sendMessage(this.out, loginMsg);
            if(statusOk()) {
                System.out.println("Successful login as \"" + username + "\"");
                // we shall stop trying
                loginSuccessful = true;
            }
            else {
                System.out.println("Failed to login as \"" + username + "\"");	
            }
        }
    }

    /**
     * Simple function to craft a message for logging in.
     * 
     * @param username - desired username
     * @return the login message
     */
    public String createLoginMessageForUser(String username) {
        return "login " + username;
    }

    /**
     * Helper to check the status of return messages. Call when expecting
     * a status response from the server.
     * 
     * @return true if OK, false if not
     */
    private boolean statusOk() {
        // first need to get. 
        // NOTE we probably want to eventually add a timeout to this.
        String msg = Utils.receiveMessage(this.in);
        if(msg.equals(Utils.SUCCESS_STS)) {
            return true;
        } else {
            System.out.println("Error: " + msg);
            return false;
        }
    }
    
    /**
     * log off from the server
	 *
	 * @param 
	 * 	username - the user log off from the server
     * */
	private void logoff(PrintWriter writer, String username) {
		String logoffSignal = "logoff " + username;
		Utils.sendMessage(writer, logoffSignal);
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
