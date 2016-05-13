 /**
  * Class ConnectFiveBackEnd will be used in order to check for a win condition after a move is made as well as store the information in the
  * 2D array as to what moves have been made
  *
  * @author Ryan Brown, Patrick Keefe Jr.
  * @version 5/9/2016
  */
  
import java.io.*;
import java.net.*;

public class ConnectFiveBackEnd
{
   char[][] connectFiveBoard = null; //Character Array to hold values of where pieces are
   char playerColor = 's'; //r = red, b = blue, s = spectator
   
   char currentPlayer = 'r';
   ObjectOutputStream oos = null;
   ObjectInputStream ois = null;
   
   /**
    * Constructor for Back End
    * Called when creating first game, and when a game resets
    */
   public ConnectFiveBackEnd()
   {
      try
      {
         InetAddress server = InetAddress.getByName("localhost");
         Socket clientSocket = new Socket(server, 36852);
         oos = new ObjectOutputStream(clientSocket.getOutputStream());
         oos.flush();
         ois = new ObjectInputStream(clientSocket.getInputStream());
         playerColor = (char)ois.readObject();
      }
      catch(ClassNotFoundException cnfe)
      {
         System.err.println(cnfe.toString());
      }
      catch(UnknownHostException uhe)
      {
         System.err.println(uhe.toString());
      }
      catch(IOException ioe)
      {
         System.err.println(ioe.toString());
      }
      Thread c5lt = new Thread(new C5ListenThread());
      c5lt.start();
   }
   
   /**
    * Method called when a token is being placed
    * Finds lowest open spot in a column, places token, and checks to see if
    * there are five tokens in a row in any direction.
    *
    * @param column Column chosen to have a token placed in
    * @param color Color of the player (r, b)
    * @return Returns "0" if the placement is invalid; "row,column,1" if placement is valid; "row,column,2" if the player won
    */
   public String placeToken(int column, char color)
   {
      int row = 8;
      boolean empty = false;
      while(!empty)
      {
         if(row == -1)
         {
            return "0"; //Placement Invalid
         }
         if(connectFiveBoard[row][column] == '\u0000')
         {
            empty = true;
            connectFiveBoard[row][column] = color;
            try
            {
               C5Pair toServer = new C5Pair(connectFiveBoard, playerColor);
               oos.writeObject(toServer);
               oos.flush();
            }
            catch(IOException ioe)
            {
               return("WRITE FAIL");
            }
         }
         else
         {
            row--;
         }
      }
      return checkFiveInARow(row, column, color); //1 = Valid placement, no win
   }                                              //2 = Valid placement, win
   
   /**
    * Method to check for win scenario (five in a row)
    * 
    * @param row Row of the newly placed piece
    * @param column Column of the newly placed piece
    * @param color Color of the newly placed piece
    * @return Returns "row,column,1" if no win; "row,column,2" if the player won
    */
   public String checkFiveInARow(int row, int column, char color)
   {
      //Sets each count to one, as the new piece counts for each direction
      int countVertical = 1;
      int countHorizontal = 1;
      int countBackSlash = 1;
      int countForwardSlash = 1;
      
      boolean otherColor = false;
      int rowVertical = row;
      while(!otherColor) //Checks down from new piece
      {
         rowVertical++;
         if(rowVertical == 9) otherColor = true;
         else if(connectFiveBoard[rowVertical][column] == color)
         {
            countVertical++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int columnHorizontalRight = column;
      while(!otherColor) //Checks to the right of new piece
      {
         columnHorizontalRight++;
         if(columnHorizontalRight == 9) otherColor = true;
         else if(connectFiveBoard[row][columnHorizontalRight] == color)
         {
            countHorizontal++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int columnHorizontalLeft = column;
      while(!otherColor) //Checks to the left of new piece
      {
         columnHorizontalLeft--;
         if(columnHorizontalLeft == -1) otherColor = true;
         else if(connectFiveBoard[row][columnHorizontalLeft] == color)
         {
            countHorizontal++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int rowBackSlashRight = row;
      int columnBackSlashRight = column;
      while(!otherColor) //Checks backslash right
      {
         rowBackSlashRight++;
         columnBackSlashRight++;
         if(rowBackSlashRight == 9 || columnBackSlashRight == 9) otherColor = true;
         else if(connectFiveBoard[rowBackSlashRight][columnBackSlashRight] == color)
         {
            countBackSlash++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int rowBackSlashLeft = row;
      int columnBackSlashLeft = column;
      while(!otherColor) //Checks backslash left
      {
         rowBackSlashLeft--;
         columnBackSlashLeft--;
         if(rowBackSlashLeft == -1 || columnBackSlashLeft == -1) otherColor = true;
         else if(connectFiveBoard[rowBackSlashLeft][columnBackSlashLeft] == color)
         {
            countBackSlash++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int rowForwardSlashRight = row;
      int columnForwardSlashRight = column;
      while(!otherColor) //Checks forwardslash right
      {
         rowForwardSlashRight++;
         columnForwardSlashRight--;
         if(rowForwardSlashRight == 9 || columnForwardSlashRight == -1) otherColor = true;
         else if(connectFiveBoard[rowForwardSlashRight][columnForwardSlashRight] == color)
         {
            countForwardSlash++;
         }
         else{otherColor = true;}
      }
      
      otherColor = false;
      int rowForwardSlashLeft = row;
      int columnForwardSlashLeft = column;
      while(!otherColor) //Checks forwardslash left
      {
         rowForwardSlashLeft--;
         columnForwardSlashLeft++;
         if(rowForwardSlashLeft == -1 || columnForwardSlashLeft == 9) otherColor = true;
         else if(connectFiveBoard[rowForwardSlashLeft][columnForwardSlashLeft] == color)
         {
            countForwardSlash++;
         }
         else{otherColor = true;}
      }
      
      if( countVertical >= 5 || countHorizontal >= 5 || countBackSlash >= 5 || countForwardSlash >= 5 )
      {
         return(row + "," + column + ",2");
      }
      
      return(row + "," + column + ",1");
   }
   
   public void reset(char winTie)
   {
      try
      {
         C5Pair temp = new C5Pair(new char[9][9], winTie);
         oos.writeObject(temp);
         oos.flush();
      }
      catch(IOException ioe)
      {
         System.err.println(ioe.toString());
      }
   }
   
   class C5ListenThread implements Runnable
   {
      Socket clientSocket = null;
      public C5ListenThread()
      {
         //clientSocket = _clientSocket;
      }
      
      public void run()
      {
         while(true)
         {
            try
            {
               Object inpObj = ois.readObject();
               if(inpObj instanceof C5Pair)
               {
                  C5Pair temp = (C5Pair)inpObj;
                  connectFiveBoard = temp.getGameBoard();
                  if(temp.getNextPlayer() != 'w' || temp.getNextPlayer() != 't')
                  {
                     currentPlayer = temp.getNextPlayer();
                  }
                  else if(temp.getNextPlayer() == 'w')
                  {
                     ConnectFive.win(currentPlayer);
                  }
                  else
                  {
                     ConnectFive.tie();
                  }
               }
               else if(inpObj instanceof String)
               {
                  //Chat client stuff
               }
            }
            catch(ClassNotFoundException cnfe)
            {
               System.err.println(cnfe.toString());
            }
            catch(IOException ioe)
            {
               System.err.println(ioe.toString());
            }
         }
      }
   }
}