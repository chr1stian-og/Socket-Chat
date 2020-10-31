import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;



public class Server extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Vectors to store the clients and usernames
	private Vector <String> users = new Vector<String>();
	private Vector <HandleClients> clients = new Vector<HandleClients>();
	
	private JPanel panel;
	private JTextArea taChat;
	private JTextField tfInput;
	private JButton btSend, btExit;
	private Socket client;
	
	private DataOutputStream output;
	private DataInputStream input;
	
	public static void main(String[] args) throws Exception {
		new Server().process();
	}
	 
	public void process() throws Exception{
		@SuppressWarnings("resource")
		
		//Server creation and incialization
				
		//For choosing the port to be open
		String portString = JOptionPane.showInputDialog(null, "Enter a port to open: ", "Choose port (0-65535)",
				JOptionPane.PLAIN_MESSAGE);
		int port = Integer.parseInt(portString);
		
		
		ServerSocket server = new ServerSocket(port, 10);
		
		System.out.println("Server Started...");
		System.out.println("Waiting for connection...");
		
		while (true){
			//objecto para aceitar conexoes e gerir os clientes
			client = server.accept();
			
			if (client.isConnected())
				System.out.println("Client connected ");
				buildInterface();
			
			HandleClients c = new HandleClients(client);
			clients.add(c);
		}
	}
	
	public void buildInterface() {
		setTitle("Server");
		ImageIcon icon = new ImageIcon("chat icon 2.png");
		setIconImage(icon.getImage());
		
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
		panel.setBackground(Color.gray);
		panel.add(tfInput);
		panel.add(btSend);
		panel.add(btExit);
		
		//add the panel and components 
		add(panel,"South");
		
		btSend.addActionListener(this);
		getRootPane().setDefaultButton(btSend);
		btExit.addActionListener(this);
		
		setSize(100, 250);
		setVisible(true);
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try{
			if (event.getSource() == btExit){
				System.exit(0);
			}else {
				output.writeUTF(tfInput.getText());
				taChat.append(tfInput.getText()+"\n");
				tfInput.setText("");
								
			}
		}catch(Exception e) {}
	}
	
	public void broadcast(String user, String message) throws IOException {
		for (HandleClients c : clients) {
			if (!c.getsUserName().equals(user)) {c.sendMessage(user, message);}
		}
	}
	
	
	//class for handling the clients 
	class HandleClients extends Thread{
		private String u_name;
		private Server ser;
	
		public HandleClients(Socket client) throws Exception {
			ser = new Server();
			
			//object to receive client input
			input = new DataInputStream(client.getInputStream());
			
			//object to make output for clients
			output = new DataOutputStream(client.getOutputStream());
			
			this.u_name = input.readUTF();
			ser.users.add(this.u_name);
			
			//start the thread
			start();
		}
		
		public void sendMessage(String username, String msg) throws IOException {
			output.writeUTF(username + ":" + msg);
		}
		
		public String getsUserName() {return u_name;}
		
		
		public void run() {			
			String line;

			try {
				while(true) {
					line = input.readUTF();
					System.out.println(line);
					taChat.append("client"+ ": "+line+"\n");
					//Client is removed if he types "exit" or "end"
					if (line.equalsIgnoreCase("end")){
						ser.clients.remove(this);
						ser.users.remove(this.u_name);
						break;
					}
					
					//broadcast message contained in line to clients 
					ser.broadcast(u_name, line);
					
				}
			}catch(Exception e){}
					
		}
		
	}

}

