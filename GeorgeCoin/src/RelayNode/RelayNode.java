package RelayNode;
import Server.*;
import Client.*;
import org.json.JSONObject;

import java.util.ArrayList;

public class RelayNode {

    private String hostnameServer;
    private String hostNameMaster;
    private int portClientMaster;
    private int portServer;
    private ServerCore server;
    private Client clientMaster;
    private Client clientMiners;
    private ArrayList<String> walletConnected;
    private ArrayList<String> minerConnected;
    private String BlockChain;

    public RelayNode(String hostname,int portServer,String hostnameMaster,int portClientMaster) {

        walletConnected=new ArrayList<String>();
        minerConnected=new ArrayList<String>();
        this.hostnameServer=hostname;
        this.portServer=portServer;
        this.hostNameMaster=hostnameMaster;
        this.portClientMaster=portClientMaster;
        server = new ServerCore(hostnameServer,this.portServer);
        clientMaster = new Client(this.hostNameMaster,this.portClientMaster);
    }

    public String WhoAMI(){
        return this.hostnameServer+":"+this.portServer;
    }
    public void sendToMaster(String msg){

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

    public void launchClientMiners(String hostName,int portMiners){
        clientMiners = new Client(hostName,portMiners);
        Thread threadClient = new Thread(clientMiners);
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
    public void getBlockChainFromMaster(){
        System.out.println("Getting a copy of the BLOCKCHAIN.");
        launchClientMaster();
        String jsonobj=new JSONObject().put("type","GiveMeTheBlockChain").toString();
        String response=clientMaster.sendMessage("master",jsonobj);
        saveBlockChain(response);
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

    public void saveBlockChain(String blockchain){
        BlockChain=blockchain;
    }
    public String getBlockChain(){
        return BlockChain;
    }

}
