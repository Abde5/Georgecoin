package RelayNode;
import Server.*;
import Client.*;

import java.util.ArrayList;

public class RelayNode {


    private String hostNameClient;
    private int portClientMaster;
    private int portServer;
    private ServerCore server;
    private Client clientMaster;
    private Client clientMiners;
    private ArrayList<String> walletConnected;
    private ArrayList<String> minerConnected;

    public RelayNode(int portServer,int portClientMaster) {

        walletConnected=new ArrayList<String>();
        minerConnected=new ArrayList<String>();
        this.portServer=portServer;
        this.portClientMaster=portClientMaster;
        server = new ServerCore(portServer);
        clientMaster = new Client("localhost",this.portClientMaster);
    }

    public void sendToMaster(String msg){
        System.out.println("OEnvoi au master");
        clientMaster.sendMessage("master",msg);
    }

    public void sendToMiners(String msg){
        clientMiners.sendMessage("miner",msg);
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public void launchClientMaster(){
        Thread threadClient = new Thread(clientMaster);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");

    }

    public void sendToALLMiners(String msg){
        for (int i=0;i<minerConnected.size();i++) {
            String[] hostInfo=minerConnected.get(i).split(":");
            launchClientMiners(hostInfo[0],Integer.parseInt(hostInfo[1]));
            sendToMiners(msg);

        }
    }
    public void launchClientMiners(String hostName,int portMiners){
        clientMiners = new Client(hostName,portMiners);
        Thread threadClient = new Thread(clientMiners);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");

    }

    public void addWallet(String clientHost){
        walletConnected.add(clientHost);
    }

    public void addMiner(String clientHost){
        minerConnected.add(clientHost);
    }

    public int getMinerNumber(){
        return minerConnected.size();
    }

}
