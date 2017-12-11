# GeorgeCoin

This repository has the implementation of the cryptocurrency 'GeorgeCoin' for the course INFO-F405.

## How to use

There are 4 main classes in the project:
  - `MasterNodeMain`, that will launch the Master Node services.
  - `RelayNodeMain`, that will launch the Relay Node services.
  - `MinerMain`, that will launch the miner main routine, listening to new blocks to mine.
  - `WalletMain`, that will launch the wallet, an interface to send money to other wallets.

For every main class, there is an executable jar given, which is located in the directory /dist. To launch them, we have to execute the jar in this order:
  - `java -jar MasterNode.jar`
  - `java -jar RelayNode.jar`
  - `java -jar Miner.jar`
  - `java -jar Wallet.jar`

## Implementation

### Master Node
  The master node is the node that contains the blockchain and manipulates it (updates the chain,
  creates a block from multiple received transactions). It is initialized with a single block with
  4 empty transactions.

### Relay Node
  The relay node is the intermediary between the master node and the wallets/miners. It will receive every
  transaction from the wallet and send it to the master node, as well as the mined blocks from the miners.


### Miner
  The miner is in charge of the computation of valid blocks. He applies the SHA-256 algorithm on transactions
  composing the TreeMap structure. Once the hash is obtained, he uses header information in order to obtain a 
  valid block. If this is the case, he sends a message to the relay node, who will contact the master node in 
  order to update the blockchain, reward the miner and proceed to dispatching the next block.


### Wallet
  The wallet is the interface between the user and the cryptocoins that he holds in his account (wallet). From here the user can check the blockchain and send money to other wallets. He can also check his current amount. The account related to the wallet is protected locally with AES-128. The wallets have an address derived from the AES-128 key (with RIPEMD160) that will be used for the transactions.


## Blockchain
  Contains computed blocks of 4 transactions each. Everyone can browse it, it is located in the master node, and every relay node has a local copy of the blockchain. At the beginning, the master node will generate an empty block. 
