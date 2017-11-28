package Miner;

import Wallet.Wallet;

public class MinerMain {

    public static void main(final String[] args) {
        Miner miner = new Miner("localhost",8082);
        miner.connectToRelay();
        miner.launchServer();
    }
}
