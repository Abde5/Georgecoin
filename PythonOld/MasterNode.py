from datetime import date, datetime
from copy import deepcopy
from Server import Server
from JsonUtilities import *
from Block import *
import json

PORT = 5655

class MasterNode(Server):
	def __init__(self, port, name, type):
		self.blockchain = []
		self.previous_hash = "0"
		self.addBlock(self.create_genesis_block())
		Server.__init__(self, port, name, type)

	def getBlockchain(self):
		return self.blockchain

	def handleClient(self, client, address,type):
		print("handle")
		while True:
			msg = self.receive(client)
			if len(msg) == 0:
				break
			if msg == "End":
				return
			if (msg == "addBlock"):
				client.send(b"ready")
				self.getBlock(client)
			elif (msg == "copy_chain"):
				self.giveCopyBlockChain(client)
			else:
				raise IOError("don't know what to do with this client")

	#get block
	#check if the last hash = the hash client sent, add it, send true
	#if not, send false
	def getBlock(self,client):
		print("addBlock")
		deserialized = recvJSON(client)
		#to convert it back to an object
		block = lambda:None
		block.__dict__ = json.loads(deserialized)
		if self.checkState(block):
			self.addBlock(block)
			self.informRelay(client,True)
		else:
			self.informRelay(client,False)

	#check if the previous hash = previoud hash in block
	#if true change it, and inform all relay
	def checkState(self, block):
		if self.blockchain[-1].previous_hash==block.previous_hash:
			self.previous_hash = block.hash_block
			#inform every relay the previous hash
			self.updatePreviousHash(self.previous_hash)
			return True
		return False

	def addBlock(self, newBlock):
		self.blockchain.append(newBlock)

	#inform the relay who wanted to add a block if it is added
	def informRelay(self, relayNode, isAdded):
		if (isAdded):
			relayNode.send(self.message2bytes("ok"))
		else:
			relayNode.send(self.message2bytes("notadded"))

	def giveCopyBlockChain(self, client):
		print("copy blockchain")
		blockchain_copy = deepcopy(self.blockchain)
		block_str=""
		for block in blockchain_copy:
			block_str += str(block.__dict__)
		json_string = json.dumps(block_str)
		sendJSON(client, json_string)

	def updateBlockChain(self, newBlockchain):
		self.blockchain = newBlockchain

	# Manually construct a block with
	# index zero and arbitrary previous hash
	def create_genesis_block(self):
		return Block(0, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)

if __name__ == "__main__":
	master = MasterNode(PORT,"Master","Master")
