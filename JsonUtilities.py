import json
from datetime import date, datetime

MGSLEN = 256

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
        deserialized = json.loads(msg.decode('utf-8'))
    except (TypeError, ValueError) as e:
        raise Exception('Data received was not in JSON format')

    return deserialized

def json_serial(obj):
    """JSON serializer for objects not serializable by default json code"""

    if isinstance(obj, (datetime, date)):
        return obj.isoformat()
    raise TypeError ("Type %s not serializable" % type(obj))