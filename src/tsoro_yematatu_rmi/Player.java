package tsoro_yematatu_rmi;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class Player extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//UI
	private JLabel displayField;
	private ImageIcon image;
	private ImageIcon defaultImage;
	private ImageIcon player1Image;
	private ImageIcon player2Image;
	
	private JButton[] buttons = new JButton[7];
	
	private JTextArea chatArea;
	private JButton chatButton;
	private JScrollPane chatScrollPane;
	private JTextField chatTextField;
		
	//GAME
	private int playerID;
	private int turnsMade;
	private int otherPlayer;
	
	private Integer[] myPoints;
	private Integer[] enemyPoints;
	private Integer[] allPoints;
	
	private boolean buttonsEnabled;
	
	private int piecesUsed;
	private int enemyPieces;
		
	private List<Integer[]> segments;
	
	private boolean winner;
	private boolean draw;
	private int drawCount;
	
	//CONNECTION
	private ServerInterface serverInterface;
	private ClientSideConnection clientConnection;
	
	public Player() {		
		this.turnsMade = 0;
		this.myPoints = new Integer[3];
		this.enemyPoints = new Integer[3];
		this.allPoints = new Integer[7];

		this.piecesUsed = 0;
		this.enemyPieces = 0;
		
		this.segments = getSegmentsList();

		this.winner = false;
		this.draw = false;
		this.drawCount = 0;
				
		connectToServer();

		initComponents();
		setUpGUI();
		
		setUpPlayers();
		
		setUpButtons();
		setUpChat();
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
	
	private void connectToServer() {
		try {
			serverInterface = (ServerInterface) Naming.lookup("//localhost/ServerRef");

			this.playerID = serverInterface.getPlayerID();
			
			clientConnection = new ClientSideConnection();
			
			String playerURL = "//localhost/Player" + playerID + "Ref";
			Naming.rebind(playerURL, clientConnection);
			
			serverInterface.lookupPlayer(playerURL, playerID);
		} 
		catch (Exception e) {
			System.out.println("Exception - connectToServer()");
		}
	}
	
	private void initComponents() {
		this.buttons[0] = new JButton();
		this.buttons[1] = new JButton();
		this.buttons[2] = new JButton();
		this.buttons[3] = new JButton();
		this.buttons[4] = new JButton();
		this.buttons[5] = new JButton();
		this.buttons[6] = new JButton();
		
		this.chatArea = new JTextArea();
		this.chatButton = new JButton();
		this.chatScrollPane = new JScrollPane();
		this.chatTextField = new JTextField();

		this.image = new ImageIcon(this.getClass().getResource("/resources/images/board.png"));
		
		this.defaultImage = new ImageIcon(this.getClass().getResource("/resources/images/defaultImage.png"));
		this.player1Image = new ImageIcon(this.getClass().getResource("/resources/images/player1.png"));
		this.player2Image = new ImageIcon(this.getClass().getResource("/resources/images/player2.png"));
		
		this.displayField = new JLabel();
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setSize(1300, 700);
		this.setTitle("Tsoro Yematatu - Player #" + playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		displayField.setIcon(image);
		this.setContentPane(displayField);

		buttons[0].setBounds(290, 40, 80, 80);
		buttons[0].setOpaque(false);
		buttons[0].setContentAreaFilled(false);
		buttons[0].setBorderPainted(false);
		buttons[0].setFocusable(false);
		buttons[0].setIcon(defaultImage);
		buttons[0].setDisabledIcon(defaultImage);
		buttons[0].setForeground(Color.WHITE);
		buttons[0].setActionCommand("0");

		buttons[1].setBounds(148, 327, 80, 80);
		buttons[1].setOpaque(false);
		buttons[1].setContentAreaFilled(false);
		buttons[1].setBorderPainted(false);
		buttons[1].setFocusable(false);
		buttons[1].setIcon(defaultImage);
		buttons[1].setDisabledIcon(defaultImage);
		buttons[1].setForeground(Color.BLACK);
		buttons[1].setActionCommand("1");
		
		buttons[2].setBounds(290, 327, 80, 80);
		buttons[2].setOpaque(false);
		buttons[2].setContentAreaFilled(false);
		buttons[2].setBorderPainted(false);
		buttons[2].setFocusable(false);
		buttons[2].setIcon(defaultImage);
		buttons[2].setDisabledIcon(defaultImage);
		buttons[2].setForeground(Color.BLACK);
		buttons[2].setActionCommand("2");

		buttons[3].setBounds(448, 327, 80, 80);
		buttons[3].setOpaque(false);
		buttons[3].setContentAreaFilled(false);
		buttons[3].setBorderPainted(false);
		buttons[3].setFocusable(false);
		buttons[3].setIcon(defaultImage);
		buttons[3].setDisabledIcon(defaultImage);
		buttons[3].setForeground(Color.WHITE);
		buttons[3].setActionCommand("3");

		buttons[4].setBounds(45, 539, 80, 80);
		buttons[4].setOpaque(false);
		buttons[4].setContentAreaFilled(false);
		buttons[4].setBorderPainted(false);
		buttons[4].setFocusable(false);
		buttons[4].setIcon(defaultImage);
		buttons[4].setDisabledIcon(defaultImage);
		buttons[4].setForeground(Color.WHITE);
		buttons[4].setActionCommand("4");

		buttons[5].setBounds(291, 539, 80, 80);
		buttons[5].setOpaque(false);
		buttons[5].setContentAreaFilled(false);
		buttons[5].setBorderPainted(false);
		buttons[5].setFocusable(false);
		buttons[5].setIcon(defaultImage);
		buttons[5].setDisabledIcon(defaultImage);
		buttons[5].setForeground(Color.WHITE);
		buttons[5].setActionCommand("5");

		buttons[6].setBounds(556, 539, 80, 80);
		buttons[6].setOpaque(false);
		buttons[6].setContentAreaFilled(false);
		buttons[6].setBorderPainted(false);
		buttons[6].setFocusable(false);
		buttons[6].setIcon(defaultImage);
		buttons[6].setDisabledIcon(defaultImage);
		buttons[6].setForeground(Color.WHITE);
		buttons[6].setActionCommand("6");

		this.add(buttons[0]);
		this.add(buttons[1]);
		this.add(buttons[2]);
		this.add(buttons[3]);
		this.add(buttons[4]);
		this.add(buttons[5]);
		this.add(buttons[6]);
		
		//set up chat
		chatTextField.setBounds(700, 550, 410, 40);

		chatButton.setText("Send");
		chatButton.setBounds(1110, 550, 80, 39);
		
		chatArea.setEditable(false);
		chatArea.setColumns(20);
		chatArea.setRows(5);		
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setFont(chatArea.getFont().deriveFont(15f));
		chatArea.setMargin(new Insets(10, 10, 10, 10));
		
		chatArea.append("----- WELCOME TO TSORO YEMATATU -----");
		chatArea.append("\n----- If you want to surrender write !surrender in the chat -----");
		chatArea.append("\n----- If you want to request a draw write !draw in the chat -----");


		DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScrollPane.setViewportView(chatArea);
		chatScrollPane.setBounds(700, 80, 490, 460);
		
		this.add(chatTextField);
		this.add(chatButton);
		this.add(chatScrollPane);
		
		this.setVisible(true);
	}
	
	private void setUpPlayers() {
		
		if (playerID == 1) {
			chatArea.append("\n----- You are player #1. You go first. -----");
			otherPlayer = 2;
			buttonsEnabled = true;
		} 
		else {
			chatArea.append("\n----- You are player #2. Wait for your turn. -----");
			otherPlayer = 1;
			buttonsEnabled = false;
		}

		toggleButtons();
	}
	
	private void setUpButtons() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				int bNum = Integer.parseInt(b.getActionCommand());
				
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
					
					try {
						serverInterface.sendButtonNum(bNum, playerID);
					} 
					catch (Exception e) {
						System.out.println("Exception - setUpButtons()");
					}
					
					checkWinner();
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
	
	private void setUpChat() {
		ActionListener actionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				sendChatMessage();
			}
		};
		
		KeyListener keyListener = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendChatMessage();
				}
			}
		};
		
		chatButton.addActionListener(actionListener);
		chatTextField.addKeyListener(keyListener);
	}
	
	//GAME
	private class ClientSideConnection extends UnicastRemoteObject implements PlayerInterface {
		private static final long serialVersionUID = 1L;
		
		public ClientSideConnection() throws RemoteException {
			super();
			
			System.out.println("----Client----");
			System.out.println("Connected to server as Player #" + playerID + ".");
		}

		@Override
		public void updateTurn(int bNum) throws RemoteException {
			
			if (bNum != -1) {
				chatArea.append("\n----- Player #" + otherPlayer + " clicked button #" + bNum 
						+ ". It's your turn. -----");	
			
				if (enemyPieces < 3) {
					enemyPoints[enemyPieces] = bNum;			
					enemyPieces++;
				}
			}
	
			if (turnsMade <= 3) {
				for (int i = 0; i < myPoints.length; i++) {
					if (myPoints[i] != null) {
						allPoints[myPoints[i]] = 1;
					}
					
					if (enemyPoints[i] != null) {
						allPoints[enemyPoints[i]] = 2;
					}			
				}
			}
			
			buttonsEnabled = true;
	
			if (turnsMade >= 3 && enemyPieces == 3) {
				toggleButtonsAfterPiecesPlaced();
			}
			else {
				toggleButtons();
			}
			
			if (!winner) {
				checkWinner();
			}
		}

		@Override
		public void updatePoints(int p0, int p1, int p2) throws RemoteException {
			
			enemyPoints[0] = p0;
			enemyPoints[1] = p1;
			enemyPoints[2] = p2;

			updateAllPointsArray();

			buttonsEnabled = true;

			if (turnsMade >= 3 && enemyPieces == 3) {
				toggleButtonsAfterPiecesPlaced();
			} 
			else {
				toggleButtons();
			}

			if (!winner) {
				checkWinner();
			}
		}

		//CHAT
		@Override
		public void receiveMessage(String message) throws RemoteException {
			
			if (!message.equalsIgnoreCase("@exit@")) {
				
				if (!message.equalsIgnoreCase("@win@")) {
					chatArea.append("\nPlayer #" + otherPlayer + ": " + message);	
				}
							
				if (message.equalsIgnoreCase("@win@") && !winner) {
					chatArea.append("\n----- Player #" + otherPlayer + " WINS! -----");
					
					buttonsEnabled = false;
					toggleButtons();
					
					winner = true;
				}
				
				if (message.equalsIgnoreCase("!surrender") && !winner) {
					chatArea.append("\n----- YOU WIN! Player #" + otherPlayer + " surrendered. -----");
					
					buttonsEnabled = false;
					toggleButtons();
					
					winner = true;
				}
				
				if (message.equalsIgnoreCase("!draw") && !winner) {
					if (draw) {
						chatArea.append("\n----- GAME OVER! Both players agreed to a draw -----");
						
						buttonsEnabled = false;
						toggleButtons();
						
						winner = true;
						draw = true;
					}
					else {
						chatArea.append("\n----- Player #" + otherPlayer + " requested a draw. Send !draw to accept -----");
						drawCount++;
					}
				}
			}			
		}
	}

	private void toggleButtons() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(buttonsEnabled);
			
			if (allPoints[i] != null) {
				buttons[i].setEnabled(false);
			}
		}
		
		setButtonColor();
	}
	
	private void toggleButtonsAfterPiecesPlaced() {
		
		Integer[] buttonToEnable = new Integer[2];
		
		for (int i = 0; i < allPoints.length; i++) {
			if (allPoints[i] == null) {
				buttonToEnable[0] = i;
			}
		}
		
		for (int i = 0; i < allPoints.length; i++) {
			if (allPoints[i] != null) {
				buttons[i].setEnabled(false);
			}

			if (allPoints[i] == null) {
				buttons[i].setEnabled(false);
				buttons[i].setDisabledIcon(defaultImage);
			}
			
			if (allPoints[i] != null && allPoints[i] == 1) {
				buttonToEnable[1] = i;
				
				for (Integer[] segment : segments) {			
					if (Arrays.asList(segment).containsAll(Arrays.asList(buttonToEnable))) {
						buttons[buttonToEnable[1]].setEnabled(buttonsEnabled);
					}			
				}
			}
		}
		
		setButtonColor();
	}
	
	private void setButtonColor() {
		for (int i = 0; i < enemyPoints.length; i++) {
			if (playerID == 1) {
				if (myPoints[i] != null) {
					buttons[myPoints[i]].setIcon(player1Image);
					buttons[myPoints[i]].setDisabledIcon(player1Image);
				}

				if (enemyPoints[i] != null) {
					buttons[enemyPoints[i]].setDisabledIcon(player2Image);
				}
			}

			if (playerID == 2) {
				if (myPoints[i] != null) {
					buttons[myPoints[i]].setIcon(player2Image);
					buttons[myPoints[i]].setDisabledIcon(player2Image);
				}

				if (enemyPoints[i] != null) {
					buttons[enemyPoints[i]].setDisabledIcon(player1Image);
				}
			}
		}
	}	
	
	private void checkWinner() {
				
		for (Integer[] segment : segments) {			
			if (Arrays.asList(segment).containsAll(Arrays.asList(myPoints))) {		
				winner = true;
				chatArea.append("\n----- Player#" + playerID + " WINS! -----");
				
				try {
					serverInterface.sendMessage("@win@", playerID);
				} 
				catch (Exception e) {
					System.out.println("Exception - checkWinner()");
				}
			}
		}	
	}
	
	private void validateMove(int bNum) {						
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
			toggleButtonsAfterPiecesPlaced();
			
			try {
				serverInterface.sendButtonNum(bNum, playerID);
				serverInterface.updatePoints(myPoints[0], myPoints[1], myPoints[2], playerID);
			} 
			catch (Exception e) {
				System.out.println("Exception - validateMove()");
			}
			
			checkWinner();
		}
	}
	
	private void updateAllPointsArray() {
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
	
	//CHAT
	public void sendChatMessage() {
		String message = chatTextField.getText();
		
		chatArea.append("\nPlayer #" + playerID + ": " + message);
		
		try {
			serverInterface.sendMessage(message, playerID);
		} 
		catch (Exception e) {
			System.out.println("Exception - sendChatMessage()");
		}
		
		chatTextField.setText("");
		
		if (message.equalsIgnoreCase("!surrender") && !winner) {
			chatArea.append("\n----- You surrendered. Player #" + otherPlayer + " wins! -----");
			
			buttonsEnabled = false;
			toggleButtons();
			
			winner = true;
		}
		
		if (message.equalsIgnoreCase("!draw") && !winner) {
			if (draw && drawCount > 0) {
				chatArea.append("\n----- You already requested a draw. Wait for your opponent -----");
			}
			
			if (!draw && drawCount == 0) {
				chatArea.append("\n----- You requested a draw. Wait for your opponent -----");
				draw = true;
				drawCount++;
			}
			
			if (!draw && drawCount > 0) {
				chatArea.append("\n----- GAME OVER! Both players agreed to a draw -----");
				
				buttonsEnabled = false;
				toggleButtons();
				
				winner = true;
				draw = true;
			}
		}
	}
	
	public static void main(String[] args) {
		new Player();
	}
}
