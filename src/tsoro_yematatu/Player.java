package tsoro_yematatu;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Player extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	
	private Container contentPane;
	private JTextArea message;
	private JButton[] buttons = new JButton[7];
	
	private int playerID;
	private int otherPlayer;
	
	private int turnsMade;
	
	private Integer[] myPoints;
	private Integer[] enemyPoints;
	private boolean buttonsEnabled;
	
	private int piecesUsed;
	private int enemyPieces;
		
	private List<Integer[]> segments;
	protected boolean winner;
	
	private ClientSideConnection clientSideConnection;
	
	public Player(int width, int height) {
		this.width = width;
		this.height = height;
		
		this.contentPane = this.getContentPane();
		this.message = new JTextArea();
		this.buttons[0] = new JButton("1");
		this.buttons[1] = new JButton("2");
		this.buttons[2] = new JButton("3");
		this.buttons[3] = new JButton("4");
		this.buttons[4] = new JButton("5");
		this.buttons[5] = new JButton("6");
		this.buttons[6] = new JButton("7");
		
		this.turnsMade = 0;
		this.myPoints = new Integer[3];
		this.enemyPoints = new Integer[3];
		
		this.piecesUsed = 0;
		this.enemyPieces = 0;
		
		this.segments = getSegmentsList();
		this.winner = false;
	}
	
	public void setUpGUI() {
		this.setSize(width, height);
		this.setTitle("Player #" + playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane.setLayout(new GridLayout(1, 8));
		contentPane.add(message);
		
		message.setText("Creating a simple turn-based game in Java.");
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		
		contentPane.add(buttons[0]);
		contentPane.add(buttons[1]);
		contentPane.add(buttons[2]);
		contentPane.add(buttons[3]);
		contentPane.add(buttons[4]);
		contentPane.add(buttons[5]);
		contentPane.add(buttons[6]);
		
		if (playerID == 1) {
			message.setText("You are player #1. You go first.");
			otherPlayer = 2;
			buttonsEnabled = true;
		}
		else {
			message.setText("You are player #2. Wait for your turn");
			otherPlayer = 1;
			buttonsEnabled = false;
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					updateTurn();
				}
			});
			t.start();
		}
		
		toggleButtons();
		
		this.setVisible(true);
	}
	
	public void connectToServer() {
		clientSideConnection = new ClientSideConnection();
	}
	
	public void setUpButtons() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				int bNum = Integer.parseInt(b.getText());
				
				message.setText("You clicked button #" + bNum + ". Now wait for player #" + otherPlayer);
				turnsMade++;
				System.out.println("Turns made: " + turnsMade);
				
				buttonsEnabled = false;				
				toggleButtons();				
				
				if (piecesUsed < 3) {
					myPoints[piecesUsed] = bNum;					
					piecesUsed++;
				}
 				
				clientSideConnection.sendButtonNum(bNum);
				
				checkWinner();
				
				if (winner == false) {
					Thread t = new Thread(new Runnable() {
						public void run() {
							updateTurn();
						}
					});
					t.start();
				}
			}
		};
		
		buttons[0].addActionListener(al);
		buttons[1].addActionListener(al);
		buttons[2].addActionListener(al);
		buttons[3].addActionListener(al);
		buttons[4].addActionListener(al);
		buttons[5].addActionListener(al);
		buttons[6].addActionListener(al);
	}
	
	public void toggleButtons() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(buttonsEnabled);
			
			for (int j = 0; j < myPoints.length; j++) {
				if (buttons[i].getText().contentEquals(String.valueOf(myPoints[j])) ||
						buttons[i].getText().contentEquals(String.valueOf(enemyPoints[j]))) {
					buttons[i].setEnabled(false);
				}
				
			}
		}
	}
	
	public void updateTurn() {
		int n = clientSideConnection.receiveButtonNum();
		
		if (enemyPieces < 3) {
			enemyPoints[enemyPieces] = n;			
			enemyPieces++;
		}
		
		checkWinner();
		buttonsEnabled = true;

		toggleButtons();
	}
	
	private void checkWinner() {
		for (Integer[] segment : segments) {			
			if (Arrays.asList(segment).containsAll(Arrays.asList(myPoints))) {
				buttonsEnabled = false;
				winner = true;
				
				System.out.println("You WIN!");
				clientSideConnection.closeConnection();
			}
		}		
	}
	
	
	private List<Integer[]> getSegmentsList() {
		List<Integer[]> segments = new ArrayList<Integer[]>();
		
		Integer[] horizontal1 = {2, 3, 4};
		Integer[] horizontal2 = {5, 6, 7};
		Integer[] diagonal1 = {1, 2, 5};
		Integer[] diagonal2 = {1, 4, 7};
		Integer[] vertical = {1, 3, 6};
		
		segments.add(horizontal1);
		segments.add(horizontal2);
		segments.add(diagonal1);
		segments.add(diagonal2);
		segments.add(vertical);
		
		return segments;
	}
	
	// Client Connection Inner Class
	private class ClientSideConnection {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		
		public ClientSideConnection() {
			System.out.println("----Client----");
			
			try {
				socket = new Socket("localhost", 51734);			
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				
				playerID = dataIn.readInt();
				System.out.println("Connected to server as Player #" + playerID + ".");
			}
			catch (IOException e) {
				System.out.println("IOException - ClientSideConnection()");
			}
		}
		
		public void sendButtonNum (int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IO Exception - sendButtonNum()");
			}
		}
		
		public int receiveButtonNum () {
			int n = -1;
			
			try {
				n = dataIn.readInt();
			}
			catch (IOException e) {
				System.out.println("IO Exception - receiveButtonNum()");
			}
			
			return n;
		}
		
		public void closeConnection() {
			try {
				socket.close();
				System.out.println("----CONNECTION CLOSED----");
			}
			catch (IOException e) {
				System.out.println("IOException - closeConnection()");
			}
		}
	}
	
	public static void main(String[] args) {
		Player p = new Player(500, 100);
		p.connectToServer();
		p.setUpGUI();
		p.setUpButtons();
	}
}
