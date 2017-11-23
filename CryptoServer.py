import socket
PORT = 8888
MSGLEN = 255

class CryptoServer:
    def __init__(self):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(('', PORT))
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.run()

    def run(self):
        while True:
            self.socket.listen(5)
            client, address = self.socket.accept()
            print("{} connected".format(address))

            response = client.recv(MSGLEN)

            if response != "":
                print(response.decode("utf-8"))

            client.send(b"resp")

        print("close")
        #client.close()
        #self.socket.close()

				
	def handleClient(self){
		raise NotImplementedError;
	}

	
if __name__ == "__main__":
    server = CryptoServer()
