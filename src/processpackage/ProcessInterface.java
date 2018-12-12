package processpackage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessInterface extends Remote {
    public void send(String[] vecinos, String origen) throws RemoteException;

}
