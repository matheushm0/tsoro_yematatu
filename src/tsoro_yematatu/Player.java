package tsoro_yematatu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class Player extends JFrame {
	private static final long serialVersionUID = 1L;
	
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
		
	private int turnsMade;
	private int otherPlayer;
	
	private Integer[] myPoints;
	private Integer[] enemyPoints;
	private Integer[] allPoints;
	
	private boolean buttonsEnabled;
	
	private int piecesUsed;
	private int enemyPieces;
		
	private List<Integer[]> segments;
	
	private ClientSideConnection clientConnection;
	private ClientChatConnection chatConnection;
	
	public Player() {		
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

		this.turnsMade = 0;
		this.myPoints = new Integer[3];
		this.enemyPoints = new Integer[3];
		this.allPoints = new Integer[7];

		this.piecesUsed = 0;
		this.enemyPieces = 0;

		this.segments = getSegmentsList();
	}

	public void setUpGUI() {
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setSize(1300, 700);
		this.setTitle("Tsoro Yematatu - Player #" + clientConnection.getPlayerID());
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

		chatScrollPane.setViewportView(chatArea);
		chatScrollPane.setBounds(700, 80, 490, 460);
		
		this.add(chatTextField);
		this.add(chatButton);
		this.add(chatScrollPane);
		
		this.setVisible(true);
		
		Thread chatThread = new Thread(new Runnable() {
			public void run() {
				updateChat();
			}
		});
		chatThread.start();
				
		if (clientConnection.getPlayerID() == 1) {
			chatArea.append("----- You are player #1. You go first. -----");
			otherPlayer = 2;
			buttonsEnabled = true;
		} 
		else {
			chatArea.append("----- You are player #2. Wait for your turn. -----");
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
	}
	
	public void connectToServer() {
		clientConnection = new ClientSideConnection();
		chatConnection = new ClientChatConnection();
	}
	
	public void setUpButtons() {
		ActionListener al = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				int bNum = Integer.parseInt(b.getActionCommand());
				
				chatArea.append("\n----- You clicked button #" + bNum + ". Now wait for your turn. -----");
				
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
					clientConnection.sendButtonNum(bNum);
					clientConnection.sendUpdateArrayFlag(false);
				}
												
				Thread t = new Thread(new Runnable() {
					public void run() {
						updateTurn();
					}
				});
				t.start();
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
	
	public void setUpChat() {
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
	
	public void sendChatMessage() {
		String message = chatTextField.getText();
		
		if (!message.isEmpty()) {
			chatArea.append("\nPlayer #" + clientConnection.getPlayerID() + ": " + message);
			chatConnection.sendMessage(message);
			chatTextField.setText("");
		}
	}
	
	public void updateChat() {
		String message = "";
		
		while (!message.equalsIgnoreCase("exit")) {
			message = chatConnection.receiveMessage();
			chatArea.append("\nPlayer #" + otherPlayer + ": " + message);	
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
			toggleButtonsAfterPiecesPlaced();
			checkWinner();
			
			clientConnection.sendButtonNum(bNum);
			clientConnection.sendUpdateArrayFlag(true);
			clientConnection.sendUpdatedPoints(myPoints[0], myPoints[1], myPoints[2]);
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
		int n = clientConnection.receiveButtonNum();
		boolean updateArray = clientConnection.receiveUpdateArrayFlag();
		
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
			Integer[] updatedPointsArray = clientConnection.receiveUpdatedPoints();
			
			enemyPoints[0] = updatedPointsArray[0];
			enemyPoints[1] = updatedPointsArray[1];
			enemyPoints[2] = updatedPointsArray[2];

			updateAllPointsArray();
			
		}
		
		checkWinner();
		buttonsEnabled = true;

		if (turnsMade >= 3 && enemyPieces == 3) {
			toggleButtonsAfterPiecesPlaced();
		}
		else {
			toggleButtons();
		}
	}
	
	public void toggleButtons() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(buttonsEnabled);
			
			if (allPoints[i] != null) {
				buttons[i].setEnabled(false);
			}
		}
		
		setButtonColor();
	}
	
	public void toggleButtonsAfterPiecesPlaced() {
		
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
	
	public void setButtonColor() {
		for (int i = 0; i < enemyPoints.length; i++) {
			if (clientConnection.getPlayerID() == 1) {
				if (myPoints[i] != null) {
					buttons[myPoints[i]].setIcon(player1Image);
					buttons[myPoints[i]].setDisabledIcon(player1Image);
				}

				if (enemyPoints[i] != null) {
					buttons[enemyPoints[i]].setDisabledIcon(player2Image);
				}
			}

			if (clientConnection.getPlayerID() == 2) {
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
				buttonsEnabled = false;
				
				chatArea.append("\n----- YOU WIN! -----");
				clientConnection.closeConnection();
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
	
	public static void main(String[] args) {
		Player p = new Player();
		p.connectToServer();
		p.setUpGUI();
		p.setUpButtons();
		p.setUpChat();
	}
}
