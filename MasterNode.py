import Block.py
import datetime as date

class MasterNode(CryptoServer):
    def __init__(self):
        self.data = []
        self.blockchain = []

    def getBlockchain(self):
        return self.blockchain

    def updateBlockChain(self, newBlockchain):
        self.blockchain = newBlockchain

    def checkState(self):
        #self.blockchain
        pass

    def addBlock(self, newBlock):
        self.blockchain.append(newBlock)

    def informRelay(self, relayNode):
        pass

    def create_genesis_block(self):
        # Manually construct a block with
        # index zero and arbitrary previous hash
        return Block(0, date.datetime.now(), "Genesis Block", "0")