package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
							// return error, user name taken
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
								System.out.println("message sent: "+ Utils.SUCCESS_STS);
							} catch (Exception e) {
								System.out.println("Failed to send confirmation message to new client");
								e.printStackTrace();
							}
						}
					} else {
						// they've already successfully logged in.
						// if it's not a login message is it a cmd message?
						String cmdOrChat = message.substring(0, message.indexOf(' '));
						System.out.println(cmdOrChat);
						if(cmdOrChat.equals("get")) {
							// cmd msg, execute command (only get contacts currently implemented).
							if (message.equals("get contacts")) {
								System.out.println("contacts request recieved from client.");
								String contactReply = "contList " + Contacts.getInstance().showContactNames();
								System.out.println(contactReply);
								Utils.sendMessage(new PrintWriter(sock.getOutputStream()), contactReply);
							}
						}
						else {                  		
							String toAndMsg = message.substring(message.indexOf(' ') + 1);
							String toname = toAndMsg.split(" ")[0];
							String msg = toAndMsg.substring(message.indexOf(' ') + 1);
							tellSomeone(msg, this.clientname, toname );
						}
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
	 *  send the message to the specific user. find the socket by searching userlist
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
	 *
	 * @param message
	 * @param fromname - sender username
	 * @param toname - recipient username
	 * @return string of formatted message, ready for sending
	 */
	private String createFormattedChatMessage(String message, String fromname, String toname ) {
		return "recv " + fromname + " " + message;
	}


	/**
	 * return the status to the specific one
	 *
	 * @param
	 * 	status - status info
	 *  toname - the specific one to transfer status
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
	 * @param
	 * 	name - the contact removed from the list
	 * */
	public void removeContact(String name)
	{
		Contacts.getInstance().removeContact(name);
		System.out.println(name + " has been removed from online contacts");
	}

}
