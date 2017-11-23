import socket
import threading

PORT = 8888
MSGLEN = 255

class Server():
	def __init__(self, port):
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.bind(('', port))
		self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.run()

	def run(self):
		print("Started Server")
		while True:
			self.socket.listen(5)
			client, address = self.socket.accept()
			threading.Thread(target = self.handleClient,args=(client, address)).start()

			print("{} connected".format(address))

			self.receive(client)
			client.send(b"resp")

		print("close")
		#client.close()
		#self.socket.close()

	def receive(self, client):
		response = client.recv(MSGLEN)
		if response != "":
			print(response.decode("utf-8"))
		return response.decode("utf-8")
		
	#@abstractmethod
	def handleClient(self, client, address):
		raise NotImplementedError;
	
if __name__ == "__main__":
	server = Server(PORT)