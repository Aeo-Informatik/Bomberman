/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.gdx.bomberman.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author qubasa
 */
public class ClientReceiveThread implements Runnable {

    private Socket socket;
    
    //To be able to communicate between Threads
    protected static BlockingQueue queue = new ArrayBlockingQueue(4000);
    
    public ClientReceiveThread(Socket socket)
    {
        this.socket = socket;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void run() 
    {
        try
        {
            //Set timeout exception after 15 seconds or 0 = never
            socket.setSoTimeout(0);
            
            //Get data from server and parse it into an Object BufferedReader
            BufferedReader receive = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String dataReceived;

            //Read all lines received from server
            while((dataReceived = receive.readLine())!= null)
            {   
                if(Thread.currentThread().isInterrupted())
                {
                    break;
                }

                //Debug
                if(Constants.CLIENTDEBUG)
                {
                    System.out.println("CLIENT: Received from server: " + dataReceived);
                }

                //Adds the data to the BlockingQueue
                queue.add(dataReceived);
            }
            
        }catch(SocketException e)
        {
            System.out.println("CLIENT: Disconnecting from server...");
        
        //If server disconnects shutdown thread
        }catch(Exception e)
        {
            System.err.println("ERROR: Something went wrong in ClientReceiveDataThread " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
}
