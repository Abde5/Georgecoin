#import Block.py
import datetime as date
import Client
import Server

class RelayNode(Server):

	def __init__(self, port):
		Server.__init__(self, port)
		self.data = []
		self.client = self.connectToMaster()
		
	def connectToMaster(self):
		self.client = Client()
		self.client.connect("localhost", 8866)
		return self.client
		
	def handleClient(self, client, address):
		id = self.receive(client)
		if (id == "Wallet"):
			self.handleWallet()
		elif (id == "Miner"):
			self.handleMiner()
		else:
			raise IOError("client not recognised")
	
	def handleWallet(self):
		#get info from wallet
		pass
	
	def handleMiner(self):
		block = self.receive(self.client)		#get hash found by miner
		self.client.send("addBlock")
		self.client.send(block)		#send hash to MasterNode
		answer = self.receive(self.client)	#get answer from MasterNode (block added or not)
		if (answer == "ok"):		#then inform the miner about the result
			self.send("ok")			
		elif (answer == "notok"):
			self.send("notok")
		else:
			raise IOError("Answer has the wrong format")
		
		
if __name__ == "__main__":
	print("test")
	relay = RelayNode(8888)