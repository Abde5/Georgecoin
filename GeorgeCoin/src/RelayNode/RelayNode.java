package RelayNode;
import Server.*;
import Client.*;

import java.util.ArrayList;

public class RelayNode {


    private String hostNameClient;
    private int portClient;
    private int portServer;
    private ServerCore server;
    private Client client;
    private ArrayList<String> transactionReceived;
    private ArrayList<String> walletConnected;
    private ArrayList<String> minerConnected;

    public RelayNode(int portServer,int portClient) {
        transactionReceived = new ArrayList<String>();
        walletConnected=new ArrayList<String>();
        minerConnected=new ArrayList<String>();
        this.portServer=portServer;
        this.portClient=portClient;
        server = new ServerCore(portServer);
        client = new Client("localhost",this.portClient);



        //threadServer.start();
        //threadClient.start();
        //threadClient.join();
        //System.out.println(client.sendTest());

        // new Thread(new ServerCore(port)).start();
        //new Thread(new Client(8081)).start();
        //ServerCore server = new ServerCore(port);

    }

    public void sendToMiners(){
        System.out.println("OKK");
        client.sendMessage("miner","RN vers Miner");
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public void launchClient(){
        System.out.print("DemarageClient");
        Thread threadClient = new Thread(client);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");

    }
    public void addTransaction(String message){
        //System.out.print(transactionReceived.size());
        transactionReceived.add(message);
        //System.out.print(transactionReceived.size());
    }

    public void addWallet(String clientHost){
        walletConnected.add(clientHost);
    }

    public void addMiner(String clientHost){
        minerConnected.add(clientHost);
    }

    public int getNumberOfTransaction(){
        //System.out.print(transactionReceived.size());
        return transactionReceived.size();
    }


}
