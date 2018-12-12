package processpackage;

import java.rmi.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Process implements ProcessInterface{

    static ProcessInterface stub2;
    public String id;
    public String[] neighbors;
    public boolean isInitiator;
    public boolean isCommited = false;
    public int n = 0;

    public Process() throws RemoteException  {

    }


    public static void main(String[] args) {

        //input
        String id = args[0];
        String[] neighbors = args[1].split(",");
        boolean isInitiator = Boolean.parseBoolean(args[2]);
        String route = args[3];
        String ip = args[4];

        try {
            Process obj = new Process();
            ProcessInterface stub2 = (ProcessInterface) UnicastRemoteObject.exportObject(obj, 0);
            System.out.println(stub2);
            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(2000);
            Registry registry = LocateRegistry.getRegistry(2000);
            registry.bind(id, stub2);

            System.err.println("Node" + id + " is  ready");
            stub2.send(neighbors, id);
        } catch (RemoteException e) {
            System.out.println("Couldnt bind node to registry\n");
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.out.println("Node already bound to registry \n");
            e.printStackTrace();
        }

    }

    @Override
    public void send (String[] vecinos, String origen, boolean iniciador) throws RemoteException {
        if (iniciador) {
            n = 0;
        }
        else {
            System.out.println("Explorer recibido de proceso" + origen);
        }
        //Investigar como  almacenar la data del proceso para poder empezar a hacer exploración.
    }

}
