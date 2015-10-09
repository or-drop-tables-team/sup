package org.server;

import java.io.*;
import java.net.*;

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
            int character = 0;
            try {
                while((character = reader.read()) != -1) {

                    String message = "";

                    // Go until EOT character, end of transmission
                    while(character != 0x04) {
                        message = message + Character.toString((char) character);
                        character = reader.read();
                    }

                    // now we have the message read in
                    System.out.println("read \"" + message + "\" from " + Thread.currentThread().getName());

                    // at this point, for the first message only, we'll need to parse the message
                    // for a username and add it to contact list, like:
                    // Only the first time expect this!
                    if(clientname.isEmpty()) {
                        String requestedName = message.split(" ")[1];
                        if (Contacts.getInstance().hasContact(requestedName)) {
                            // TODO return error, username taken,end connection
                            System.out.println("Contact name taken: " + requestedName );
                        }
                        else {
                            clientname = requestedName;
                            Contacts.getInstance().addContact( clientname, new PrintWriter(sock.getOutputStream()) );
                            System.out.println("New contact name: " + clientname );
                        }
                    }

                    // for all chat messages (and only chat messages), send to the destination
                    String toname = "TODO"; // need to parse that out of the chat message
                    tellSomeone(message, this.clientname, toname );
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
        try{
            ServerSocket supServer = new ServerSocket(this.port);

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
    }

    // send the message to the specific user. find the socket by searching userlist
    // this needs to be thread safe.
    public void tellSomeone(String message, String fromname, String toname) {
        System.out.println("Forwarding \"" + message + "\" from " + fromname + " to " + toname);
        if (Contacts.getInstance().hasContact(toname)) {
            try {
                PrintWriter writer = Contacts.getInstance().getContact(toname);
                writer.println(message);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}