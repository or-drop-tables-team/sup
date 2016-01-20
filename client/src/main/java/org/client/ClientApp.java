package org.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.UnknownHostException;

import org.common.Utils;

/**
 * The main class for the client application. 
 *
 */
public class ClientApp 
{
    private static final String serverAddress = "45.55.88.120";
    private static final int serverPort = 3000;
    private SSLSocket sock;
    private PrintWriter out;
    private BufferedReader in;
    private MessageReceiver receiver;
    private String username;

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
        	SSLSocketFactory sslSockFact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            this.sock = (SSLSocket) sslSockFact.createSocket(serverAddress, serverPort);
            this.out = new PrintWriter(this.sock.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Host \"" + serverAddress + "\" is unknown.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("Failed to connect. Is server running at \"" + serverAddress + ":" + serverPort + "\"?");
            return;
        }

        // Now we know the server is online.

        // Log on
        LoginWindow loginWindow = new LoginWindow(this);
        loginWindow.setModal(true);
        loginWindow.setVisible(true);
        
        // TODO instead of going directly into a chat window here, we should open
        // a list of available contacts. Then clicking on a contact opens a dialog
        // with that user. We could keep a map of contacts -> windows (much like 
        // we do for contacts -> sockets in the server) to dispatch to the correct
        // window in MessageReceiver.
        
        // Now enter into a chat window.
        ChatWindow chatWindow = new ChatWindow(this);
        // Have the title of the window dictate the user name of the destination user
        // and the logged in user. For now the destination user is effectively everyone.
        chatWindow.setTitle("[" + this.username + "] All");
        chatWindow.setVisible(true);
        
        // Now that we're logged in, start our message receiver to
        // constantly receive and display message from the server for the client.
        this.receiver = new MessageReceiver( this.in, chatWindow.getChatBox(), chatWindow.getErrorMessage() );
        Thread t = new Thread(this.receiver);
        t.start();
    }
    
    /**
     * Try registering a new account
     * 
     * @param Username - to log on with
     * @param Password - password for user
     * 
     * @return true if registration successful, else false
     */
    public boolean register(String username, String password) {
        // Reserver their desired username. 
        String regsiterMsg = createRegistrationMessageForUserPass(username, password);
        Utils.sendMessage(this.out, regsiterMsg);
        if(statusOk()) {
            System.out.println("Successful registration as \"" + username + "\"");
            this.username = username;
            return true;
        }
        else {
            System.out.println("Failed to register as \"" + username + "\"");  
            return false;
        }
    }

     /**
     * Try logon with a username
     * 
     * @param Username - to log on with
     * @param Password - password associated with username
     * 
     * @return true if login successful, else false
     */
    public boolean login(String username, String password) {
        // get their desired username. 
        String loginMsg = createLoginMessageForUserPass(username, password);
        Utils.sendMessage(this.out, loginMsg);
        if(statusOk()) {
            System.out.println("Successful login as \"" + username + "\"");
            this.username = username;
            return true;
        }
        else {
            System.out.println("Failed to login as \"" + username + "\"");  
            return false;
        }
    }

    /**
     * Simple function to craft a message for registration.
     * 
     * @param username - desired username
     * @param password - password for username
     * 
     * @return the proper registration message
     */
    public String createRegistrationMessageForUserPass(String username, String password) {
        return "register " + username + " " + password;
    }

    /**
     * Simple function to craft a message for logging in.
     * 
     * @param username - desired username
     * @param password - password for username
     * 
     * @return the login message
     */
    public String createLoginMessageForUserPass(String username, String password) {
        return "login " + username + " " + password;
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
	boolean sendChatMessage(String username, String msg) {
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
  
