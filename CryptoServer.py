import socket
PORT = 7777

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

            response = client.recv(255)
            self.socket.sendall(bytes("resp", "utf-8"))

            if response != "":
                print(response.decode("utf-8"))
                #self.socket.send(bytes("1", 'utf-8'))
                #client.send(bytes("recieved", "utf-8"))

        print("close")
        #client.close()
        #self.socket.close()

if __name__ == "__main__":
    server = CryptoServer()
