package org.client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
    private final int EOT = 0x04;
    
    public static void main( String[] args )
    {
        App client = new App();
        client.start();
        
        //I think my logoff() should in a new thread().run();
        // temporary, we will run this in a loop
        Console console = System.console();
        console.readLine("Any key to exit.");
    }
    
    // start the client logic
    void start() {
        
        try {
            this.sock = new Socket(serverAddress, serverPort);
            out = new PrintWriter(this.sock.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Now we know the server is online.
    
        // get their desired username
        Console console = System.console();
        String username = console.readLine("Enter your username: ");
        String loginMsg = createLoginMessageForUser(username);
        sendMessage(loginMsg);
    }
    
    // Already know the address of the server, send the login message
    private void sendMessage(String msg) {
        msg = msg + Character.toString((char) EOT);
        out.print(msg);
        out.flush();
    }
    
    public String createLoginMessageForUser(String username) {
        return "login " + username;
    }
    
    /**
     * log off from the server
	 *
	 * @param 
	 * 	username - the user log off from the server
     * */
	private void logoff(String username) {
		String logoffSignal = "logoff " + username;
		sendMessage(logoffSignal);
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
