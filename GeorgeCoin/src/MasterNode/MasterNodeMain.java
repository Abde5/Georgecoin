package MasterNode;

public class MasterNodeMain {

	/**
	 * Creates a new MasterNode, generates the first block and launchs the server
	 * @param args
	 * @throws Exception
	 */
    public static void main(final String[] args) throws Exception {
        MasterNode master = new MasterNode("localhost",8081,8080);
        master.generateFirstBlock();
        master.launchServer();

    }
}
