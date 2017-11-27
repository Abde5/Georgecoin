package Miner;

import Wallet.Wallet;

public class MinerMain {

    public static void main(final String[] args) {
        Miner miner = new Miner(8081,8080);
        miner.launchClient();
        miner.launchServer();
    }
}
