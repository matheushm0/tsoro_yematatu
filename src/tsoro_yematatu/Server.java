package tsoro_yematatu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
	private ServerSocket serverSocket;
	private int numPlayers;
	
	private ServerSideConnection player1;
	private ServerSideConnection player2;
	
	private String player1Message;
	private String player2Message;
	
	private int player1ButtonNum;
	private int player2ButtonNum;
	
	private int player1UpdatedPoints0;
	private int player1UpdatedPoints1;
	private int player1UpdatedPoints2;
	
	private int player2UpdatedPoints0;
	private int player2UpdatedPoints1;
	private int player2UpdatedPoints2;
	
	private boolean player1UpdateFlag;
	private boolean player2UpdateFlag;

	 	
	public Server() {
		System.out.println("----Iniciando Servidor----");
		numPlayers = 0;
		
		try {
			serverSocket = new ServerSocket(51734);
		} 
		catch (IOException e) {
			System.out.println("IOException - Server()");
		}
	}
	
	public void acceptConnections() {
		try {
			System.out.println("Esperando conexões...");
			
			while (numPlayers < 2) {
				Socket s = serverSocket.accept();
				numPlayers++;
				
				System.out.println("Jogador #" + numPlayers + " se conectou!");
				
				ServerSideConnection serverSideConnection = new ServerSideConnection(s, numPlayers);
				
				if (numPlayers == 1) {
					player1 = serverSideConnection;
				}
				else {
					player2 = serverSideConnection;
				} 
				
				Thread thread = new Thread(serverSideConnection);
				thread.start();
			}
			
			System.out.println("Há 2 jogadores conectados. Não aceitando mais conexões.");
		} 
		catch (IOException e) {
			System.out.println("IOException - acceptConnections()");
		}
	}
	
	private class ServerSideConnection implements Runnable {
		
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private int playerID;
		
		public ServerSideConnection(Socket s, int id) {
			socket = s;
			playerID = id;
			
			try {
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
			}
			catch (IOException e) {
				System.out.println("IOExcepiton - ServerSideConnection()");
			}
		}
		
		@Override
		public void run() {
			try {
				dataOut.writeInt(playerID);
				dataOut.flush();
				
				while (true) {
					if (playerID == 1) {
						player1ButtonNum = dataIn.readInt();
						player1UpdateFlag = dataIn.readBoolean();
						player1Message = dataIn.readUTF();
						
						System.out.println("Player1: " + player1Message);

						System.out.println("Player 1 clicked button #" + player1ButtonNum);
						player2.sendButtonNum(player1ButtonNum);
						player2.sendUpdateArrayFlag(player1UpdateFlag);
						
						
						if (player1UpdateFlag == true) {
							player1UpdatedPoints0 = dataIn.readInt();
							player1UpdatedPoints1 = dataIn.readInt();
							player1UpdatedPoints2 = dataIn.readInt();

							player2.sendUpdatedPoints(player1UpdatedPoints0, player1UpdatedPoints1, player1UpdatedPoints2);
						}

					} 
					else {
						player2ButtonNum = dataIn.readInt();
						player2UpdateFlag = dataIn.readBoolean();
						player2Message = dataIn.readUTF();
						
						System.out.println("Player2: " + player2Message);

						System.out.println("Player 2 clicked button #" + player2ButtonNum);
						player1.sendButtonNum(player2ButtonNum);
						player1.sendUpdateArrayFlag(player2UpdateFlag);
						
						if (player2UpdateFlag == true) {
							player2UpdatedPoints0 = dataIn.readInt();
							player2UpdatedPoints1 = dataIn.readInt();
							player2UpdatedPoints2 = dataIn.readInt();

							player1.sendUpdatedPoints(player2UpdatedPoints0, player2UpdatedPoints1, player2UpdatedPoints2);
						}
					}
				}
			}
			catch (IOException e) {
				System.out.println("IOException - run() SSC");
			}
		}
		
		public void sendMessage(String message) {
			try {
				dataOut.writeUTF(message);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IO Exception - sendMessage()");
			}
		}
		
		public void sendButtonNum(int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IOException - sendButtonNum()");
			}
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
		
		public void sendUpdateArrayFlag (boolean updateArray) {
			try {
				dataOut.writeBoolean(updateArray);
				dataOut.flush();
			}
			catch (IOException e) {
				System.out.println("IO Exception - sendUpdateArrayFlag()");
			}
		}
		
//		public void closeConnection() {
//			try {
//				socket.close();
//				System.out.println("----CONNECTION CLOSED----");
//			}
//			catch (IOException e) {
//				System.out.println("IOException - closeConnection()");
//			}
//		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.acceptConnections();
	}
}
