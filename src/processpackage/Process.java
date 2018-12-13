package processpackage;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Process implements ProcessInterface{

    public String[] neighbors;
    public boolean isInitiator;
    public int received = 0;
    public String id;

    public Process(String[] neighbors, boolean isInitiator, String id) throws RemoteException  {
        this.neighbors = neighbors;
        this.isInitiator = isInitiator;
        this.id = id;
    }

    public static void main(String[] args) {
        //input
        String id = args[0];
        String[] neighbors = args[1].split(",");
        boolean isInitiator = Boolean.parseBoolean(args[2]);
        if(isInitiator) {
            String route = args[3];
            String ip = args[4];
        }

        try {
            Process obj = new Process(neighbors, isInitiator, id);
            ProcessInterface stub2 = (ProcessInterface) UnicastRemoteObject.exportObject(obj, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            try {
                registry.bind(id, stub2);
            } catch (AlreadyBoundException e){
                System.out.println("Node already bound to registry \n");
            }
            System.err.println("Node " + id + " is  ready");
            if(isInitiator){
                System.out.println("Sending something");
                stub2.send(id);
            }
        } catch (RemoteException e) {
            System.out.println("Couldnt bind node to registry\n");
            e.printStackTrace();
        }

    }

    @Override
    public void send (String idOrigin) throws RemoteException {
        if (isInitiator) {
            for(String ids : neighbors){
                try{
                    Registry reg = LocateRegistry.getRegistry();
                    ProcessInterface stub = (ProcessInterface) reg.lookup(ids);
                    stub.send(ids);

                    while(received < neighbors.length){
                        continue;
                    }
                    System.out.println(idOrigin + ": I'm ready with responses");
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("Explorer recibido de proceso " + idOrigin);
            String parent = idOrigin;
            received += 1;
            for(String ids : neighbors){
                if(!ids.equals(parent)){
                    try{
                        Registry reg = LocateRegistry.getRegistry();
                        ProcessInterface stub = (ProcessInterface) reg.lookup(ids);
                        stub.send(ids);

                        while(received < neighbors.length){
                            continue;
                        }

                        stub = (ProcessInterface) reg.lookup(parent);
                        stub.sendOk(idOrigin, parent);
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void sendOk (String from, String to) throws RemoteException{
        if(!id.equals(to)) {
            try {
                Registry reg = LocateRegistry.getRegistry();
                ProcessInterface stub = (ProcessInterface) reg.lookup(to);
                System.out.println("Sending OK to :" + to);
                stub.sendOk(from, to);

            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println(from + " Replied with Ok...");
            received += 1;
        }
    }

}
