package org.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class SupServer {
    
  public class SupClientHandler implements Runnable {
    BufferedReader reader;
    String clientname;
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
      String message;
      try{
        message = reader.readLine();
        while(message != null) {
          System.out.println("read" + message + " from " + Thread.currentThread().getName());
          
          // at this point, for the first message only, we'll need to parse the message
          // for a username and add it to contact list, like:
          String clientname = "temporaryName"; // TODO obviously need to parse this from the first msg
          if (Contacts.getInstance().hasContact(clientname)) {
        	  // TODO return error, username taken,end connection
          }
          else {
        	  this.clientname = clientname;
        	  Contacts.getInstance().addContact( clientname, new PrintWriter(sock.getOutputStream()) );
          }
          
          // for all chat messages, send to the destination
          String toname = "TODO"; // need to parse that out of the chat message
          tellSomeone(message, this.clientname, toname );
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public void start () {
    try{
      ServerSocket supServer = new ServerSocket(3000);
      
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
    if (Contacts.getInstance().hasContact(toname)) {
      try {
            PrintWriter writer = Contacts.getInstance().getContact(toname);
            writer.println(message);
            writer.flush();
          } catch (Exception ex) { ex.printStackTrace(); }
      }
    }
  
  public static void main(String[] args) {
    SupServer server = new SupServer();
    server.start();
  }
}