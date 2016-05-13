import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

 /**
  * Class Slot will take the pieces that are placed in a column and change the slot to the current players color.
  *
  * @author Ryan Brown, Patrick Keefe Jr.
  * @version ISTE-123.03 Mini Project
  *
  */
public class Slot extends JPanel
{
   BufferedImage myPicture = null;
   JLabel picLabel = null;
   int row = 0;
   int column = 0;
   int id = 0;

   /**
    * Constructor for Slot. Sets default color of token to a white circle
    */
   public Slot(int _id)
   {
      id = _id;
      try{
      myPicture = ImageIO.read(new File("Pictures/WhiteCircle.png"));
      picLabel = new JLabel(new ImageIcon(myPicture));
      add(picLabel);
      }
      catch(Exception e)
      {
         System.out.println("Image may be located in wrong directory - should be in Pictures");
      }
   }
   public int getRow()
   {
      return row;
   }
   public int getColumn()
   {
      return column;
   }
   
   /**
    * Method to set color of token if a play is valid
    *
    * @param color Color of the piece to be placed
    */
   public void setColor(char color)
   {
      switch(color){
         case 'r': try{
                      remove(picLabel);
                      myPicture = ImageIO.read(new File("Pictures/RedCircle.png"));
                      picLabel = new JLabel(new ImageIcon(myPicture));
                      add(picLabel);
                      revalidate(); 
                      repaint();
                      break;
                   }
                   catch (IOException e)
                   {
                     System.out.println(e.getMessage());
                   }
         case 'b': try{
                      remove(picLabel);
                      myPicture = ImageIO.read(new File("Pictures/BlueCircle.png"));
                      picLabel = new JLabel(new ImageIcon(myPicture));
                      add(picLabel); 
                      revalidate(); 
                      repaint();
                      break;
                   }
                   catch (IOException e)
                   {
                     System.out.println(e.getMessage());
                   }
         default: System.out.println("ERROR");
      }
   }

   /**
    * Method to reset board to all white tokens
    */
   public void resetBoard()
   {
      try{
          remove(picLabel);
          myPicture = ImageIO.read(new File("Pictures/WhiteCircle.png"));
          picLabel = new JLabel(new ImageIcon(myPicture));
          add(picLabel);
          revalidate();
          repaint();
       }
       catch (IOException e)
       {
         System.out.println(e.getMessage());
       }
   }
}