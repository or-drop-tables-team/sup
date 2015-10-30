package org.server;

/**
 * ServerApp is the main class for the server application. It serves to instantiate and start
 * the server's business logic.
 */
public class ServerApp 
{
    private final static int PORT_NUM = 3000;
    
    public static void main( String[] args )
    {
        SupServer server = new SupServer(PORT_NUM);
        server.start();
    }
}
