package tsoro_yematatu_rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayerInterface extends Remote {
	public void updateTurn(int bNum) throws RemoteException;
	public void updatePoints(int p0, int p1, int p2) throws RemoteException;
	public void receiveMessage(String msg) throws RemoteException;
}
