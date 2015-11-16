	/**
	* This is a tracker element that keeps track of unacknowledged messages
	* It is kept in a list on the server side
	*/
	package org.server;

	public class MessageTracker
	{
		public String recipient = "";
		public String sender = "";
		public boolean stale = false;
	
		public MessageTracker(String from, String to)
		{
			recipient = to;
			sender = from;
		}

	}

