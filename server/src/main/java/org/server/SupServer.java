package org.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SupServer {

    private int port;

    public SupServer(int port) {
        this.port = port;
    }

    public class SupClientHandler implements Runnable {
        BufferedReader reader;
        PrintWriter writer; 
        String clientname = "";
        Socket sock;

        public SupClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                
                OutputStreamWriter osWriter = new OutputStreamWriter(sock.getOutputStream());
                writer = new PrintWriter(new BufferedWriter(osWriter), true);
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
            	//I think server can also remove contact here and send the list to everyone
            	//removeContact(clientname);
            	//sendList();
            	//return ;
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
    
    /**
     * return the status to the specific one
	 *
	 * @param 
	 * 	status - status info
	 *  toname - the specific one to transfer status
     * */
    public void tellSomeoneStatus(String status, String toname)
    {
    	System.out.println("Return the \"" + status +"\" to " + toname);
    	if (Contacts.getInstance().hasContact(toname)) {
            try {
                PrintWriter writer = Contacts.getInstance().getContact(toname);
                writer.println(status);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    /**
     * return the status to everyone
	 *
	 * @param 
	 * 	status - status info
     * */
	public void tellEveryoneStatus(String status) {
		System.out.println("Return the \"" + status + "\" to all");
		try {
			
			List<PrintWriter> list = Contacts.getInstance().getWriterList();

			for (PrintWriter writer : list) {
				writer.println(status);
				writer.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
     * remove contact from active list and send the online list to everyone
	 *
	 * @param 
	 * 	name - the contact removed from the list
     * */
	public void removeContact(String name)
	{
		Contacts.getInstance().removeContact(name);
		System.out.println(name + "has been removed from the contact");
	}
	
	/**
     * send user list after remove someone
	 *
     * */
	public void sendList()
	{
		try {
			List<String> users = Contacts.getInstance().getUserList();
			List<PrintWriter> list = Contacts.getInstance().getWriterList();
			for(int i=0; i<list.size(); i++)
			{
				PrintWriter writer = list.get(i);
				writer.print(users.toArray());
				writer.flush();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
