import socket
import threading
from JsonUtilities import *



"""
CODE:

Error: 404
Check dispo :100
Oui :200
Non:201

Si oui et wallet: 204
si oui et miner:205
si oui et RN:206
si oui et master:207
"""
PORT = 5655
MSGLEN = 2048

class Server:
	def __init__(self,port,name,type,nbrMaxWallets=4,nbrMaxMiners=10,nbrMaxRelay=10):

		# coté reseau
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.socket.bind(('', port))

		#numbre de connexion max par type
		self.nbrMaxRelay=nbrMaxRelay
		self.nbrMaxMiners=nbrMaxMiners
		self.nbrMaxWallet=nbrMaxWallets
		#conteur pour chaque type
		self.countWallet=0
		self.countMiners=0
		self.countRelay=0

		self.relayNodesList = []

		#ID serveur
		self.name=name
		self.type=type
		if (type == "Relay" or type=="RelayUpdate"): # Type Relay Node
			self.allWalletsClients=list()
			self.allMinersClients = list()

		elif (type=="Master"): #type Master Node
			self.allRelayClients = list()
		else:
			print("Mauvais type de serveur")

		#Structure de donne pour la sauvegarde des client.

		self.run()

	def run(self):
		print("Started Server "+self.type+"\nWaiting for connections...")
		self.socket.listen(5)
		while True:
			client, address = self.socket.accept()
			print("Got connection from connected {}".format(address))
			typeClient=self.receive(client)

			if (typeClient=="Wallet"): # type Wallet
				print("Got a  Wallet")
				if (self.nbrMaxWallet>self.countWallet): #si encore de la place
					self.countWallet += 1
					threading.Thread(target = self.handleClient,args=(client, address,typeClient)).start()
					#self.handleClient(client,address,typeClient)
				else:
					print("refused")
					client.send(self.message2bytes("Full"))

			elif (typeClient=="Miner"):
				print("Got a Miner")
				if (self.nbrMaxMiners>self.countMiners): #si encore de la place
					self.countMiners += 1
					threading.Thread(target = self.handleMiner,args=(client, address,typeClient)).start()
				else:
					print("refused")
					client.send(self.message2bytes("Full"))

			elif (typeClient=="Relay"):
				print("Got a Relay")
				if (self.nbrMaxRelay>self.countRelay): #si encore de la place
					self.countRelay += 1
					client.send(self.message2bytes("Paired"))
					self.relayNodesList.append(client)
					threading.Thread(target = self.handleClient,args=(client, address,typeClient)).start()
				else:
					print("refused")
					client.send(self.message2bytes("Full"))
			else:
				print("Got unknown type")
				print(typeClient)

	def updatePreviousHash(self, updatedPreviousHash):
		for relayNode in self.relayNodesList:
			#relayNode.getUpdatedPreviousHash()
			print("server")
			relayNode.send(self.message2bytes("update"))
			#relayNodes.send(self.message2bytes(updatedPreviousHash))

	def receive(self, client):
		response = client.recv(MSGLEN)
		if response != "":
			return response.decode("utf-8").rstrip("\0")

	#AVEC LE MASTER ON NE RENTRE JAMAIS ICI!
	#@abstractmethod
	def handleClient(self, client, address,type):
		print("New Thread")
		client.send(b"Paired")

		resp=self.receive(client)
		if (resp=="End"):
			print("A "+type+ " has deconnected from the server.")
			self.decrementTypeCounter(type)
			#connexion va se terminer coté Client ->thread meur ou doit mourir

		#raise NotImplementedError;
	def message2bytes(self,message):
		return (bytes(message+ "\0", 'utf-8'))

	def decrementTypeCounter(self,type):
		if (type=="Wallet"):
			self.countWallet-=1
		elif (type=="Miner"):
			self.countMiners-=1
		elif (type=="Relay"):
			self.countRelay-=1
		else:
			print("Probleme compteur c'est bagdad")