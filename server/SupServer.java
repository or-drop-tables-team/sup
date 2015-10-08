import java.io.*;
import java.net.*;
import java.util.*;

public class SupServer {
  
  // userList is a hashmap which is used to store the user name and its printwriter
  HashMap<String, PrintWriter> userList = new HashMap<String, PrintWriter>();
  int clientIndex = 1;
  
  public class SupClientHandler implements Runnable {
    BufferedReader reader;
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
          tellSomeone(message, Thread.currentThread().getName());
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
        PrintWriter writer = new PrintWriter(supClient.getOutputStream());
        
        String clientName = "Client" + this.clientIndex;
        if(userList.containsKey(clientName)) {
          throw new Exception("User name is already used, please use another one.");
        }
        userList.put(clientName, writer);


        Thread t = new Thread(new SupClientHandler(supClient));
        t.start();
        t.setName(clientName);
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
  public void tellSomeone(String message, String name) {
    while (userList.containsKey(name)) {
      try {
            PrintWriter writer = userList.get(name);
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