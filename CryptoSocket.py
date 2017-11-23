import socket
PORT = 7777
MSGLEN = 255

class CryptoSocket:
    def __init__(self, sock = None):
        if (sock == None):
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        else:
            self.sock = sock

    def connect(self, host, port):
        self.sock.connect((host, port))

    def send(self, message):
        total_sent = 0
        while (total_sent < MSGLEN):
            sent = self.sock.send(bytes(message[total_sent:], 'utf-8'))
            if (sent == 0):
                raise RuntimeError("socket connection broken")
            total_sent += sent

    def receive(self):
        chunks = []
        bytes_rcvd = 0
        while (bytes_rcvd < MSGLEN):
            chunk = self.sock.recv(min(MSGLEN - bytes_rcvd, 2048))
            if(chunk == b''):
                raise RuntimeError("socket connection broken")
            chunks.append(chunk)
            bytes_rcvd += len(chunk)
        return b''.join(chunks)

if __name__ == "__main__":
    print("socket running")
    socket = CryptoSocket();

    socket.connect("localhost", PORT)
    socket.send("test")

    #resp = socket.receive()
    #print(resp.decode("utf-8"))
