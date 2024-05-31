import socket

# 서버의 ip, port 설정
SERVER_IP = "127.0.0.1"
SERVER_PORT =4001
ADDR = (SERVER_IP,SERVER_PORT)

# 클라이언트 소켓 설정
client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    # AF_INET : IPv4의 네트워크 주소 체계
    # SOCK_STREAM : TCP protocol로 통신하는 경우
    # SOCK_DGRAM : UDP protocol로 통신하는 경우


while 1 : 
    print("CLIENT >> ", end="")
    client_send_data = bytes(input().encode())
    client_socket.sendto(client_send_data, ADDR)
    # 클라이언트-> 서버
    server_send_data, ADDR= client_socket.recvfrom(2048)
    print("SERVER >> " +str(server_send_data.decode()))
    # 서버-> 클라이언트
    
