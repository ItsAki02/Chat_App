import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Client extends JFrame {

      Socket socket;
      BufferedReader br;
      PrintWriter out;

      private JLabel heading = new JLabel("Client Area");
      private JTextArea msgArea = new JTextArea();
      private JTextField msgInput = new JTextField();
      private Font font = new Font("Roboto",Font.PLAIN,20);

     public Client(){
         try{
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7778);
            System.out.println("connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvents();
            startReading();
            //startWriting();
         }
         catch (Exception e){
            e.printStackTrace();
         }
     }
     /**
     * 
     */
    private void handleEvents(){
        msgInput.addKeyListener( new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()== 10){
                   // System.out.println("you have pressed enter button");
                   String contentToSend=msgInput.getText();
                   msgArea.append("Me :"+contentToSend+"\n");
                   out.println(contentToSend);
                   out.flush();
                   msgInput.setText("");
                   msgInput.requestFocus();
                }
            }
            
        });
     }
 
     private void createGUI(){
        this.setTitle("Client Messanger[END]");
        this.setSize(600,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        //coding for component
        heading.setFont(font);
        msgArea.setFont(font);
        msgInput.setFont(font);
        heading.setIcon(new ImageIcon("chat.png"));
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        
        msgArea.setEditable(false);
        msgInput.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //frame layout
        this.setLayout(new BorderLayout());

        //adding the components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane JscrollPane= new JScrollPane(msgArea);
        this.add(JscrollPane,BorderLayout.CENTER);
        this.add(msgInput,BorderLayout.SOUTH);

        this.setVisible(true);

     }
     public void startReading(){
        //thread will read the data and give
        Runnable r1=()->{
           System.out.println("reader started...");
            try{
           while(true){
            
            String msg = br.readLine();
            if(msg.equals("exit")){
                System.out.println("Server terminated the chat");
                JOptionPane.showMessageDialog(this,"Server terminated the chat");
                msgInput.setEnabled(false);
                socket.close();
                break;
            }
            // System.out.println("Server : "+msg);
            msgArea.append("Server :" + msg+"\n");
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        };

        new Thread(r1).start();

    }

   

    public void startWriting(){
          //tread will take the user data and will give it to client
          Runnable r2=()->{
            System.out.println("writer started...");
            try{
            while(true && !socket.isClosed()){
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                }
                System.out.println("connection is closed");
              
             }
             catch(Exception e){
                e.printStackTrace();
             }
          };

          new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("this is client...");
        new Client();
    } 
}
