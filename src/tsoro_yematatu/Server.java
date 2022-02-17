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
	
	private int turnsMade;
//	private int maxTurns;
//	private int[] values;
	
	private int player1ButtonNum;
	private int player2ButtonNum;
 	
	public Server() {
		System.out.println("----Iniciando Servidor----");
		numPlayers = 0;
		turnsMade = 0;
//		maxTurns = 6;
//		values = new int[7];
		
//		for (int i = 0; i < values.length; i++) {
//			values[i] = (int) Math.ceil(Math.random() * 100);
//			System.out.println("Value #" + (i + 1) + " is " + values[i]);
			
//			values[i] = 0;
//		}
		
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
//				dataOut.writeInt(maxTurns);
//				dataOut.writeInt(values[0]);
//				dataOut.writeInt(values[1]);
//				dataOut.writeInt(values[2]);
//				dataOut.writeInt(values[3]);
//				dataOut.writeInt(values[4]);
//				dataOut.writeInt(values[5]);
//				dataOut.writeInt(values[6]);
				dataOut.flush();
				
				while (true) {
					if (playerID == 1) {
						player1ButtonNum = dataIn.readInt();
						System.out.println("Player 1 clicked button #" + player1ButtonNum);
						player2.sendButtonNum(player1ButtonNum);
					}
					else {
						player2ButtonNum = dataIn.readInt();
						System.out.println("Player 2 clicked button #" + player2ButtonNum);
						player1.sendButtonNum(player2ButtonNum);
					}
					
					turnsMade++;
					
//					//TODO
//					if (turnsMade == 10) {
//						System.out.println("Max turns has been reached");
//						break;
//					}
				}
				
				//TODO -> close connection on player win
//				player1.closeConnection();
//				player2.closeConnection();
			}
			catch (IOException e) {
				System.out.println("IOException - run() SSC");
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
		Server server = new Server();
		server.acceptConnections();
	}
}
