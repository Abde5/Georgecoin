#import Block.py
import datetime as date
from Server import Server
from JsonUtilities import *

PORT = 5655

class MasterNode(Server):
	def __init__(self, port, name, type):
		Server.__init__(self, port, name, type)
		self.data = []
		self.blockchain = []
		#self.addBlock(self.create_genesis_block())

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
		blockmsg = recvJSON(client)
		print(blockmsg)
		self.informRelay(client,True)
		#check the blockchain and if the last hash = the hash client sent, add it
		#if not good, don't do anything



	def giveCopyBlockChain(self):
		#send block chain
		pass

	def updateBlockChain(self, newBlockchain):
		self.blockchain = newBlockchain

	def checkState(self):
		#self.blockchain
		pass

	def addBlock(self, newBlock):
		self.blockchain.append(newBlock)

	def informRelay(self, relayNode, isAdded):
		if (isAdded):
			relayNode.send("ok")
		else:
			relayNode.send("notok")


	def create_genesis_block(self):
		# Manually construct a block with
		# index zero and arbitrary previous hash
		return Block(0, date.datetime.now(), "Genesis Block", "0")

if __name__ == "__main__":
	master = MasterNode(PORT,"Master","Master")
