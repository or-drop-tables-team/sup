package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.common.TokenPair;
import org.common.Utils;

import java.sql.*;

/**
 * This is the main business logic for the server. This class starts and continuously listens
 * for new connections from clients. Each connection is tracked and chat messages are forwarded as
 * appropriate during normal execution.
 *
 */
public class SupServer {

    public static final String DB_FILE = "sup_auth.db";
    private int port;

    /**
     * Constructor for this class. Simply provide the port the listening socket shall bind to.
     *
     * @param port - port to bind
     */
    public SupServer(int port) {
        this.port = port;
    }

    /**
     * When the server gets a new connection request from a client a new SupClientHandler is
     * created and operates in its own thread, handling communications from the connected client.
     * Commands and chat messages can be received and handled in this class.
     */
    public class SupClientHandler implements Runnable {
        BufferedReader reader;
        String clientname = "";
        Socket sock;

        /**
         * Constructor
         *
         * @param clientSocket - the raw socket created for this client connection
         */
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

        /**
         * Run is the logic that will be (continually) executed during this thread's lifetime.
         * This thread will be running for the duration of the client's connection.
         */
        public void run() {
            // Run until connection is broken or user terminates
            String message = "";
            try {
                // if connection is broken, receiveMessage will return empty string.
                while((message = Utils.receiveMessage(this.reader)) != "") {

                    // At this point, determine what the message is so we know how to act.
                    
                    TokenPair cmdPair = Utils.tokenize(message);
                    // If the command is to log in, handle that.
                    if(cmdPair.first.equals("login")) {
                        // Log the user in.
                            if(cmdPair.rest.isEmpty()) {
                                // blank username, not all right.
                                Utils.sendMessage(new PrintWriter(sock.getOutputStream()), Utils.FAIL_LOGIN_USERNAME_INVALID);
                            }
                            else if (Contacts.getInstance().hasContact(cmdPair.rest)) {
                                // return error, user name taken
                                System.out.println("Contact name already logged in: " + cmdPair.rest );
                                // This isn't exactly an accurate status message, but I do think it should still fail, since
                                // we don't deliver to multiple sockets (which we would need to do if we allowed them to
                                // sign on from multiple locations).
                                Utils.sendMessage(new PrintWriter(sock.getOutputStream()), Utils.FAIL_LOGIN_USERNAME_TAKEN);
                            }
                            else {
                                // They aren't online, try to log them in.
                                TokenPair namePassPair = Utils.tokenize(cmdPair.rest);
                                if(authenticateUser(namePassPair.first, namePassPair.rest, DB_FILE)) {
                                    // Successful login!
                                    System.out.println("Successful login for user:" + namePassPair.first);
                                    clientname = namePassPair.first;
                                } else {
                                    // Intentionally generic!!
                                    System.out.println("Login denied for user:" + namePassPair.first + " pass:" + namePassPair.rest);
                                    Utils.sendMessage(new PrintWriter(sock.getOutputStream()), Utils.FAIL_LOGIN_PERMISSION_DENIED);
                                }
                                // Add them to the list of online contacts.
                                Contacts.getInstance().addContact( clientname, new PrintWriter(sock.getOutputStream()) );
                                try {
                                    Utils.sendMessage(Contacts.getInstance().getContact(clientname), Utils.SUCCESS_STS);
                                } catch (Exception e) {
                                    System.out.println("Failed to send confirmation message to new client");
                                    e.printStackTrace();
                                }
                            }
                    // Check for a registration message.
                    } else if(cmdPair.first.equals("register")) {
                        TokenPair userPassPair = Utils.tokenize(cmdPair.rest);
                        String status = registerUser(userPassPair.first, userPassPair.rest, DB_FILE);
                        // We don't really have to be concerned with what happened here, just trust registerUser 
                        // to have done the right thing and made the right status.
                        Utils.sendMessage(new PrintWriter(sock.getOutputStream()), status);
                    } else {
                        // It's a chat message, make sure the username is set (only done on successful
                        // login).
                        if(this.clientname.isEmpty()) {
                            // Not sure what we should do, but don't process the message. Should we return error?
                            continue;
                        }

                        // They've already successfully logged in.
                        // If it's not a login message, it's a chat message. parse it and forward.

                        // The first token is the chat command. There will come a time when
                        // we really need to process this to decide what to do, but for now
                        // we just assume it's a chat msg, beginning with "send".
                        TokenPair chatCmd = Utils.tokenize(message);

                        // The first token is the dest username, the rest is the message.
                        TokenPair destUser = Utils.tokenize(chatCmd.rest);

                        tellSomeone(destUser.rest, this.clientname, destUser.first);
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
                removeContact(clientname);
            }
        }
    }

    /**
     * Start the server listening at the expected port, ready to acccept new client connections!
     */
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

    /**
     * Send the message to the specific user. Find the socket by searching userlist.
     *
     * @param message
     * @param fromname
     * @param toname
     */
    // this needs to be thread safe.
    public void tellSomeone(String message, String fromname, String toname) {
        if (Contacts.getInstance().hasContact(toname)) {
            try {
                PrintWriter writer = Contacts.getInstance().getContact(toname);
                String msg = createFormattedChatMessage(message, fromname, toname);
                Utils.sendMessage(writer, msg);
                tellSomeoneStatus(Utils.SUCCESS_STS, fromname);
            } catch (Exception ex) {
                ex.printStackTrace();
                tellSomeoneStatus(Utils.FAIL_INTERNAL, fromname);
            }
        } else {
            tellSomeoneStatus(Utils.FAIL_USER_NOT_ONLINE, fromname);
        }
    }

    /**
     * Chat messages must be specifically formatted. This is a helper to format a message appropriately.
     *
     * @param message - message to send
     * @param fromname - sender username
     * @param toname - recipient username
     *
     * @return string of formatted message, ready for sending
     */
    private String createFormattedChatMessage(String message, String fromname, String toname ) {
        return "recv " + fromname + " " + message;
    }


    /**
     * return the status to the specific one
     *
     * @param status - status info
     * @param toname - the specific one to transfer status
     *
     * */
    public void tellSomeoneStatus(String status, String toname)
    {
        if (Contacts.getInstance().hasContact(toname)) {
            try {
                PrintWriter writer = Contacts.getInstance().getContact(toname);
                Utils.sendMessage(writer, status);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    /**
     * remove contact from active list and send the online list to everyone
     *
     * @param name - the contact to remove from the list
     * */
    public void removeContact(String name)
    {
        Contacts.getInstance().removeContact(name);
        System.out.println(name + " has been removed from online contacts");
    }

    /**
     * Helper to register a new user in the database. Pass the desired username and (plaintext)
     * password, and if available and appropriate, user will be added to database.
     *
     * @param name - desired username to registered
     * @param password - plaintext password
     * @param db - name of the database to use. Will always be DB_FILE in production,
     * but makes testing easier.
     *
     * @return Utils status string, depending specific error or success
     */
    public static String registerUser(String name, String password, String db) {
        Connection c = null;
        PreparedStatement createTableStmt = null;
        PreparedStatement checkUsernameStmt = null;
        PreparedStatement registerUserStmt = null;

        // This could be refactored into multiple functions.

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + db);
            // The table should exist, if this is not our first time, but SQL should handle that special case
            // for us by creating only if it does not already exist.
            createTableStmt = c.prepareStatement("CREATE TABLE IF NOT EXISTS USERS (NAME TEXT NOT NULL, PASSHASH TEXT NOT NULL);");
            createTableStmt.executeUpdate();
            createTableStmt.close();

            // Test to see whether this username is already registered.
            // Using prepared statements protects us from SQL injection.
            checkUsernameStmt = c.prepareStatement("SELECT * from USERS WHERE NAME=?;");
            checkUsernameStmt.setString(1, name);
            // Execute this query.
            ResultSet res = checkUsernameStmt.executeQuery();
            // If there are no rows found, then the name is taken and cannot be registered.
            if(res.next()) {
                res.close();
                checkUsernameStmt.close();
                c.close();
                return Utils.FAIL_LOGIN_USERNAME_TAKEN;
            }
            res.close();
            checkUsernameStmt.close();

            // Now add the actual users to the DB
            registerUserStmt = c.prepareStatement("INSERT INTO USERS (NAME, PASSHASH) VALUES (?, ?);");
            // Using prepared statements protects us from SQL injection.
            registerUserStmt.setString(1, name);
            registerUserStmt.setString(2, Utils.hashPass(password));
            // Execute this query.
            registerUserStmt.executeUpdate();

            // Close up
            registerUserStmt.close();
            c.close();
        }
        catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("Failed to add user \"" + name + "\" to authentication database!");
            return Utils.FAIL_INTERNAL;
        }

        return Utils.SUCCESS_STS;
    }

    /**
     * Helper to authenticate a user's login. Take username and password
     * and return true if authenticated, false if not. We can log speficic error reasons,
     * but only yes/no is returned to the user.
     *
     * @param name - username to check login for
     * @param password - the password
     * @param db - name of the database to use. Will always be DB_FILE in production,
     * but makes testing easier.
     *
     * @return true if authenticated, false if not
     */
    public static boolean authenticateUser(String name, String password, String db) {
        // Check to see if the user exists. We store a SHA256 hex hash of the password,
        // not the actual password.
        String hash = Utils.hashPass(password);
        // Now simply check for this user/password combo in the DB.
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + db);
            // Now make a select statement and see if we find anything.
            stmt = c.prepareStatement("SELECT * from USERS WHERE NAME=? and PASSHASH=?;");
            // Using prepared statements protects us from SQL injection.
            stmt.setString(1, name);
            stmt.setString(2, hash);
            // Execute this query.
            ResultSet res = stmt.executeQuery();
            // If there are no rows found, then the login is not valid.
            if(res.next()) {
                return true;
            }
            res.close();
            stmt.close();
            c.close();
        }
        catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("Unable to open database connection!");
        }
        return false;
    }
}
