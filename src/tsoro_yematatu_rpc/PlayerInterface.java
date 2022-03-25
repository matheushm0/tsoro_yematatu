package tsoro_yematatu_rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayerInterface extends Remote {
	public void updateTurn(int bNum) throws RemoteException;
}
