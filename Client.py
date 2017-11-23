import socket
PORT = 8888
MSGLEN = 255

class Client:
	def __init__(self, sock = None):
		if (sock == None):
			self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		else:
			self.sock = sock

	def connect(self, host, port):
		self.sock.connect((host, port))

	def send(self, message):
		total_sent = 0
		while (total_sent < MSGLEN and total_sent < len(message)):
			sent = self.sock.send(bytes(message[total_sent:] + "\0", 'utf-8'))
			print("sent : " + str(sent))
			if (sent == 0):
				raise RuntimeError("socket connection broken")
			total_sent += sent

	def receive(self):
		return self.sock.recv(MSGLEN)

if __name__ == "__main__":

	print("socket running")
	socket = CryptoSocket();

	socket.connect("localhost", PORT)
	socket.send("Miner")
	socket.send("TestBlock")

	resp = socket.receive()
	print(resp.decode("utf-8"))
