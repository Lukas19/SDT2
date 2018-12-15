package processpackage;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Process implements ProcessInterface{

    public String[] neighbors;
    public boolean isInitiator;
    public int received = 0;
    public String id;
    public List<String> alreadySended;

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
            try {
                LocateRegistry.createRegistry(2000);
            } catch (RemoteException e) { }
            Registry registry = LocateRegistry.getRegistry(2000);
            try {
                registry.bind(id, stub2);
            } catch (AlreadyBoundException e){
                System.out.println("Node already bound to registry \n");
            }
            System.err.println("Node " + id + " is  ready");
            if(isInitiator)
                stub2.send(id);
        } catch (RemoteException e) {
            System.out.println("Couldnt bind node to registry\n");
            e.printStackTrace();
        }

    }

    @Override
    public String send (String idOrigin) throws RemoteException {
        if (isInitiator) {
            if(!id.equals(idOrigin))
                return "Repre";
            Registry reg = LocateRegistry.getRegistry(2000);
            alreadySended = new ArrayList<>();
            for(String ids : neighbors){
                if(!alreadySended.contains(ids)) {
                    try {
                        ProcessInterface stub = (ProcessInterface) reg.lookup(ids);
                        System.out.println("Sending explorer to " + ids);
                        alreadySended.add(ids);
                        stub.send(id);
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            while(received < neighbors.length){
                continue;
            }
            System.out.println(idOrigin + ": I'm ready with responses");
            return null;
        }
        else {
            System.out.println("Explorer received from process " + idOrigin);
            String parent = idOrigin;
            received += 1;
            Registry reg = LocateRegistry.getRegistry(2000);
            ProcessInterface stub;
            alreadySended = new ArrayList<>();
            for(String ids : neighbors){
                if(!ids.equals(parent) && !alreadySended.contains(ids)){
                    try{
                        stub = (ProcessInterface) reg.lookup(ids);
                        alreadySended.add(ids);
                        System.out.println("Sending explorer to " + ids);
                        String value = stub.send(id);
                        if(value != null){
                            received += 1;
                            System.out.println("The node " + ids + " is the coordinator");
                        }
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            while(received < neighbors.length){
                continue;
            }
            try {
                stub = (ProcessInterface) reg.lookup(parent);
                System.out.println("Sending OK message to " + idOrigin);
                stub.sendOk(id, parent);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public String sendOk (String from, String to) throws RemoteException{
        if(!id.equals(to)) {
            try {
                Registry reg = LocateRegistry.getRegistry(2000);
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
        return null;
    }

}
