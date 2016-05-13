import java.net.*;	
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;		
import java.util.*;

/**  Group Jacob Feinter, Ryan Sweeney, Patrick Keefe, Jr., Ryan Brow
 *A class that creates a server to maintain a group chat
 *
 *@author Ryan Sweeney
 *@version 4/17/16
 */
public class ChatServerGame extends JFrame
{
   private Vector<Pair> write = new Vector<Pair>();
   
 /**
  *Main method for server
  */
   public static void main(String [] args)
   {
      new ChatServerGame();
   }
  
  /**
   *Constructor for server and its GUI
   */
   public ChatServerGame()
   {  
      try 
      {																															
         ServerSocket ss = new ServerSocket(34567);
         Socket cs = null;
         	
         while( true ){
            System.out.println("Waiting for a client connection\n");
            cs = ss.accept();		// wait here for connections from a client
            System.out.println("Have a client "+ cs +"\n" );
               
            ServerThread ts = new ServerThread(cs);
            Thread th = new Thread(ts);
            th.start();	
         }	
         	
      }																										
      catch( IOException e ) { 																			
         System.out.println("Error with read and write"); 										
         e.printStackTrace();	
      }
      catch(Exception e)
      {
         System.out.println(e.getMessage());
      }
             
   }
   
   /**
    *A WindowAdapter class that handles what happens when the server is closed
    *
    *@author Ryan Sweeney
    *@version 3/31/16
    */
   class CloseListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent e)
      {
         for(Pair client: write)
         {
            client.getPrintWriter().println("*EXIT*");
            client.getPrintWriter().flush();
            client.getPrintWriter().close();
         }
      
         System.exit(0);
      }
   }
   
   /**
    *An Inner class that creates threads for clients to the server
    *
    *@author Ryan Sweeney
    *@version 3/30/16
    */
   class ServerThread implements Runnable
   {
      private Socket sock;
      private BufferedReader in = null;
      private PrintWriter out = null;
      private Pair user = null;
      
      private boolean connected = true;
   
      /**
       *Constructor for Thread class
       */
      public ServerThread(Socket _sock)
      {
         try
         {
            sock = _sock;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream());
            out.println("Welcome to the chat!!!");
            out.flush();
            user = new Pair(out);
            write.add(user);
         }
         catch(IOException ioe)
         {
            write.remove(user); 
         }
      }
    
     /**
      *Run method for the thread(s)
      */
      public void run()
      {
         try
         {
            String name = in.readLine();
            user.setUsername(name);
            
            while(connected)
            {
               String message = in.readLine();
            
               if(message.equals("*EXIT*"))//*EXIT* String
               {
                  out.close();
                  in.close();
                  write.remove(out);
                  for(Pair client: write)
                  {
                     client.getPrintWriter().println("A client has exited chat...");
                  }
                  connected = false;
               }
               
               else//Anything else sent, sends message to every other client in chat
               {
                  for(Pair client: write)
                  {
                     client.getPrintWriter().println(user.getUsername()+": "+message);
                     client.getPrintWriter().flush();
                  }
               }
            }
         }
         catch(Exception e)
         {
            System.out.println("Error: "+ e.getMessage()+"\n");
         }
      }
   }
   
   /**
    *A class that creates a pair of PrintWriters and Strings for server use
    *
    *@author Ryan Sweeney
    *@verion 4/17/16
    */
   class Pair
   {
      private PrintWriter pw = null;
      private String name = "";
   
   /**
    *Constuctor for class
    *@param firstElement The PrintWriter for sending messages to user
    */
      public Pair(PrintWriter firstElement)
      {
         pw = firstElement;
      }
   
   /**
    *Method the returns PrintWriter 
    *@return The PrintWriter for a specific user
    */
      public PrintWriter getPrintWriter()
      {
         return pw;
      }
    
    /**
     *Method that sets the Username for a client in chat
     *@param _name The Username for a client
     */
      public void setUsername(String _name)
      {
         name = _name;
      }
    
    /**
     *Method that returns the name for a client user
     *@return The username of a client
     */
      public String getUsername()
      {
         return name;
      }
   
   }
}