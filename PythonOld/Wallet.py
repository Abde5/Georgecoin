import hashlib
from Crypto.Random import random
from Crypto.PublicKey import DSA
from Crypto.Hash import SHA
from Crypto.Cipher import AES

class Wallet :

    def __init__(self, address, public_key, private_key, passphrase):
        """
        Wallet's constructor
        """
        self.address = address
        self.public_key = public_key
        self.private_key = private_key
        self.passphrase = passphrase

    #store encrypted keys
    #a password to decrypt them wil be asked when it lauches

    def create_addresses(self):
        print("new addresses")

    def send_transaction(self, destination):
        print("send transaction")

    def explore_blocks(self):
        print("explore blocks")

    def get_copy_blockchain(self):
        print("get copy of the blockchain")

    def get_address(self) :
        return self.address

    def get_public_key(self) :
        return self.public_key

    def get_private_key(self) :
        return self.address

    def get_passphrase(self) :
        return self.passphrase

if __name__ == '__main__':
    #SHA-256 to mine
    #https://docs.python.org/3/library/hashlib.html
    hash_256 = hashlib.sha256(b"Nobody inspects the spammish repetition").hexdigest()
    print(hash_256)

    #RIPEMD160 To derive addresses from the public keys
    #https://docs.python.org/3/library/hashlib.html
    publickey = b'02218AD6CDC632E7AE7D04472374311CEBBBBF0AB540D2D08C3400BB844C654231'
    h = hashlib.new('ripemd160')
    h.update(publickey)
    address = h.hexdigest()
    print(address)

    #AES-128 to encrypt private key
    #https://eli.thegreenplace.net/2010/06/25/aes-encryption-of-files-in-python-with-pycrypto
    key = '0123456789abcdef'
    IV = 16 * '\x00'  # Initialization vector: discussed later
    mode = AES.MODE_CBC
    encryptor = AES.new(key, mode, IV=IV)

    text = 'j' * 64 + 'i' * 128
    ciphertext = encryptor.encrypt(text)

    decryptor = AES.new(key, mode, IV=IV)
    plain = decryptor.decrypt(ciphertext)
    print("plain : "+plain.decode('utf-8'))

    #DSA to sign transactions
    #https://www.dlitz.net/software/pycrypto/api/current/Crypto.PublicKey.DSA-module.html
    message = "Hello"
    key = DSA.generate(1024)
    h = SHA.new(message.encode('utf-8')).digest()
    k = random.StrongRandom().randint(1,key.q-1)
    sig = key.sign(h,k)

    if key.verify(h,sig):
        print("OK")
    else:
        print("Incorrect signature")


    wallet = Wallet(address , 345, 678, "password")
    #print(wallet.get_address)


