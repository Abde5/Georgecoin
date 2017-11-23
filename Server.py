import socket
import threading



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
MSGLEN = 255

class Server():
	def __init__(self, port,name,type,nbrMaxWallets=4,nbrMaxMiners=10,nbrMaxRelay=10):

		# coté reseau
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.bind(('', port))
		self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

		#numbre de connexion max par type
		self.nbrMaxRelay=nbrMaxRelay
		self.nbrMaxMiners=nbrMaxMiners
		self.nbrMaxWallet=nbrMaxWallets
		#conteur pour chaque type
		self.countWallet=0
		self.countMiners=0
		self.countRelay=0

		#ID serveur
		self.name=name
		self.type=type
		if (type == "Relay"): # Type Relay Node
			self.allWalletsClients=list()
			self.allMinersClients = list()
			self.runRelay()

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
				else:
					print("refused")
					client.send(self.message2bytes("Full"))

			elif (typeClient=="Miner"):
				print("Got a Miner")
				if (self.nbrMaxMiners>self.countMiners): #si encore de la place
					self.countMiners += 1
					threading.Thread(target = self.handleClient,args=(client, address,typeClient)).start()
				else:
					print("refused")
					client.send(self.message2bytes("Full"))

			elif (typeClient=="Relay"):
				print("Got a Relay")
				if (self.nbrMaxRelay>self.countRelay): #si encore de la place
					self.countRelay += 1
					threading.Thread(target = self.handleClient,args=(client, address,typeClient)).start()
				else:
					print("refused")
					client.send(self.message2bytes("Full"))
			else:
				print("Got unknown type")
				print(typeClient)

			#



	def receive(self, client):
		response = client.recv(MSGLEN)
		if response != "":
			#print(response.decode("utf-8").rstrip("\0"))
			return response.decode("utf-8").rstrip("\0")

	#@abstractmethod
	def handleClient(self, client, address,type):
		print("New Thread")
		#self.receive(client)
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
			print("Probleme Conteur c'est bagdad")






if __name__ == "__main__":

	# Exemple Relay
	server = Server(PORT,"RN1","Master")