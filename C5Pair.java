/**
 * Class to pass 2D character array and player character between client and server
 *    for ConnectFive game
 * @author Patrick Keefe, Jr.
 * @version 5/9/2016
 */
import java.io.*;
 
public class C5Pair implements Serializable
{
   private char[][] gameBoard;
   private char nextPlayer;
   
   /**
    * Constructor for C5Pair
    * @param _gameBoard Gameboard after player makes move
    * @param _nextPlayer Char indicating who the next player is (b = blue, r = red)
    */
   public C5Pair(char[][] _gameBoard, char _nextPlayer)
   {
      gameBoard = _gameBoard;
      nextPlayer = _nextPlayer;
   }
   
   public char[][] getGameBoard()
   {
      return gameBoard;
   }
   
   public char getNextPlayer()
   {
      return nextPlayer;
   }
}