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
 * The main class for the client application. 
 *
 */
public class ClientApp 
{
    private static final String serverAddress = "127.0.0.1";
    private static final int serverPort = 3000;
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private MessageReceiver receiver;

    /**
     * Main entry point of the client application.
     * 
     * @param args
     */
    public static void main( String[] args )
    {
        ClientApp client = new ClientApp();
        client.start();
    }

    /**
     * Start the client logic executing. Creates the connections needed and begins
     * accepting input from the user.
     * 
     * @return void
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
        
        // Now that we're logged in, start our message receiver to
        // constantly receive and display message from the server for the client.
        this.receiver = new MessageReceiver( this.in );
        Thread t = new Thread(this.receiver);
        t.start();
        
        // TODO temporary, replace with Colin's smarter code
        // pretend there's a user logged on named "user"
        while(true) {
            String user = console.readLine("To username: ");
            String msg = console.readLine("Message: ");
            sendChatMessage(user, msg);
        }
        
    }

    /**
     * Simple function to craft a message for logging in.
     * 
     * @param username - desired username
     * 
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
        // TODO this should be a part of MessageReceiver
        String msg = Utils.receiveMessage(this.in);
        if(msg.equals(Utils.SUCCESS_STS)) {
            return true;
        } else {
            System.out.println("Error: " + msg);
            return false;
        }
    }
    
    /**
     * Log off from the server. Ideally should wait until the confirmation from the server
     * before terminating the connection.
	 *
	 * @param username - the user log off from the server
	 * 
	 * @return void
     */
	private void logoff(PrintWriter writer, String username) {
		String logoffSignal = "logoff " + username;
		Utils.sendMessage(writer, logoffSignal);
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// stop our listener when logging off
		this.receiver.stopRunning();
		
		System.exit(0);
	}
	
	/**
	 * Send a chat message to the server for delivery.
	 * 
	 * @param username - username of the logged on user
	 * @param msg - properly formatted string representing the message user wants to send
	 */
	private boolean sendChatMessage(String username, String msg) {
	    Utils.sendMessage(this.out, createChatMessage(username, msg));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	    // TODO this needs to check the status, not just return OK
	    return true;
	}
	
	/**
	 * Create a formatted chat message for delivery through the server.
	 * 
	 * @param toUser - username of the destination contact
	 * @param message - user-provided message to be delivered to the end user
	 * 
	 * @return string representing the properly formatted message
	 */
	private String createChatMessage(String toUser, String message) {
	    return "send " + toUser + " " + message;
	}
	
}
  
