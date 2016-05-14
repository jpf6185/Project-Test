import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.net.*;


 /**
  * Class ConnectFive is the main interface for the connect five gui.
  *
  * @author Ryan Brown, Patrick Keefe Jr.
  * @version 4/29/2016
  */
public class ConnectFive extends JFrame implements ActionListener
{
   JButton jbOne = null;
   JButton jbTwo = null;
   JButton jbThree = null;
   JButton jbFour = null;
   JButton jbFive = null;
   JButton jbSix = null;
   JButton jbSeven = null;
   JButton jbEight = null;
   JButton jbNine = null;
   JPanel jpCenter = null;
   
   JFrame connection = null;
   String ipAddress = "";
   JLabel connectServer = null;
   JTextField serverIP = null;
   JButton connectToServer = null;
   boolean isConnected = false;
   
   private static int redWins = 0;
   private static int blueWins = 0;
   private int placedPieces = 0;
   
   String fromBackEnd = null;
   int beRow = 0; //be = BackEnd
   int beColumn = 0;
   int validOrWin = 0;
   
   char currentPlayer = 'r';
   
   //Ints specify the total rows, columns, and slots
   private static final int SLOTS = 81;
   private static final int ROWS = 9;
   private static final int COL = 9;
   
   ArrayList<Slot> slots = new ArrayList<Slot>();
   
   ConnectFiveBackEnd backEnd = new ConnectFiveBackEnd();
   
   /**
    * Constructor for Connect Five game. Sets up GUI, and sets all spots to default color
    */
   public ConnectFive()
   { 
      connectServer = new JLabel("Connect to server: ");
      serverIP = new JTextField(25);
      connectToServer = new JButton("Connect");
      
      connection = new JFrame();
      
      connection.setLayout( new FlowLayout(FlowLayout.CENTER));
      connection.add(connectServer);
      connection.add(serverIP);
      connection.add(connectToServer);
      connectToServer.addActionListener(this);
            
      connection.setVisible(true);
      connection.setLocationRelativeTo(null);
      connection.setSize(500, 100);
   
      setLayout(new BorderLayout());
      
      jpCenter = new JPanel();
      jpCenter.setLayout( new GridLayout(10, 9, 10, 10));
      
      add(jpCenter, BorderLayout.CENTER);    
           
      jbOne = new JButton("One");
      jbOne.addActionListener(this);
      
      jbTwo = new JButton("Two");
      jbTwo.addActionListener(this);
      
      jbThree = new JButton("Three");
      jbThree.addActionListener(this);
      
      jbFour = new JButton("Four");
      jbFour.addActionListener(this);
      
      jbFive = new JButton("Five");
      jbFive.addActionListener(this);
      
      jbSix = new JButton("Six");   
      jbSix.addActionListener(this);   
      
      jbSeven = new JButton("Seven");   
      jbSeven.addActionListener(this);   
      
      jbEight = new JButton("Eight");
      jbEight.addActionListener(this);      
      
      jbNine = new JButton("Nine");
      jbNine.addActionListener(this);

      jpCenter.add(jbOne);
      jpCenter.add(jbTwo);
      jpCenter.add(jbThree);
      jpCenter.add(jbFour);
      jpCenter.add(jbFive);
      jpCenter.add(jbSix);
      jpCenter.add(jbSeven);
      jpCenter.add(jbEight);
      jpCenter.add(jbNine);

      for(int i = 0; i < SLOTS; i++)
      {
         Slot spot = new Slot(i);
         jpCenter.add(spot);
         slots.add(spot);
      }
   }
   
   /**
    * Main method for ConnectFive game
    */
   public static void main(String[] args)
   {       
        ConnectFive jfMain = new ConnectFive();
        jfMain.getContentPane().setBackground(new Color(0xFEFFE2));
        jfMain.setTitle("Connect Five");
        jfMain.setSize(700, 700);
        jfMain.setLocationRelativeTo( null );		
        jfMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfMain.setVisible(false);
   }
   
   /**
    * Method to parse return from ConnectFiveBackEnd.placeToken()
    */
   public void intifyFromBackEnd()
   {
      String[] fbeArray = fromBackEnd.split(",");
      beRow = Integer.parseInt(fbeArray[0]);
      beColumn = Integer.parseInt(fbeArray[1]);
      validOrWin = Integer.parseInt(fbeArray[2]);
      fromBackEnd = null;
   }
   
