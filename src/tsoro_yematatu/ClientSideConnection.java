package tsoro_yematatu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSideConnection {
	
	private int playerID;

	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	public ClientSideConnection() {
		System.out.println("----Client----");
		
		try {
			socket = new Socket("localhost", 51734);
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());

			setPlayerID(dataIn.readInt());
			System.out.println("Connected to server as Player #" + playerID + ".");
		}
		catch (IOException e) {
			System.out.println("IOException - ClientSideConnection()");
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
	
	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
}
