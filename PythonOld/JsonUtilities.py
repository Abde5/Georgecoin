import json
from datetime import date, datetime

MGSLEN = 2048

def sendJSON(sock, data):
	try:
		serialized = json.dumps(data)
	except (TypeError, ValueError) as e:
		raise Exception('You can only send JSON-serializable data')

	# send the serialized data
	sock.sendall(bytes(serialized,'utf8'))

def recvJSON(sock):
	msg = sock.recv(MGSLEN)
	try :
		if not (isinstance(msg, (bytes, bytearray))):
			msg = bytes(msg, 'utf-8')
		deserialized = json.loads(msg.decode('utf-8'))
	except (TypeError, ValueError) as e:
		raise Exception('Data received was not in JSON format')

	return deserialized

"""JSON serializer for objects not serializable by default json code"""
def json_serial(obj):
	if isinstance(obj, (datetime, date)):
		return obj.isoformat()
	raise TypeError ("Type %s not serializable" % type(obj))