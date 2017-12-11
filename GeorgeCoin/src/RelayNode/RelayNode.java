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

    /**
     * RelayNode Constructor
     * @param hostname
     * @param newPortServer
     * @param newHostnameMaster
     * @param newPortClientMaster
     */
    public RelayNode(String hostname,int newPortServer,String newHostnameMaster,int newPortClientMaster) {

        walletConnected=new ArrayList<String>();
        minerConnected=new ArrayList<String>();
        hostnameServer=hostname;
        portServer=newPortServer;
        hostNameMaster=newHostnameMaster;
        portClientMaster=newPortClientMaster;
        server = new ServerCore(hostnameServer,portServer);
        clientMaster = new Client(hostNameMaster,portClientMaster);
    }

    /**
     * Creates a string containing the information about the identity of itself
     * @return String hostnameServer:portServer;
     */
    public String WhoAMI(){
        return hostnameServer+":"+portServer;
    }
    
    /**
     * Sends msg to MasterNode
     * @param msg
     */
    public void sendToMaster(String msg){

        clientMaster.sendMessage("master",msg);
    }

    /**
     * Send msg to Miner
     * @param msg
     */
    public void sendToMiners(String msg){

        clientMiners.sendMessage("miner",msg);
    }

    /**
     * Launches the server in a thread
     */
    public void launchServer(){
        Thread threadServer = new Thread(server);
        threadServer.start();
    }

    /**
     * Launches the client communicating with the master in a thread
     */
    public void launchClientMaster(){
        Thread threadClient = new Thread(clientMaster);
        threadClient.start();
    }

    /**
     * Launches the client communicating with miners in a thread
     * @param hostName
     * @param portMiners
     */
    public void launchClientMiners(String hostName,int portMiners){
        clientMiners = new Client(hostName,portMiners);
        Thread threadClient = new Thread(clientMiners);
        threadClient.start();
    }

    /**
     * Sends an information to all the connected miners
     * @param msg
     */
    public void sendToALLMiners(String msg){
        for (int i=0;i<minerConnected.size();i++) {
            String[] hostInfo=minerConnected.get(i).split(":");
            launchClientMiners(hostInfo[0],Integer.parseInt(hostInfo[1]));
            sendToMiners(msg);

        }
    }
    
    /**
     * Asks to the MasterNode a copy of the current state of the block chain, and stores it
     */
    public void getBlockChainFromMaster(){
        System.out.println("Getting a copy of the BLOCKCHAIN.");
        launchClientMaster();
        String jsonobj=new JSONObject().put("type","GiveMeTheBlockChain").toString();
        String response=clientMaster.sendMessage("master",jsonobj);
        saveBlockChain(response);
    }
    
    /**
     * Adds a Wallet name in the connected's Wallets list
     * @param clientHost
     */
    public void addWallet(String clientHost){
        walletConnected.add(clientHost);
    }

    /**
     * Adds a Miner name in the connected's Miners list
     * @param clientHost
     */
    public void addMiner(String clientHost){
        minerConnected.add(clientHost);
    }

    /**
     * Number of connected Miners getter
     * @return int size of minerConnected array
     */
    public int getMinerNumber(){
        return minerConnected.size();
    }

    /**
     * Stores a copy of the block chain
     * @param blockchain
     */
    public void saveBlockChain(String blockchain){
        BlockChain=blockchain;
    }
    
    /**
     * Copy of the block chain getter
     * @return String BlockChain
     */
    public String getBlockChain(){
        return BlockChain;
    }

}
