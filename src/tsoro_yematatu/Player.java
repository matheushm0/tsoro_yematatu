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
	private Integer[] allPoints;
	
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
		this.buttons[0] = new JButton("0");
		this.buttons[1] = new JButton("1");
		this.buttons[2] = new JButton("2");
		this.buttons[3] = new JButton("3");
		this.buttons[4] = new JButton("4");
		this.buttons[5] = new JButton("5");
		this.buttons[6] = new JButton("6");
		
		this.turnsMade = 0;
		this.myPoints = new Integer[3];
		this.enemyPoints = new Integer[3];
		this.allPoints = new Integer[7];
		
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
				
				if (piecesUsed < 3) {
					myPoints[piecesUsed] = bNum;					
					piecesUsed++;
				}
				
				turnsMade++;
				System.out.println("Turns made: " + turnsMade);
								
				buttonsEnabled = false;	
 				
				if (turnsMade > 3) {
					validateMove(bNum);
				} 					
				else {
					toggleButtons();
					checkWinner();
					clientSideConnection.sendButtonNum(bNum);
					clientSideConnection.sendUpdateArrayFlag(false);
				}
												
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

			if (turnsMade >= 3 && enemyPieces == 3) {
				if (allPoints[i] != null && allPoints[i] == 2) {
					buttons[i].setEnabled(false);
				}

				if (allPoints[i] == null) {
					buttons[i].setEnabled(false);
				}
			} 
			else {
				if (allPoints[i] != null) {
					buttons[i].setEnabled(false);
				}
			}
		}
	}
	
	public void validateMove(int bNum) {						
		boolean isValid = false;
		
		Integer[] buttonToSwitch = new Integer[2];
		buttonToSwitch[0] = bNum;
		
		for (int i = 0; i < allPoints.length; i++) {
			if (allPoints[i] == null) {
				buttonToSwitch[1] = i;
			}
		}
		
		for (Integer[] segment : segments) {			
			if (Arrays.asList(segment).containsAll(Arrays.asList(buttonToSwitch))) {
				isValid = true;
				
				for (int i = 0; i < myPoints.length; i++) {
					if (myPoints[i].equals(buttonToSwitch[0])) {
						myPoints[i] = buttonToSwitch[1];
					}
				}
			}			
		}
		
		if (isValid) {			
			updateAllPointsArray();
			toggleButtons();
			checkWinner();
			
			clientSideConnection.sendButtonNum(bNum);
			clientSideConnection.sendUpdateArrayFlag(true);
			clientSideConnection.sendUpdatedPoints(myPoints[0], myPoints[1], myPoints[2]);
		}
		else {
			message.setText("Invalid Movement");
		}
	}
	
	public void updateAllPointsArray() {
		allPoints = new Integer[7];
		
		for (int i = 0; i < myPoints.length; i++) {
			if (myPoints[i] != null) {
				allPoints[myPoints[i]] = 1;
			}
			
			if (enemyPoints[i] != null) {
				allPoints[enemyPoints[i]] = 2;
			}	
		}
	}
	
	public void updateTurn() {
		int n = clientSideConnection.receiveButtonNum();
		boolean updateArray = clientSideConnection.receiveUpdateArrayFlag();
		
		if (enemyPieces < 3) {
			enemyPoints[enemyPieces] = n;			
			enemyPieces++;
		}

		if (turnsMade <= 3 && updateArray == false) {
			for (int i = 0; i < myPoints.length; i++) {
				if (myPoints[i] != null) {
					allPoints[myPoints[i]] = 1;
				}
				
				if (enemyPoints[i] != null) {
					allPoints[enemyPoints[i]] = 2;
				}			
			}
		}
		
		if (updateArray == true) {
			Integer[] updatedPointsArray = clientSideConnection.receiveUpdatedPoints();
			
			enemyPoints[0] = updatedPointsArray[0];
			enemyPoints[1] = updatedPointsArray[1];
			enemyPoints[2] = updatedPointsArray[2];

			updateAllPointsArray();
			
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
		
		Integer[] horizontal1 = {1, 2, 3};
		Integer[] horizontal2 = {4, 5, 6};
		Integer[] diagonal1 = {0, 1, 4};
		Integer[] diagonal2 = {0, 3, 6};
		Integer[] vertical = {0, 2, 5};
		
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
		
		public void sendUpdatedPoints (int n0, int n1, int n2) {
			try {
				dataOut.writeInt(n0);
				dataOut.writeInt(n1);
				dataOut.writeInt(n2);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IO Exception - sendUpdatedPoints()");
			}
		}
		
		public Integer[] receiveUpdatedPoints () {
			Integer[] n = {-1, -1, -1};
			
			try {
				n[0] = dataIn.readInt();
				n[1] = dataIn.readInt();
				n[2] = dataIn.readInt();
			}
			catch (IOException e) {
				System.out.println("IO Exception - receiveUpdatedPoints()");
			}
			
			return n;
		}
		
		public void sendUpdateArrayFlag (boolean updateArray) {
			try {
				dataOut.writeBoolean(updateArray);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IO Exception - sendUpdateArrayFlag()");
			}
		}
		
		public boolean receiveUpdateArrayFlag  () {
			boolean updateArray = false;
			
			try {
				updateArray = dataIn.readBoolean();
			}
			catch (IOException e) {
				System.out.println("IO Exception - receiveUpdateArrayFlag()");
			}
			
			return updateArray;
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
