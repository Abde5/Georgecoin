import json

def sendJSON(sock, data):
  try:
    serialized = json.dumps(data)
  except (TypeError, ValueError) as e:
    raise Exception('You can only send JSON-serializable data')
  # send the length of the serialized data first
  sock.send(bytes('%d\n' % len(serialized), 'utf8'))
  # send the serialized data
  sock.sendall(bytes(serialized,'utf8'))

def recvJSON(sock):
  # read the length of the data, letter by letter until we reach EOL
  length_str = ''
  char = sock.recv(1)
  while char != '\n':
    length_str += str(char)
    char = sock.recv(1)
  total = int(length_str)
  # use a memoryview to receive the data chunk by chunk efficiently
  view = memoryview(bytearray(total))
  next_offset = 0
  while total - next_offset > 0:
    recv_size = sock.recv_into(view[next_offset:], total - next_offset)
    next_offset += recv_size
  try:
    deserialized = json.loads(view.tobytes())
  except (TypeError, ValueError) as e:
    raise Exception('Data received was not in JSON format')
  return deserialized
