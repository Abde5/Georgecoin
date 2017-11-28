from Block import *
import datetime as date
from Client import Client
from Server import Server
from JsonUtilities import *
import threading
import json
PORT = 5657
PORT_UPDATE=5656

class RelayNode(Server):

	def __init__(self, port, name, type):
		self.previousHash = "0"
		self.server = None
		self.client = None
		threadServeur= threading.Thread(target = self.serverComunication())
		threadServeur.start()
		threadClient= threading.Thread(target = self.clientComunication())
		#thread.start()
		threadOthers= threading.Thread(target = self.comunicationOthers())
		#thread.start()
		#nouveau socket qui est que pour les updates

		#threadUpdate = threading.Thread(target = self.getUpdatedPreviousHash)
		#threadUpdate.setDaemon(True)
		#threadUpdate.start()
		#self.flag = True
		#getUpdatedPreviousHash()

		
		#self.serverUpdate = Server("RelayUpdate")
		#self.clientupdate.connectToMaster(self.clientupdate)
		#updateThread= threading.Thread(target = self.listenForUpdate())
		#newthread.start()
		
		#Server.__init__(self, port, name, type) W-> RN -> Miners-> RN -> MN 
		#self.client.endConnection()


	def serverComunication(self):
		self.client=Client('Relay')
		self.client.connectToMaster(self.client)
		self.serveur=Server(PORT,"RN1","Relay")
		## TODO check communicatioOthers ->> Voir George 

	def clientComunication(self):
		self.client=Client('Relay')
		self.client.connectToMaster(self.client)

	def comunicationOthers(self):
		block = Block(1, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)
		block = json.dumps(block.__dict__) 
		self.client.send("addBlock")
		response = self.client.recv()
		if (response=="ready"):
			sendJSON(self.client.sock, block)
		response = self.client.recv()# -> recevoir un update ?? ou routine qui check tout les ...
		print(response)

	def listenForUpdate(self):
		self.serverUpdate.socket.bind(('', PORT_UPDATE))
		print("hiiii")
		while True :
			print("hi")
			msg = self.clientupdate.recv()
			print(msg)
			if msg=="update":
				print("updating")
				updated = self.clientupdate.recv()
				self.previousHash = updated
			else:
				break
	
	#inutile	
	def handleClient(self, client, address):
		id = self.receive(client)
		if (id == "Wallet"):
			self.handleWallet()
		elif (id == "Miner"):
			self.handleMiner()
		else:
			raise IOError("client not recognised")
	
	def handleWallet(self):
		print("wallet!")
		while True:
			msg = self.receive(self.client)
			if len(msg) == 0:
				print("block!")

				return 
			if msg == "update":
				print("update block ! ")
				self.previousHash = updated

	
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
	#exemple pour un Relay Node vers le Master
	RelayNode(PORT, "Relay", "Relay");
	#socket.connectToMaster()

	#envoyer un block
	"""block = Block(1, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)
	block = json.dumps(block.__dict__) 
	socket.send("addBlock") #--> Envoyer les transactions
	response = socket.recv()
	if (response=="ready"):
		sendJSON(socket.sock, block)
	response = socket.recv()# -> recevoir un update ?? ou routine qui check tout les ...
	print(response)

	#demander copy blockchain
	socket.send("copy_chain")
	blockchain = recvJSON(socket)

	#envoyer un autre block
	block = Block(2, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)
	block = json.dumps(block.__dict__) 
	socket.send("addBlock") #--> Envoyer les transactions
	response = socket.recv()
	if (response=="ready"):
		sendJSON(socket.sock, block)
	response = socket.recv()# -> recevoir un update ?? ou routine qui check tout les ...
	print(response)
	
	socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur"""





