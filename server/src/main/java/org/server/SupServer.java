package org.server;

import java.io.*;
import java.net.*;

import org.common.Utils;

public class SupServer {

    private int port;

    public SupServer(int port) {
        this.port = port;
    }

    public class SupClientHandler implements Runnable {
        BufferedReader reader;
        String clientname = "";
        Socket sock;

        public SupClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            // Run until connection is broken or user terminates
            String message = "";
            try {
                // if connection is broken, receiveMessage will return empty string.
                while((message = Utils.receiveMessage(this.reader)) != "") {

                    // now we have the message read in. this might be more verbose than we want.
                    System.out.println("read \"" + message + "\" from " + Thread.currentThread().getName());

                    // at this point, for the first message only, we'll need to parse the message
                    // for a username and add it to contact list, like:
                    // Only the first time expect this!
                    if(clientname.isEmpty()) {
                        String requestedName = message.split(" ")[1];
                        if (Contacts.getInstance().hasContact(requestedName)) {
                            // return error, username taken
                            System.out.println("Contact name taken: " + requestedName );
                            Utils.sendMessage(new PrintWriter(sock.getOutputStream()), Utils.FAIL_LOGIN_USERNAME_TAKEN);
                        }
                        else {
                            // success, add them to the collection of online contacts.
                            clientname = requestedName;
                            System.out.println("New contact name: " + clientname );
                            Contacts.getInstance().addContact( clientname, new PrintWriter(sock.getOutputStream()) );
                            try {
                                Utils.sendMessage(Contacts.getInstance().getContact(clientname), Utils.SUCCESS_STS);
                            } catch (Exception e) {
                                System.out.println("Failed to send confirmation message to new client");
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // they've already successfully logged in.
                        // if it's not a login message, it's a chat message. parse it and forward.
                        String toname = "TODO"; // need to parse that out of the chat message
                        tellSomeone(message, this.clientname, toname );
                    }
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // if they were actually authenticated and left, log them off..
            if(!clientname.isEmpty()) {
                System.out.println(clientname + " is no longer online.");
                // remove user from active contacts
                Contacts.getInstance().removeContact(clientname);
            }
        }
    }

    public void start () {
        ServerSocket supServer = null;
        try{
            supServer = new ServerSocket(this.port);

            // everytime receive a socket from a server, create a new thread. put the printwriter into userlist and set the name for client
            while(true) {
                Socket supClient = supServer.accept();

                Thread t = new Thread(new SupClientHandler(supClient));
                t.start();
                System.out.println("Received a new connection");
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        // all done, clean up.
        try {
            supServer.close();
        } catch (IOException e) {
            System.out.println("Failed to clean up socket, finishing anyway.");
            e.printStackTrace();
        }
    }

    // send the message to the specific user. find the socket by searching userlist
    // this needs to be thread safe.
    public void tellSomeone(String message, String fromname, String toname) {
        if (Contacts.getInstance().hasContact(toname)) {
            try {
                PrintWriter writer = Contacts.getInstance().getContact(toname);
                String msg = createFormattedChatMessage(message, fromname, toname);
                Utils.sendMessage(writer, msg);
            } catch (Exception ex) { 
                ex.printStackTrace(); 
            }
        }
    }

    /**
     * 
     * @param message
     * @param fromname - sender username
     * @param toname - recipient username
     * @return string of formatted message, ready for sending
     */
    private String createFormattedChatMessage(String message, String fromname, String toname ) {
        // TODO 
        return "TODO make this valid " + message;
    }
}