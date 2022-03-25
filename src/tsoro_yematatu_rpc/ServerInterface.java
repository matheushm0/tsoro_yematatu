package tsoro_yematatu_rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public int getPlayerID() throws RemoteException;
	public void lookupPlayer(String playerURL, int playerID) throws RemoteException;
	public void sendButtonNum(int bNum, int playerID) throws RemoteException;
	public void updatePoints(int p0, int p1, int p2, int playerID) throws RemoteException;
	public void sendMessage(String msg, int playerID) throws RemoteException;
}
