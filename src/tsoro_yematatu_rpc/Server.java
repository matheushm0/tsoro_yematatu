package tsoro_yematatu_rpc;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;
	
	private int numPlayers;
	private PlayerInterface player1;
	private PlayerInterface player2;
	
	public Server() throws RemoteException {
		super();
		
		System.out.println("----Starting Server----");
		numPlayers = 0;
	}

	@Override
	public int getPlayerID() {		
		if (numPlayers < 2) {
			numPlayers++;
			
			System.out.println("Player #" + numPlayers + " has connected!");
			
			if (numPlayers == 2) {
				System.out.println("There are 2 players connected. No more connections accepted.");
			}
		}
		
		return numPlayers;
	}
	

	@Override
	public void lookupPlayer(String playerURL, int playerID) throws RemoteException {
		try {
			if (playerID == 1) {
				player1 = (PlayerInterface) Naming.lookup(playerURL);
			}
			
			if (playerID == 2) {
				player2 = (PlayerInterface) Naming.lookup(playerURL);
			}	
		}
		catch (Exception e) {
			//TODO
			System.out.println("Exception: " + e.getMessage());
		}
	}

	@Override
	public void sendButtonNum(int bNum, int playerID) throws RemoteException {
		if (playerID == 1) {
			player2.updateTurn(bNum);
		}
		
		if (playerID == 2) {
			player1.updateTurn(bNum);
		}
	}
	
	public static void main(String[] args) {

		try {			
			Server server = new Server();
			Naming.rebind("//localhost/ServerRef", server);
			
			System.out.println("Waiting Connections...");
		}
		catch (Exception e) {
			//TODO
			System.out.println("Exception: " + e.getMessage());
		}
		
	}
}
