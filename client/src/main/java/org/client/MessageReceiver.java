package org.client;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import org.common.TokenPair;
import org.common.Utils;

/**
 * Provides logic to continuously receive messages from the server and provide notification to
 * the user. Done in another thread as to not disrupt the user's experience.
 *
 */
public class MessageReceiver implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running;
    JTextArea chatTxtField;
    JLabel errLabel;

    MessageReceiver( BufferedReader insock, Printwriter.outsock, JTextArea jTextArea, JLabel jLabel ) {
        this.in = insock;
        this.out = outsock;
        this.chatTxtField = jTextArea;
        this.errLabel = jLabel;
    }

    /**
     * This is the logic run by this object once the thread is started.
     */
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
            TokenPair cmdTuple = Utils.tokenize(incomingMessage);
            if(cmdTuple.first.equals("status")) {
                // TODO somehow handle status messages in the normal course of operations
                // probably move the "statusOk()" method from main client app to here
            	
            	// Reset the error label message for every thread
            	this.errLabel.setText("");
                TokenPair statusTuple = Utils.tokenize(cmdTuple.rest);
                if(!statusTuple.first.equals("000")) {
                	System.out.println("Error: " + statusTuple.rest);
                	this.chatTxtField.setText(this.chatTxtField.getText());
                	
                	// Set the text of error label if there is any error occurs
                	this.errLabel.setText("ERR: " + statusTuple.rest);
                }
            } else if(cmdTuple.first.equals("recv")){
                TokenPair userChatTuple = Utils.tokenize(cmdTuple.rest);
                System.out.println("Got chat message from " + userChatTuple.first + ": " + userChatTuple.rest);
                this.chatTxtField.setText(this.chatTxtField.getText() + "\n" + userChatTuple.first + " -> " + userChatTuple.rest);
                 
                //In future there will be a variety of ways to turn down the message
                try {
    				//Send the server a message saying message was received
    				Utils.sendMessage(out, Utils.MSG_RECEIVED);
                } catch (Exception e) {
                    System.out.println("Failed to send confirmation message to new client");
                    e.printStackTrace();
                }
            } else {
                // Ignore it.
                System.out.println("Unknown command message: " + cmdTuple.first);
            }
        }
    }
    

    /**
     * Called when this thread should stop executing, probably at the termination of the
     * application.
     *
     * @return void
     */
    public void stopRunning() {
        this.running = false;
    }

}
