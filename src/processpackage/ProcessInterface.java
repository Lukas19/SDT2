package processpackage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessInterface extends Remote {
    public void send(String origen) throws RemoteException;

    public void sendOk(String from, String to) throws RemoteException;

}
