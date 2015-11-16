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
    private String uName = "";

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
                uName = username;
            }
            else {
                System.out.println("Failed to login as \"" + username + "\"");	
            }
        }
        
        // Now that we're logged in, start our message receiver to
        // constantly receive and display message from the server for the client.
        this.receiver = new MessageReceiver( this.in, this.out );
        Thread t = new Thread(this.receiver);
        t.start();
        
        // TODO temporary, replace with Colin's smarter code
        // pretend there's a user logged on named "user"
        
        String command = "start";
        String currentContact = "";

        //Run a loop indefinitely - reading messages from server in another thread
        while(command != "quit")	{

        try	{
        	String input = console.readLine();
        	String firstLetter = input.substring(0, 1);
        	
        	if (firstLetter == "/" || firstLetter == "-")	{
        		//Method found on stack overflow to get the first word of a string
        		//command is the first word, cutting out the first character, which is / or -
        		
        		if (input.contains(" ") )	{
        			//Check to make sure there are multiple words
        			command = input.substring(1, input.indexOf(" ") ).toLowerCase();
        		}	else	{
        			//Otherwise the command is just the entire string
        			command = input.substring(1).toLowerCase();
        		}

        		//Switch on the command
       /*	Apparently Java (at least my version) does not support switch on strings
        * 	will use a large if/else block instead
        *
        *		switch(command)	{
        *			case "message":
        *				//currentContact becomes everything after the command
        *				//Should be only 1 word, but no check for it yet
        *				currentContact = input.substring(input.indexOf(" ") + 1);
        *				break;
        *			case "quit":
        *				//Call logoff procedures + this will exit loop next time it tries to execute
        *				//Can also call logoff procedures after the loop, to exit properly no matter why this loop might break
        *				break;
        *			case "contacts":
        *				//Call a get contacts procedure
        *				break;
        *			case "help":
        *			case "commands":
        *				//Print the list of available commands
        *				System.out.println(	"/message <username>\t\tChange your current contact to <username>\n" +
        *							"/quit\t\t\t\tExit the program\n" +
        *							"/contacts\t\t\tGet a list of online users\n" +
        *							"/commands\t\t\tDisplays a list of available user commands"	);
        *				break;
        *			default:
        *				System.out.println("Command not recognized. Type /commands for help.");
        *		}	//End switch
        */
        		//IF ELSE EVERTYTHING
        		if (command == "message")	{
        			//currentContact becomes everything after the command
        	        //Should be only 1 word, but no check for it yet
        			currentContact = input.substring(input.indexOf(" ") + 1);
        		}
        		else if (command == "quit")	{
        			//Call logoff procedures + this will exit loop next time it tries to execute
        	        //Can also call logoff procedures after the loop, to exit properly no matter why this loop might break
        	       logoff(out, uName);
        		}
        		else if (command == "contacts")	{
        			//Call a get contacts procedure
        		}
        		else if (command == "help" || command == "help")	{
        			//Print the list of available commands
        	        System.out.println(	"/message <username>\t\tChange your current contact to <username>\n" +
        	        					"/quit\t\t\t\tExit the program\n" +
        	        					"/contacts\t\t\tGet a list of online users\n" +
        	        					"/commands\t\t\tDisplays a list of available user commands"	);
        		}
        		else {
        			System.out.println("Command not recognized. Type /commands for help.");
        		}
        		
        	}	//End if command starts with a / or -
        	else	{
        		//Else fires if the input is just a message
        		//Send the entire string as a message to the current contact
        		sendChatMessage(currentContact, input);
        	}
        }	//End Try block
        catch (Exception e)	{
        	e.printStackTrace();
        }

        }	//End while loop
        
        
       /*	Edited out due to old loop
        *  while(true) {
        *
        *    String user = console.readLine("To username: ");
        *    String msg = console.readLine("Message: ");
        *    sendChatMessage(user, msg);
        *	}
    	*/
        
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
		String logoffSignal = "quit " + username;
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
  
