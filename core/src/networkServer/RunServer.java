/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkServer;
import com.gdx.bomberman.Constants;
import java.util.Scanner;

/**
 *
 * @author qubasa
 */
public class RunServer 
{
    public static void main(String [] args)
    {
        try
        {
            
            //Initialise server object
            Server server = new Server(Constants.SERVERPORT, Constants.MAXPLAYERS);   

            server.OpenLobby();
            
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println("Commands: /startGame, /stopServer, /closeLobby");
            
            while(true)
            {
                String n = reader.nextLine();
                if(n.equalsIgnoreCase("/startGame"))
                {
                    System.out.println("----------GAME STARTED---------");
                    server.startGame();
                    
                }else if(n.equalsIgnoreCase("/stopServer"))
                {
                    System.out.println("----------SERVER STOPPED---------");
                    server.stopServer();
                    break;
                    
                }else if(n.equalsIgnoreCase("/closeLobby"))
                {
                    System.out.println("----------SERVER STOPPED---------");
                }
            }
               
        }catch(Exception e)
        {
            System.err.println("ERROR: Unexpected error has been thrown in main" + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
