import socket

# 접속 정보 설정
SERVER_IP = '127.0.0.1'
SERVER_PORT = 8081
SIZE = 1024
SERVER_ADDR = (SERVER_IP, SERVER_PORT)
# 서버의 ip, port 설정

# 클라이언트 소켓 설정
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # AF_INET : IPv4의 네트워크 주소 체계
    # SOCK_STREAM : TCP protocol로 통신하는 경우
    # SOCK_DGRAM : UDP protocol로 통신하는 경우
client_socket.connect(SERVER_ADDR)  
    # 127.0.0.1 서버에 접속 
       
while True :

    print("CLIENT >> ", end ="")
    client_send_data = bytes(input().encode())
    client_socket.send(client_send_data) 
    # client -> server 송신
    server_send_data = client_socket.recv(SIZE).decode() 
    # server -> client 송신
    # 서버로부터 온 메시지를 server_send_data에 담아서 server >> 에 출력함

    print("SERVER >> " +str(server_send_data))