   /**
    * ActionEvent is called for all JButtons and will determine if the piece can be placed in that
    * slot and check for five of the same color in a row
    */
   public void actionPerformed(ActionEvent e)
   {
                String cmd = e.getActionCommand();
      
      if(cmd.equalsIgnoreCase("One"))
      {
         buttonOne();
      }
      else if(cmd.equalsIgnoreCase("Two"))
      {
         buttonTwo();
      }
      else if(cmd.equalsIgnoreCase("Three"))
      {
         buttonThree();
      }
      else if(cmd.equalsIgnoreCase("Four"))
      {
         buttonFour();
      }
      else if(cmd.equalsIgnoreCase("Five"))
      {
         buttonFive();
      }
      else if(cmd.equalsIgnoreCase("Six"))
      {
         buttonSix();
      }
      else if(cmd.equalsIgnoreCase("Seven"))
      {
         buttonSeven();
      }
      else if(cmd.equalsIgnoreCase("Eight"))
      {
         buttonEight();
      }
      else if(cmd.equalsIgnoreCase("Nine"))
      {
         buttonNine();       
      }
      else if(cmd.equalsIgnoreCase("Connect"))
      {
         buttonConnect();
      }

      placedPieces++;
      if(validOrWin == 2) //Checks if there is a winner
      {
         resetBoard('w');
      }
      else if(placedPieces == SLOTS)
      {
         resetBoard('t');
      }
   }
   
   /**
    * Method to win game. Called when a player has won. Clears board, and shows win statistics
    */
   public static void win(char winPlayer)
   {
      if(winPlayer == 'b')
      {
         redWins++;
         JOptionPane.showMessageDialog(null, "Red Player has won! \nBlue - " + blueWins + "\nRed - " + redWins);
      }
      else
      {
         blueWins++;
         JOptionPane.showMessageDialog(null, "Blue Player has won! \nBlue - " + blueWins + "\nRed - " + redWins);
      }
   }
   
   /**
    * Method called when neither player wins. Does not add to 
    * win counts, and it resets the board
    */
   public static void tie()
   {
      JOptionPane.showMessageDialog(null, "Neither player wins. Tie! \nBlue - " + blueWins + "\nRed - " + redWins);
   }
   
   /**
    * Method to reset board. Resets GUI to default white tokens, and creates new backEnd
    */
   public void resetBoard(char winTie)
   {
      guiReset();
      backEnd.reset(winTie);
      placedPieces = 0;
   }
   
   public void guiReset()
   {
      for(int q = 0; q < SLOTS; q++)
      {
         slots.get(q).resetBoard();                
      }
   }
   /**
    * Methods for buttons pressed.
    *
    * @version 5/9 Ryan Brown
    */
    public void buttonOne()
    {
    fromBackEnd = backEnd.placeToken(0, currentPlayer);
    if(fromBackEnd.equals("0")) //Invalid placement of token
    {
       JOptionPane.showMessageDialog(jbOne, "Column one is full. Try Again");
       return;
    }
    else if(fromBackEnd.equals("WRITE FAIL"))
    {
      JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
    }
    else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);} //Valid placement of token
    if(currentPlayer == 'r')
    {
       currentPlayer = 'b';
    }
    else currentPlayer = 'r';
    }
    
    public void buttonTwo()
    {
        fromBackEnd = backEnd.placeToken(1, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbTwo, "Column two is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonThree()
    {
        fromBackEnd = backEnd.placeToken(2, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbThree, "Column three is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
   
       public void buttonFour()
    {
        fromBackEnd = backEnd.placeToken(3, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbFour, "Column four is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonFive()
    {
        fromBackEnd = backEnd.placeToken(4, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbFive, "Column Five is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonSix()
    {
        fromBackEnd = backEnd.placeToken(5, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbSix, "Column six is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonSeven()
    {
        fromBackEnd = backEnd.placeToken(6, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbSeven, "Column seven is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonEight()
    {
        fromBackEnd = backEnd.placeToken(7, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbEight, "Column eight is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r';    
    }
    
    public void buttonNine()
    {
        fromBackEnd = backEnd.placeToken(8, currentPlayer);
         if(fromBackEnd.equals("0"))
         {
            JOptionPane.showMessageDialog(jbNine, "Column nine is full. Try Again");
            return;
         }
         else if(fromBackEnd.equals("WRITE FAIL"))
         {
            JOptionPane.showMessageDialog(jbOne, "Network Error. Try Again");
         }
         else {intifyFromBackEnd(); slots.get(beRow * COL + beColumn).setColor(currentPlayer);}
         if(currentPlayer == 'r')
         {
            currentPlayer = 'b';
         }
         else currentPlayer = 'r'; 
    }
    
    public void buttonConnect()
    {
      //serverIP is the JTextField
      ipAddress = serverIP.getText();
      
//       if()
//       {
//          isConnected = true;
//       }
      
      if(isConnected == true)
      {
         setVisible(true);
         connection.setVisible(false);
      }
      
    }
     
    /**
     * Class ConnectFiveBackEnd will be used in order to check for a win condition after a move is made as well as store the information in the
     * 2D array as to what moves have been made
     *
     * @author Ryan Brown, Patrick Keefe Jr.
     * @version 5/9/2016
     */
   class ConnectFiveBackEnd
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
            InetAddress server = InetAddress.getByName(ipAddress);
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
}