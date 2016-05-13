import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Server for ConnectFive game
 *
 * @author Patrick Keefe, Jr.
 * @version 5/9/2016
 */
public class ConnectFiveServer
{
   Vector<ObjectOutputStream> oosVector = new Vector<ObjectOutputStream>(); //Vector that holds all ObjectOutputStreams to send String/char[][] objects to all clients
   private final char[] COLORS = {'r','b','s'};
   int playerCount = 0;
   private char[][] currentGameboard = new char[9][9];
   private char currentPlayer = 'r';
   
   /**
    * Constructor for ConnectFiveServer. Starts threads when clients connect
    */
   public ConnectFiveServer()
   {
      ServerSocket ss = null;
      try
      {
         ss = new ServerSocket(36852);
         Socket clientSocket = null;
         while(true)
         {
            char playerColor = 's';
            playerCount++; //Needs synchronization
            switch(playerCount)
            {
               case 1: playerColor = COLORS[0];
                       break;
               case 2: playerColor = COLORS[1];
                       break;
               default: playerColor = COLORS[2];
                        break;
            }
            clientSocket = ss.accept();
            Thread c5st = new Thread(new C5ServerThread(clientSocket, playerColor));
            c5st.start();
            System.out.println("CLIENT CONNECTED");
         }
      }
      catch(BindException be)
      {
         System.err.println("ERROR: Port in use");
      }
      catch(IOException ioe)
      {
         System.err.println(ioe.toString());
      }
   }
   
   /**
    * Thread for ConnectFive server
    */
   class C5ServerThread implements Runnable
   {
      private Socket clientSocket;
      private char playerColor;
      
      /**
       * Constructor for C5ServerThread
       * @param _clientSocket Socket of the client
       */
      public C5ServerThread(Socket _clientSocket, char _playerColor)
      {
         clientSocket = _clientSocket;
         playerColor = _playerColor;
      }
      
      /**
       * Run method for server. Receives an object (String or char[][]), and sends it to all clients
       */
      public void run()
      {
         ObjectInputStream ois = null;
         ObjectOutputStream oos = null;
         try
         {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            C5Pair initialPair = new C5Pair(currentGameboard, currentPlayer);
            oos.writeObject(playerColor);
            oos.flush();
            oos.writeObject(initialPair);
            oos.flush();
            oosVector.add(oos);
            ois = new ObjectInputStream(clientSocket.getInputStream());
            
            while(true)
            {
               Object inpObj = ois.readObject();
               if(inpObj instanceof C5Pair)
               {
                  C5Pair temp = (C5Pair)inpObj;
                  currentGameboard = temp.getGameBoard();
                  if(temp.getNextPlayer() != 'w' || temp.getNextPlayer() != 't')
                  {
                     currentPlayer = temp.getNextPlayer();
                  }
               }
               for(ObjectOutputStream element : oosVector)
               {
                  element.writeObject(inpObj);
                  element.flush();
               }  
            }
         }
         catch(ClassNotFoundException cnfe)
         {
            System.err.println(cnfe.toString());
         }
         catch(EOFException eof)
         {
            playerCount--;
         }
         catch(IOException ioe)
         {
            System.err.println(ioe.toString());
         }
      }
   }
   
   public static void main(String[] args)
   {
      new ConnectFiveServer();
   }
}