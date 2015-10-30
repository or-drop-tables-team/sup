package org.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Universally accessible contacts list (safe to edit from any thread of execution)
 *
 */
public class Contacts {
    
    private static Contacts instance = null;
    
    /**
     * Map user names to their client sockets. 
     */
    private ConcurrentHashMap<String, PrintWriter> userList = new ConcurrentHashMap<String, PrintWriter>();
    
    /**
     * Constructor is defined only to prevent instantiation of this class, since it should be
     * a singleton. Constructor is protected.
     */
    protected Contacts() {
        // Don't allow instantiation. Do nothing here.
    }
       
    /**
     * Use this to get an instance of this contacts object.
     * 
     * @return Singleton instance of this class.
     */
    public static Contacts getInstance() {
        if(instance == null) { 
            instance = new Contacts();
        }
        return instance;
    }
    
    /**
     * Determine whether a certain contact name is currently logged on to the server.
     * 
     * @param name - of the user being queried
     * 
     * @return true if currently logged on, else false
     */
    public Boolean hasContact(String name) {
        return userList.containsKey(name);
    }
    
    /**
     * Add a user to the online contact book. The username must be unique or it will fail!
     * 
     * @param name - name of the user to add.
     * @param pw - outgoing PrintWriter used to contact this username
     * 
     * @return true if successfully added name, false otherwise
     */
    public Boolean addContact(String name, PrintWriter pw) {
        if(hasContact(name)) {
            // cannot add duplicate
            return false;
        }
        userList.put(name, pw);
        return true;
    }
    
    /**
     * Retrieve the output stream for a user so they can be delivered a message.
     * May throw if the contact is not currently online.
     * 
     * @param name - username to get the stream for
     * 
     * @return PrintWriter that can be used to send a message to this user
     * 
     * @throws Exception
     */
    public PrintWriter getContact(String name) throws Exception {
        if(! hasContact(name)) {
            // cannot get that contact
            throw new Exception();
        }
        return userList.get(name);
    }
    
    /**
     * Remove a contact from the map. Contacts tracked are only those online, so a contact
     * should be removed when they either log off or the connection is unexpectedly terminated.
     * 
     * @param name - username of the contact who is no longer online
     */
    public void removeContact(String name) {
        userList.remove(name);
    }
    
    /**
     * Get a list of all active outgoing sockets. Useful to send a universal notification.
     * 
     * @return List of active outgoing socket streams
     * 
     * @throws Exception
     */
    public List<PrintWriter> getWriterList() throws Exception{
    	List<PrintWriter> users = new ArrayList<PrintWriter>();
    	for (Entry<String, PrintWriter> entry : userList.entrySet()) {
			users.add(entry.getValue());
		}
    	return users;
    }
    
    /**
     * Get a list of all the logged on user names. Useful to enumerate all active users at a given
     * time.
     * 
     * @return List of all usernames currently active
     * 
     * @throws Exception
     */
    public List<String> getUserList() throws Exception{
    	List<String> users = new ArrayList<String>();
    	for (Entry<String, PrintWriter> entry : userList.entrySet()) {
			users.add(entry.getKey());
		}
    	return users;
    }
}
