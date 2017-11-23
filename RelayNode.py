#import Block.py
import datetime as date
import CryptoSocket
from CryptoServer import CryptoServer as Server

class RelayNode(Server):
	def __init__(self, port):
	
		Server.__init__(self, port)
		self.data = []
		self.client = self.connectToMaster()
		
	def connectToMaster(self):
		client = CryptoSocket()
		client.connect("localhost", 8866)
		return client
		
	def handleClient(self, client, address):
		id = self.receive(client)
		if (id == "Wallet"):
			self.handleWallet()
		elif (id == "Miner"):
			self.handleMiner()
		else:
			raise IOError("client not recognised")
	
	def handleWallet():
		#get info from wallet
		pass
	
	def handleMiner():
		
		block = self.receive(client)		#get hash found by miner
		self.client.send("addBlock")
		self.client.send(block)		#send hash to MasterNode
		answer = self.receive(client)	#get answer from MasterNode (block added or not)
		if (answer == "ok"):		#then inform the miner about the result
			self.send("ok")			
		elif (answer == "notok"):
			self.send("notok")
		else:
			raise IOError("Answer has the wrong format")
		
		
if __name__ == "__main__":
	relay = RelayNode(8888)