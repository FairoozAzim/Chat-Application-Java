import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;


public class Client {
    
  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String userName;

  public Client(Socket socket, String userName){
      try {
          this.socket = socket;
          this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
          this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          this.userName = userName;
      } catch (IOException e){
          closeEverything(socket, bufferedReader, bufferedWriter);
      }
  }

  public void sendMessage(){
      try{
          bufferedWriter.write(userName);
          bufferedWriter.newLine();
          bufferedWriter.flush();

          try (Scanner scanner = new Scanner(System.in)) {
            while(socket.isConnected()){
                  String messageToSend = scanner.nextLine();
                  bufferedWriter.write(userName + ": " + messageToSend);
                  bufferedWriter.newLine();
                  bufferedWriter.flush();
              }
        }
        } catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
  }

  public void messageListener(){
      new Thread(new Runnable() {

        @Override
        public void run() {
            String receivedMsg;

            while(socket.isConnected()){
               try{
                receivedMsg = bufferedReader.readLine();
                System.out.println(receivedMsg);
               } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
               }
            }
            
        }
          
      }).start();
  }

  public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
    try{
        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null){
            bufferedWriter.close();
        }
        if(socket != null){
            socket.close();
        }
    } catch (IOException e){
        e.printStackTrace();
    }
 }

  public static void main(String[] args) throws UnknownHostException, IOException {
      JFrame frame = new JFrame("Client Window");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(600,600);
      JPanel panel = new JPanel(); // the panel is not visible in output
      JPanel panel2= new JPanel();

      JLabel uN = new JLabel("Enter your username for the group chat: ");
      JTextField userName = new JTextField(10);
      String username = userName.getText();
      Socket socket = new Socket("localhost",1234);
      Client client = new Client(socket, username);
      JButton enter = new JButton("Enter Chatroom");

      JLabel label = new JLabel("Write Message: ");
      JTextField tf = new JTextField(40); // accepts upto 10 characters
      JButton send = new JButton("Send");
    
// Components Added using Flow Layout

      panel.add(label); 
      panel.add(tf);
      panel.add(send);
    
      
      panel2.add(uN);
      panel2.add(userName);
      panel2.add(enter);

        // Text Area at the Center
        JTextArea ta = new JTextArea();

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, panel2);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);
        client.messageListener();
        client.sendMessage();

 }


}
