package org.client;

import java.io.BufferedReader;

import org.common.Utils;

public class MessageReceiver implements Runnable {

    private BufferedReader in;
    private volatile boolean running;

    MessageReceiver( BufferedReader insock ) {
        this.in = insock;
    }
    
    public void run() {
        this.running = true;
        // while the client is running, read message from the server and display
        // for the user.
        while( running ) {
            String incomingMessage = Utils.receiveMessage(this.in);
            if( incomingMessage.equals("") ) {
                System.out.println("Connection with server lost!");
                break;
            }
            
            // decide what message this is
            String cmd = incomingMessage.substring(0, incomingMessage.indexOf(' '));
            String rest = incomingMessage.substring(incomingMessage.indexOf(' ') + 1);
            if(cmd.equals("status")) {
                // TODO somehow handle status messages in the normal course of operations
                // probably move the "statusOk()" method from main client app to here
                String code = rest.substring(0, rest.indexOf(' '));
                if(!code.equals("000")) {
                    System.out.println("Error: " + rest);
                }
            } else if(cmd.equals("recv")){
                String fromuser = rest.substring(0, rest.indexOf(' '));
                String chatMsg = rest.substring(incomingMessage.indexOf(' ') + 1);
                System.out.println("Got chat message from " + fromuser + ": " + chatMsg );
            } else {
                System.out.println("Unknown command message: " + cmd);
            }
        }
    }
    
    public void stopRunning() {
        this.running = false;
    }

}
