package org.server;

/**
 * Hello world!
 *
 */
public class App 
{
    private final static int PORT_NUM = 58009;
    
    public static void main( String[] args )
    {
        SupServer server = new SupServer(PORT_NUM);
        server.start();
    }
}
