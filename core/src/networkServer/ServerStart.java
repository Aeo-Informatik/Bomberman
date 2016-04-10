/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkServer;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author qubasa
 */
public class ServerStart implements Runnable
{

    public static void main(String [] args)
    {
        startServer();
    }
    
    @Override
    public void run() 
    {
        startServer();
        Server.DEBUG = false;
    }
    
    public static void startServer()
    {
        try
        {
            //Variables & objects
            final int port = 13199;
            final int minConnections = 1;
            final int maxConnections = 1;
            final int timeout = 20000;
            
            //Initialise server object
            Server server = new Server(port, minConnections, maxConnections);   
               
            //Set debug output to true
            Server.DEBUG = true;
                    
            
            //Accept all client connections and get them as socket object
            ArrayList<Socket> socketList = server.AcceptConnections(timeout);
            
            //Starts the game
            server.startGame(socketList);
               
        }catch(Exception e)
        {
            System.err.println("ERROR: Unexpected error has been thrown in main" + e);
            System.exit(0); 
        }
    }
    
}
