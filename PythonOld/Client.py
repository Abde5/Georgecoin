import socket
from datetime import date, datetime
from JsonUtilities import *

PORT_UPDATE=5656
PORT = 5657
MSGLEN = 2048

class Client:
	def __init__(self,type):
		self.type=type

		#RN HardCoded
		self.RN=["localhost","localhost","localhost","localhost","localhost"] # tous meme por
		self.MN=("localhost",5655)
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		

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

	def recv(self, MSGLEN=256):
		return self.sock.recv(MSGLEN).decode("utf-8").rstrip("\0")

	def recv2(self, MSGLEN=256, flag=1):
		print("flag")
		return self.sock.recv(MSGLEN, flag).decode("utf-8").rstrip("\0")

	def endConnection(self):
		print("Ending connection.")
		self.sock.send(self.message2bytes("End"))

	def connectToRelay(self):
		print("Connecting to a Relay Node ...")
		connected=False
		i=0
		while (not connected):
			self.connect(self.RN[i], PORT)
			self.sock.send(self.message2bytes(self.type))
			resp = socket.recv()
			if (resp=="Paired"):
				connected=True
			elif(resp=="Full"):		#TODO:test pour plusieur RN
				print("RN"+str(i))
				i+=1
		print("Got a Relay, connection succeeded.")
		return True

	def connectToMaster(self, client):
		print("Connecting to the Master Node ...")
		connected = False
		while(not connected):
			self.connect(self.MN[0], self.MN[1])
			self.sock.send(self.message2bytes(self.type))
			resp = client.recv()
			if (resp == "Paired"):
				connected = True
				print("Got the Master, connection succeeded.")
		return connected

	def message2bytes(self,message):
		return (bytes(message+ "\0", 'utf-8'))

if __name__ == "__main__":

#	print("Client Started")
	#Exemple pour un Wallet vers un Relay Node
	socket = Client("Wallet");
	socket.connectToRelay()
	#socket.send() --> Envoyer les transactions
	socket.recv() #-> recevoir un update ?? ou routine qui check tout les ...
	#socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur


	#exemple pour un Miner vers un Relay Node
	#socket = Client("Miner");
	#socket.connectToRelay()
	#socket.send() --> Envoyer les transactions
	#socket.receive() -> recevoir un update ?? ou routine qui check tout les ...
	#socket.endConnection() #->terminer la connexion, ca libere le thread du Serveur

	"""#exemple pour un Relay Node vers le Master
	socket = Client("Relay");
	socket.connectToMaster()

	#envoyer un block
	block = Block(1, "0", "0", json.dumps(datetime.now(), default=json_serial), 0)
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