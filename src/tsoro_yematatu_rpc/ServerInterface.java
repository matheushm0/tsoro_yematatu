package tsoro_yematatu_rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public int getPlayerID() throws RemoteException;
	public void lookupPlayer(String playerURL, int playerID) throws RemoteException;
	public void sendButtonNum(int bNum, int playerID) throws RemoteException;
}
