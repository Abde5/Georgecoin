from datetime import date, datetime
from Server import Server
from JsonUtilities import *
from Block import *

PORT = 5655

class MasterNode(Server):
	def __init__(self, port, name, type):
		self.data = []
		self.blockchain = []
		self.addBlock(self.create_genesis_block())
		Server.__init__(self, port, name, type)

	def getBlockchain(self):
		return self.blockchain

	def handleClient(self, client, address,type):
		print("handle")
		msg = self.receive(client)
		print(msg)
		if (msg == "addBlock"):
			self.tryAddBlock(client)
		elif (msg == "copy_chain"):
			self.giveCopyBlockChain(client)
		else:
			raise IOError("don't know what to do with this client")


	def tryAddBlock(self,client):
		print("tryAddBlock")
		#blockmsg = self.receive(client)
		block = recvJSON(client)
		print(block)
		self.blockchain.append(block)
		self.informRelay(client,True)
		#check the blockchain and if the last hash = the hash client sent, add it
		#if not good, don't do anything



	def giveCopyBlockChain(self, client):
		print("copy blockchain")

	def updateBlockChain(self, newBlockchain):
		self.blockchain = newBlockchain

	def checkState(self):
		#self.blockchain
		pass

	def addBlock(self, newBlock):
		self.blockchain.append(newBlock)

	def informRelay(self, relayNode, isAdded):
		if (isAdded):
			relayNode.send(self.message2bytes("ok"))
		else:
			relayNode.send(self.message2bytes("notok"))


	def create_genesis_block(self):
		# Manually construct a block with
		# index zero and arbitrary previous hash
		return Block(0, "0", "0", datetime.now(), 0)

if __name__ == "__main__":
	master = MasterNode(PORT,"Master","Master")
