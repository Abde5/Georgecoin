package MasterNode;

public class MasterNodeMain {

    public static void main(final String[] args) {
        MasterNode master = new MasterNode("localhost",8081,8080);
        master.generateFirstBlock();
        master.launchServer();

    }
}
