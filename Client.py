import socket
from datetime import date, datetime
from JsonUtilities import *
from Block import *

PORT = 5655
MSGLEN = 255

class Client:
	def __init__(self,type, sock = None):
		self.type=type

		#RN HardCoded
		self.RN=[("localhost",5655),("localhost", 5656),("localhost", 5657),("localhost", 5658),("localhost", 5659)]
		self.MN=("localhost",5655)
		if (sock == None):
			self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		else:
			self.sock = sock

	def connect(self, host, port):
		self.sock.connect((host, port))

	def send(self, message):
		total_sent = 0
		while (total_sent < MSGLEN and total_sent < len(message)):
			sent = self.sock.send(self.message2bytes(message))
			print("sent : " + str(sent))
			if (sent == 0):
				raise RuntimeError("socket connection broken")
			total_sent += sent

	def receive(self):
		return self.sock.recv(MSGLEN).decode("utf-8").rstrip("\0")

	def endConnection(self):
		print("Ending connection.")
		self.sock.send(self.message2bytes("End"))

	def connectToRelay(self):
		print("Connecting to a Relay Node ...")
		connected=False
		i=0
		while (not connected):
			self.connect(self.RN[i][0], self.RN[i][1])
			self.sock.send(self.message2bytes(self.type))
			resp = socket.receive()
			if (resp=="Paired"):
				connected=True
			elif(resp=="Full"):		#TODO:test pour plusieur RN
				print("RN"+str(i))
				i+=1
		print("Got a Relay, connection succeeded.")
		return True

	def connectToMaster(self):
		print("Connecting to the Master Node ...")
		connected = False
		while(not connected):
			self.connect(self.MN[0], self.MN[1])
			self.sock.send(self.message2bytes(self.type))
			resp = socket.receive()
			print(resp)
			if (resp == "Paired"):
				connected = True
				print("Got the Master, connection succeeded.")
		return connected

	def message2bytes(self,message):
		return (bytes(message+ "\0", 'utf-8'))

if __name__ == "__main__":

	print("Client Started")
	#Exemple pour un Wallet vers un Relay Node
	#socket = Client("Wallet");
	#socket.connectToRelay()
	#socket.send() --> Envoyer les transactions
	#socket.receive() -> recevoir un update ?? ou routine qui check tout les ...
	#socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur


	#exemple pour un Miner vers un Relay Node
	#socket = Client("Miner");
	#socket.connectToRelay()
	#socket.send() --> Envoyer les transactions
	#socket.receive() -> recevoir un update ?? ou routine qui check tout les ...
	#socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur

	#exemple pour un Relay Node vers le Master
	socket = Client("Relay");
	socket.connectToMaster()
	block = Block(1, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)
	block = json.dumps(block.__dict__) 
	socket.send("addBlock") #--> Envoyer les transactions
	sendJSON(socket.sock, block)

	response = socket.receive()# -> recevoir un update ?? ou routine qui check tout les ...
	print(response)
	socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur
