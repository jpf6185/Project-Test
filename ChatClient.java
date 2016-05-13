/**   Group Jacob Feinter, Ryan Sweeney, Patrick Keefe, Jr., Ryan Brow
** Chat server Client
**@Author Jacob Feiner
**
** ISTE 121
* V0.2
*/

// import statements
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ChatClient extends JFrame implements ActionListener{


  /* variables and GUI elements that need to be accesed by other elements and inner clases
  */ 
   private Thread th;
   private final static int PORT=34567;
   private Socket com=null; // socket for use in comunication
   private BufferedWriter bw=null;
   private boolean connected=false; // boolean to control wether or not the thread is running
   private JButton jbSetUserName=new JButton("Set User Name");   
   private JTextArea jtaChat=new JTextArea(25,25); // the chat and send boxes
   private JTextArea jtaSend;
   private JTextField jtfName;
   private JPanel jpName=new JPanel(new FlowLayout());
   private JTextField jtfServer;// the server to connect to
   private JButton jbSend=new JButton("send"); // the buttons for sending messages, connecting to the server, and exiting the client
   private JButton jbConnect=new JButton("Connect");
   
   private ChatReader listen; // a chatreader to read messages from the server
   
   /* Constructor for the client gui
   */
   public ChatClient(){
      setLayout(new BorderLayout(5,5));
      
      JPanel jpServer=new JPanel(new FlowLayout(FlowLayout.CENTER)); // jpannel to hold the textfield for server name, and connect button
      // initializes the jtextfield and button adds them to the jpannel then adds the jpanel to the jframe
      jtfServer=new JTextField(25); 
      jbConnect.addActionListener(this);
      JLabel jlServer=new JLabel("Server Address:");
      jpServer.add(jlServer);
      jpServer.add(jtfServer);
      jpServer.add(jbConnect);
      add(jpServer,BorderLayout.NORTH);
      
      
      jbSetUserName.addActionListener(this);
      jbSetUserName.setEnabled(false);
      
      setUserName();
      
      setVisible(true);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      addWindowListener(new ExitListener());
      
   }
   
   public static void main(String[]args){
      ChatClient myChat=new ChatClient();
   }
   private void setUserName(){
      
      JLabel jlName=new JLabel("UserName");
      jtfName=new JTextField(30);
      jpName.add(jlName);
      jpName.add(jtfName);
      add(jpName,BorderLayout.CENTER);
      
      add(jbSetUserName,BorderLayout.SOUTH);
      pack();
  }
      
   
   private void userNameChosen(){

      remove(jpName);
      remove(jbSetUserName);
      jtaChat.setLineWrap(true); // sets the textarea to wrap lines that are to long
      add(new JScrollPane(jtaChat));
      JPanel jpSend=new JPanel(new BorderLayout()); // jpanel to hold send field and send and exit buttons
      jtaSend=new JTextArea(10,25);
      jtaSend.setLineWrap(true);
      jpSend.add(new JScrollPane(jtaSend),BorderLayout.NORTH);
      
      JPanel jpButton=new JPanel(new FlowLayout(FlowLayout.RIGHT)); // flowlayout for buttons
      jbSend.addActionListener(this);
      jpButton.add(jbSend);
      jpSend.add(jpButton);
      add(jpSend,BorderLayout.SOUTH);
      setSize(550,800);
      revalidate();
      repaint();
  }
   
   public void close(boolean exit){
      try{
         bw.close();
         jbConnect.setText("Connect");
         jbSend.setEnabled(false);
         connected=false;  
         th.interrupt();
         jbSetUserName.setEnabled(false);
      }  
      catch(Exception e){}
      
      if(exit){
         System.exit(0);
      }
   }      
   
   public void actionPerformed(ActionEvent ae){ // action listener for buttons
      String action=ae.getActionCommand();
      // if the connect button is clicked
      if(action.equalsIgnoreCase("connect")){
         try{
            // gets the inet adress from the name entered using that to create a socket and printwriter and turns the connect button into a disconect button
            
            InetAddress server=InetAddress.getByName(jtfServer.getText());
            com=new Socket(server,PORT);
            // creates a  thread to listen for messages
            connected=true;
            listen=new ChatReader(com);
            th=new Thread(listen);
            th.start();
            bw=new BufferedWriter(new OutputStreamWriter(com.getOutputStream()));
            jbSend.setEnabled(true);
            jbSetUserName.setEnabled(true);
            jbConnect.setText("Disconnect");
         }
         
         catch(Exception e){
            JOptionPane.showMessageDialog(null,"error failed to connect to server");
         }
      }
      // if the disconect button is clicked
      if(action.equalsIgnoreCase("disconnect")){
         try{
            bw.write("*EXIT*\n");
            bw.flush();
            Thread.currentThread().sleep(2000);
         }
         catch(Exception e){}
         
         close(false);
      }
      if(action.equalsIgnoreCase("send")){
         try{
            bw.write(jtaSend.getText()+"\n");
            bw.flush();
            jtaSend.setText("");
         }
         catch(Exception e){
            close(false);
         }

              
      }
      if(action.equalsIgnoreCase("Set User Name")){
         try{
            bw.write(jtfName.getText()+"\n");
            bw.flush();
            userNameChosen();
         }
         catch(Exception e){
            close(false);
         }
      }
      
         
   }
   class ChatReader implements Runnable{
   
      private BufferedReader br=null;
      
      public ChatReader(Socket sock){
         try{
            br=new BufferedReader(new InputStreamReader(sock.getInputStream()));
         }
         catch(Exception e){}
      }
      
      public void run(){
         try{
            while(connected){ // loops as long as connected is true
               String message=br.readLine();
               if(message.equals("*EXIT*")){
                  JOptionPane.showMessageDialog(null,"Server is Exiting");
                  close(false);
               }
               else if(!(message.isEmpty())){
                  jtaChat.append("\n"+message+"\n");
               }
            }
         }
         catch(Exception e){}
      }
      
   }
   
   private class ExitListener extends WindowAdapter{
      
      public void WindowClosing(WindowEvent we){
         close(true);
      }
   }
      
   
}
      
   