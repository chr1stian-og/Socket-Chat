import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Client extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private DataOutputStream output;
	private DataInputStream input;
	
	private JPanel panel;
	private JTextArea taChat;
	private JTextField tfInput;
	private JButton btSend, btExit;
	private Socket client;
	
	
	public static void main(String []args) throws Exception{
		//set the server ip
		String servername = JOptionPane.showInputDialog(null, "Enter the server ip: ", "IP to connect",
				JOptionPane.PLAIN_MESSAGE);
		
		//set the open port of the server to connect to
		String portString = JOptionPane.showInputDialog(null, "Enter port of the server: ", "Port to connect",
				JOptionPane.PLAIN_MESSAGE);
			
		int port = Integer.parseInt(portString);
		//take username from user
		String name = JOptionPane.showInputDialog(null, "Enter your name: ", "Username",
				JOptionPane.PLAIN_MESSAGE);

		new Client(name, servername, port);
	}
	
	public Client(String username, String servername, int port) throws Exception{
		setTitle(username);//set the title for frame as username
		
		this.username = username;
		
		//socket object to connect to server
		client = new Socket(servername, port);
		
		//objects to get and send data to server respectivelly
		input = new DataInputStream(client.getInputStream());
		output = new DataOutputStream(client.getOutputStream());
		
		
		//send the username to server so that its handled with HandleClients class
		output.writeUTF(username);
		
		buildInterface();
		
		//listening thread
		new MessagesThread().start();
	}
	
	public void buildInterface() {
		ImageIcon icon = new ImageIcon("chat icon 2.png");
		setIconImage(icon.getImage());
		setSize(100, 250);
		setLocationRelativeTo(null);
		
		
		btSend = new JButton("send");
		btSend.setBackground(Color.green);
		
		btExit = new JButton("exit");
		btExit.setBackground(Color.red);
		
		taChat = new JTextArea();
		taChat.setRows(10);
		taChat.setColumns(15);
		taChat.setBackground(Color.LIGHT_GRAY);
		taChat.setEditable(false);
		
		tfInput = new JTextField(30);
		JScrollPane sp = new JScrollPane(taChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		add(sp, "Center");
		
		panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.BLUE);
		panel.add(tfInput);
		panel.add(btSend);
		panel.add(btExit);
		
		//add the panel and the components to the frame
		add(panel,"South");
		
		btSend.addActionListener(this);
		getRootPane().setDefaultButton(btSend);
		btExit.addActionListener(this);
		
		setVisible(true);
		pack();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try{
			if (event.getSource() == btExit) {
				//tell the server to end the session
				output.writeUTF(username+" "+"out.");
				System.exit(0);
			}else {
				//send message to server
				output.writeUTF(tfInput.getText());
				taChat.append(tfInput.getText()+"\n");
				tfInput.setText("");
			}
		}catch(Exception e) {}
		
		
	}
	
	class MessagesThread extends Thread{
		public void run() {
			String line;
			try {
				while (true) {
					//receive the server's message and add it on text area
					line = input.readUTF();
					System.out.println(line);
					taChat.append("server: "+line + "\n");
				}
			}catch(Exception e) {e.printStackTrace();}
		}
	}        						
	
}


